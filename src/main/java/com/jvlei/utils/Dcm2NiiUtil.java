package com.jvlei.utils;

import cn.hutool.core.io.FileUtil;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.io.DicomInputStream;
import org.itk.simple.Image;
import org.itk.simple.ImageFileWriter;
import org.itk.simple.ImageSeriesReader;
import org.itk.simple.PixelIDValueEnum;
import org.itk.simple.VectorDouble;
import org.itk.simple.VectorString;
import org.itk.simple.VectorUInt32;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Dcm2NiiUtil {

    public static void main(String[] args) {
        // 输入：DICOM序列文件夹路径
        String dicomDir = "path/to/dicom/folder";
        // 输出：NIfTI文件路径（.nii.gz）
        String outputNiiPath = "output.nii.gz";

        try {
            convertDcmToNii(dicomDir, outputNiiPath);
            System.out.println("转换成功：" + outputNiiPath);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("转换失败：" + e.getMessage());
        }
    }

    /**
     * 将DICOM序列转换为NIfTI文件
     *
     * @param dicomDir   DICOM文件所在文件夹
     * @param outputPath 输出.nii.gz文件路径
     */
    public static void convertDcmToNii(String dicomDir, String outputPath) throws Exception {
        // 1. 读取并排序DICOM文件（按切片位置排序）
        List<File> dicomFiles = getSortedDicomFiles(dicomDir);
        if (dicomFiles.isEmpty()) {
            throw new IllegalArgumentException("DICOM文件夹为空：" + dicomDir);
        }

        // 2. 从第一个DICOM文件获取元数据（维度、间距等）
        Attributes firstDcmAttrs = readDicomAttributes(dicomFiles.get(0));
        int rows = firstDcmAttrs.getInt(Tag.Rows, 0);
        int cols = firstDcmAttrs.getInt(Tag.Columns, 0);
        int depth = dicomFiles.size(); // 切片数量（三维深度）

        // 获取像素间距（x, y, z）
        float[] spacing = getSpacing(firstDcmAttrs, dicomFiles);
        // 获取原点坐标（x, y, z）
        float[] origin = getOrigin(firstDcmAttrs);

        // 3. 读取所有DICOM的像素数据，构建三维数组
        short[][][] pixelData = new short[depth][rows][cols];
        for (int z = 0; z < depth; z++) {
            File dcmFile = dicomFiles.get(z);
            BufferedImage image = readDicomImage(dcmFile);
            // 提取像素值（注意DICOM可能是16位灰度图）
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    int pixel = image.getRGB(x, y);
                    // 转换为16位有符号整数（DICOM常用存储格式）
                    pixelData[z][y][x] = (short) ((pixel >> 8) & 0xFFFF);
                }
            }
        }

        // 4. 使用SimpleITK创建NIfTI图像
        Image image = createNiftiImage(pixelData, rows, cols, depth, spacing, origin);

        // 5. 保存为.nii.gz（自动压缩）
        ImageFileWriter writer = new ImageFileWriter();
        writer.setFileName(outputPath);
        writer.execute(image);
    }

    /**
     * 读取DICOM文件夹并按切片位置排序
     */
    private static List<File> getSortedDicomFiles(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles((f) -> f.getName().endsWith(".dcm"));
        if (files == null) {
            return Collections.emptyList();
        }

        List<File> dicomFiles = new ArrayList<>();
        for (File f : files) {
            dicomFiles.add(f);
        }

        // 按DICOM的Slice Location（切片位置）排序
        dicomFiles.sort(Comparator.comparingDouble(f -> {
            try {
                Attributes attrs = readDicomAttributes(f);
                return attrs.getDouble(Tag.SliceLocation, 0);
            } catch (Exception e) {
                return 0;
            }
        }));
        return dicomFiles;
    }

    /**
     * 读取DICOM文件的元数据
     */
    private static Attributes readDicomAttributes(File dcmFile) throws Exception {
        try (DicomInputStream dis = new DicomInputStream(dcmFile)) {
            return dis.readDataset();
        }
    }

    /**
     * 读取DICOM文件的像素图像
     */
    private static BufferedImage readDicomImage(File dcmFile) throws Exception {
        try (ImageInputStream iis = ImageIO.createImageInputStream(new FileInputStream(dcmFile))) {
            ImageReader reader = ImageIO.getImageReadersByFormatName("DICOM").next();
            reader.setInput(iis);
            return reader.read(0); // 读取第一帧（单帧DICOM）
        }
    }

    /**
     * 获取像素间距（x, y, z） x/y间距从第一个DICOM获取，z间距通过相邻切片位置计算
     */
    private static float[] getSpacing(Attributes firstAttrs, List<File> dicomFiles) throws Exception {
        // 像素间距（x, y）：DICOM标签(0028,0030)
        String[] pixelSpacing = firstAttrs.getString(Tag.PixelSpacing).split("\\\\");
        float spacingX = Float.parseFloat(pixelSpacing[0]);
        float spacingY = Float.parseFloat(pixelSpacing[1]);

        // 切片间距（z）：通过相邻两个切片的位置差计算
        float spacingZ = 0;
        if (dicomFiles.size() >= 2) {
            Attributes secondAttrs = readDicomAttributes(dicomFiles.get(1));
            double slice1 = firstAttrs.getDouble(Tag.SliceLocation, 0);
            double slice2 = secondAttrs.getDouble(Tag.SliceLocation, 0);
            spacingZ = (float) Math.abs(slice2 - slice1);
        }

        return new float[] {spacingX, spacingY, spacingZ};
    }

    /**
     * 获取原点坐标（x, y, z） 原点为DICOM的Image Position (Patient)
     */
    private static float[] getOrigin(Attributes attrs) {
        String[] imagePosition = attrs.getString(Tag.ImagePositionPatient).split("\\\\");
        float originX = Float.parseFloat(imagePosition[0]);
        float originY = Float.parseFloat(imagePosition[1]);
        float originZ = Float.parseFloat(imagePosition[2]);
        return new float[] {originX, originY, originZ};
    }

    /**
     * 使用SimpleITK创建NIfTI图像
     */
    private static Image createNiftiImage(short[][][] pixelData, int rows, int cols, int depth, float[] spacing, float[] origin) {

        // 定义图像尺寸（x, y, z）
        long[] size = {cols, rows, depth};
        // 图像类型：16位有符号整数（与DICOM像素格式匹配）
        VectorUInt32 vUintSize = new VectorUInt32(size);
        Image image = new Image(vUintSize, PixelIDValueEnum.sitkInt16);

        // 设置像素间距
        image.setSpacing(new VectorDouble(float2double(spacing)));
        // 设置原点坐标
        image.setOrigin(new VectorDouble(float2double(origin)));

        // 将三维数组数据写入SimpleITK图像
        /*for (int z = 0; z < depth; z++) {
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    image.setPixelAsInt64(new long[]{x, y, z}, pixelData[z][y][x]);
                }
            }
        }*/

        return image;
    }

    public static double[] float2double(float[] primitiveArray) {
        if (primitiveArray == null) {
            return null;
        }
        Float[] wrapperArray = new Float[primitiveArray.length];
        for (int i = 0; i < primitiveArray.length; i++) {
            // 自动装箱：float -> Float
            wrapperArray[i] = primitiveArray[i];
        }
        return Arrays.stream(wrapperArray).mapToDouble(Float::floatValue).toArray();
    }

    /**
     * 将DICOM序列转换为.nii.gz格式
     *
     * @param dicomDir   DICOM文件所在文件夹
     * @param outputPath 输出.nii.gz文件路径
     */
    public static void convertDicomToNiiGz(String dicomDir, String outputPath) {
        // 1. 创建DICOM序列读取器
        ImageSeriesReader reader = new ImageSeriesReader();

        // 2. 获取文件夹中所有DICOM文件的路径（SimpleITK自动筛选.dcm文件）
        List<String> dicomFileNames = reader.getGDCMSeriesFileNames(dicomDir);
        if (dicomFileNames.isEmpty()) {
            throw new RuntimeException("DICOM文件夹中未找到任何DICOM文件：" + dicomDir);
        }

        // 3. 设置读取的文件列表
        List<String> fileNames = new ArrayList<>();

        for (String s : FileUtil.listFileNames(dicomDir)) {
            fileNames.add(dicomDir + File.separator + s);
        }

        VectorString fileNamesV = new VectorString(fileNames);

        reader.setFileNames(fileNamesV);

        // 4. 读取DICOM序列为3D图像（自动处理切片排序、元数据整合） todo: lossless
        Image image = reader.execute();

        // 5. 创建写入器，保存为.nii.gz（路径以.gz结尾时自动启用压缩）
        ImageFileWriter writer = new ImageFileWriter();
        writer.setFileName(outputPath);
        // 显式指定NIfTI格式IO（可选，通常自动识别）
        writer.setImageIO("NiftiImageIO");
        writer.execute(image);
    }

}
