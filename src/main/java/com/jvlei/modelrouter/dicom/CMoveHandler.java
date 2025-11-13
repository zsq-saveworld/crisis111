package com.jvlei.modelrouter.dicom;

import cn.hutool.core.io.FileUtil;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.ReceivedObjectHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class CMoveHandler extends ReceivedObjectHandler {

    public Runnable exe;

    private String finalPath;

    public CMoveHandler(Runnable exe) {
        this.exe = exe;
    }

    public CMoveHandler(String finalPath) {
        this.finalPath = finalPath;
    }

    @Override
    public void sendReceivedObjectIndication(String filename, String transferSyntax, String calledAetTitle)
            throws DicomNetworkException, DicomException, IOException {

        log.info("Incoming data from " + calledAetTitle + "...");
        log.info("filename:" + filename);
        log.info("transferSyntax:" + transferSyntax);

        if (exe != null) {
            exe.run();
        }

        transfer(filename, transferSyntax, calledAetTitle);

    }

    private void transfer(String filename, String transferSyntax, String calledAetTitle) {
        /*try {
            // 将文件迁移到另一个文件夹 - 解析 studyId
            Path src = Paths.get(filename);
            DicomInputStream item = new DicomInputStream(Files.newInputStream(src));
            AttributeList list = new AttributeList();
            list.read(item);
            // 获取影像号
            String accnum = getAttr(list, TagFromName.AccessionNumber);
            String studyDate = getAttr(list, TagFromName.StudyDate);
            // Year-Month-Day-Accnum
            String year = studyDate.substring(0, 4);
            String month = studyDate.substring(4, 6);
            String day = studyDate.substring(6, 8);
            String filePath = finalPath + File.separator + year + File.separator + month + File.separator + day + File.separator + accnum;
            if (!FileUtil.exist(filePath)) {
                try {
                    FileUtil.mkdir(filePath);
                } catch (Exception e) {
                    // if concurrency, maybe exist by another thread.
                    log.warn("failed to create dir: {}", filePath, e);
                }
            }
            String name = filename.substring(filename.lastIndexOf(File.separator) + 1);

            // 将文件移动到指定目录
            String destName = filePath + File.separator + name;
            log.info("start to copy file, filename: {}, destFile: {}", filename, destName);
            try {
                FileUtil.move(src, Paths.get(destName), true);
            } catch (Exception e) {
                log.warn("failed to copy file, filename: {}, destName: {}", filename, destName, e);
            }
        } catch (Exception e) {
            log.warn("failed to receive dicom files, fileName: {}, transferSyntax: {}, calledAET: {}", filename, transferSyntax, calledAetTitle, e);
        }*/

        try {
            // 将文件迁移到另一个文件夹 - 解析 studyId
            Path src = Paths.get(filename);
            DicomInputStream item = new DicomInputStream(Files.newInputStream(src));
            AttributeList list = new AttributeList();
            list.read(item);
            // 获取影像号
            String accnum = getAttr(list, TagFromName.AccessionNumber);
            String studyDate = getAttr(list, TagFromName.StudyDate);
            String filePath = finalPath + File.separator + studyDate + File.separator + accnum + File.separator + "origin";
            if (!FileUtil.exist(filePath)) {
                try {
                    FileUtil.mkdir(filePath);
                } catch (Exception e) {
                    // if concurrency, maybe exist by another thread.
                    log.warn("failed to create dir: {}", filePath, e);
                }
            }
            String name = filename.substring(filename.lastIndexOf(File.separator) + 1);
            // 将文件移动到指定目录
            String destName = filePath + File.separator + name;
            log.info("start to copy file, filename: {}, destFile: {}", filename, destName);
            try {
                FileUtil.move(src, Paths.get(destName), true);
            } catch (Exception e) {
                log.warn("failed to copy file, filename: {}, destName: {}", filename, destName, e);
            }
        } catch (Exception e) {
            log.warn("failed to receive dicom files, fileName: {}, transferSyntax: {}, calledAET: {}", filename, transferSyntax, calledAetTitle, e);
        }
    }

    public static String getAttr(AttributeList list, AttributeTag tag) {
        return Attribute.getDelimitedStringValuesOrEmptyString(list, tag);
    }

}
