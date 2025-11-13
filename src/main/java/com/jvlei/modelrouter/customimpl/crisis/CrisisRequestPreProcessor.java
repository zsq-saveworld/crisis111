package com.jvlei.modelrouter.customimpl.crisis;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.jvlei.datadispatch.model.dto.ExamInfo;
import com.jvlei.modelrouter.dicom.DicomConfig;
import com.jvlei.modelrouter.generic.GenericRequest;
import com.jvlei.modelrouter.generic.RequestPreProcessor;
import com.jvlei.modelrouter.generic.SeriesSelector;
import com.jvlei.utils.Dcm2NiiUtil;
import com.jvlei.utils.DicomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;

import static com.jvlei.modelrouter.BuzConstant.BUZ_CRISIS_PREDICT;

@Component
@Slf4j
public class CrisisRequestPreProcessor implements RequestPreProcessor<ExamInfo> {

    @Value("${jvlei.ai.crisis.inputFileRoot:d:/testpage/TECHTEST/crisis}")
    private String defaultCrisisInputFileDir;

    @Resource
    private SeriesSelector seriesSelector;
    @Resource
    private DicomConfig dicomConfig;

    @Override
    public GenericRequest<ExamInfo> process(GenericRequest<ExamInfo> request) {
        log.info("start to process pre request ==");
        // 获取指定的序列名称
        String seriesDescription = seriesSelector.select(request.getData(), request.getType());
        if (!StringUtils.hasText(seriesDescription)) {
            request.getData().setCanDo(false);
            request.getData().setErrMsg("series is null");
            return request;
        }
        // 拉取影像
        DicomUtil.getSeriesImg(request.getData().getAccnum(),
                dicomConfig.getScpAe(), dicomConfig.getScpPort(), dicomConfig.getScpIp(),
                dicomConfig.getLocalAe(), request.getData().getPatientId(), seriesDescription);
        // get remote store server info. todo:
        String seriesStorePath =
                defaultCrisisInputFileDir + File.separator + DateUtil.format(request.getData().getExamDateTime(), DatePattern.PURE_DATE_PATTERN) + File.separator
                        + request.getData().getAccnum();
        // 异步线程，store to ai-pacs todo:

        // 转换为 nii.gz
        String inputFile = seriesStorePath + File.separator + "_0000.nii.gz";
        Dcm2NiiUtil.convertDicomToNiiGz(seriesStorePath + File.separator + "origin", inputFile);

        // 设置 inputFile outputDir, 交给模型服务处理
        request.getData().setInputFile(inputFile);
        request.getData().setOutputDir(seriesStorePath);

        return request;
    }

    @Override
    public String interest() {
        return BUZ_CRISIS_PREDICT;
    }
}
