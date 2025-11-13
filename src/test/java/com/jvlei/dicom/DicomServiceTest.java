package com.jvlei.dicom;

import com.jvlei.utils.DicomUtil;
import com.jvlei.modelrouter.dicom.DicomConfig;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class DicomServiceTest {


    private String remoteAe;

    private String remoteIp;

    private int remotePort;

    private String localAe;

    private String accessionNum;

    private String testPatientId;

    @BeforeEach
    public void init() {
        remoteAe = "dcmqrscp";
        remoteIp = "192.168.112.74";
        remotePort = 11110;
        localAe = "localAe";
        testPatientId = "1010401039";
        accessionNum = "125092805646";
    }

    @Test
    public void testGetSeries() {
        /// 通过 pacs 拉取影像的流程

        // 1. 接收完成消息
        // 2. 根据工作站获取放射设备 AE
        // 4. 根据检查项目匹配AI服务
        // 5. 服务中拉取影像数据（根据影像号获取 studyIuid 和设备AE， 根据设备AE获取配置的seriesDescription, 根据 studyIuid 和 seriesDescription 获取 序列 seriesIuid, 根据 seriesIuid 获取
        // 影像文件）
        // 6. 根据影像文件进行推理，并保存推理结果
        // 7. 若有异常则发出风险预警消息
        // 8. 通知到医技工作站（作为异常消息通知）
        // 9. 通知到短信
        // 10. 通知到客户端

        ///  通过 设备 直连
        // 0. 设备做完检查，影像直接传输到 AI-PACS
        // 1. AI-PACS 根据影像号查检查项目信息，匹配是否需要做 AI 预测
        // 2. 服务中拉取影像数据（根据影像号获取 studyIuid 和设备AE， 根据设备AE获取配置的seriesDescription, 根据 studyIuid 和 seriesDescription 获取 序列 seriesIuid, 根据 seriesIuid 获取影像文件）
        // 3. 根据影像文件进行推理，并保存推理结果
        // 4. 若有异常则发出风险预警消息
        // 5. 通知到医技工作站（作为异常消息通知）
        // 6. 通知到短信
        // 7. 通知到客户端

        init();

        DicomConfig config = new DicomConfig();
        // 假设 DicomConfig 上已添加 @Data 注解，可直接设置属性
        config.setScpAe("dcmqrscp");
        config.setScpIp("192.168.112.96");
        config.setScpPort(11110);
        config.setLocalAe("localAe");
        config.setLocalIp("127.0.0.1");
        config.setLocalPort(2222);
        config.setStorePath("d:/dicomtest/");

        DicomUtil.getStudy(remoteAe, remotePort, remoteIp, localAe, accessionNum, testPatientId, (list) -> {
            String studyInstanceUID = list.get(TagFromName.StudyInstanceUID).getSingleStringValueOrEmptyString();
            Set<String> setofSopClassesExpected = new HashSet<String>();
            Attribute sopClassesInStudy = list.get(TagFromName.SOPClassesInStudy);
            if (sopClassesInStudy != null) {
                String[] sopClassesInStudyList = null;
                try {
                    sopClassesInStudyList = sopClassesInStudy.getStringValues();
                } catch (DicomException e) {
                    // do nothing.
                }
                for (String sopClassInStudy : sopClassesInStudyList) {
                    setofSopClassesExpected.add(sopClassInStudy);
                }
            } else {
                //if SOP class data for study is not found, then supply all storage SOP classes
                //todo: 无法从DICOM中获取到检查类别以及相关的SOPClassUID 暂只支持CT,MR,CR,RF
                setofSopClassesExpected.add("1.2.840.10008.5.1.4.1.1.2");
            }

            // "1.2.840.113619.2.55.3.279723093.332.1756200895.963"
            String seriesDescription = "5mm Stnd C+ Recon"; /// 5mm Lung Recon
            DicomUtil.getSeries(studyInstanceUID, remoteAe, remotePort, remoteIp, localAe, accessionNum, "01007717", seriesDescription, (item) -> {
                String seriesIuid = "";
                seriesIuid = item.get(TagFromName.SeriesInstanceUID).getSingleStringValueOrEmptyString();
                log.info("start to move series image, seriesIuid: {}", seriesIuid);
                DicomUtil.moveImageBySeries(remoteAe, remotePort, remoteIp, localAe, DicomUtil.storeScpAeTitle, studyInstanceUID, seriesIuid);
            });

            // DicomUtil.getImage("dcmqrscp", 11110, "192.168.112.96", "localAe", "d:/dicomtest/", studyInstanceUID, accessionNum,
            // setofSopClassesExpected);
        });
    }

}
