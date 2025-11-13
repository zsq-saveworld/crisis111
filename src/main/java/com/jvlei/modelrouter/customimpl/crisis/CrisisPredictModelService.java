package com.jvlei.modelrouter.customimpl.crisis;

import cn.hutool.core.io.FileUtil;
import com.jvlei.config.Sys;
import com.jvlei.datadispatch.model.dto.ExamInfo;
import com.jvlei.modelrouter.generic.GenericModelService;
import com.jvlei.modelrouter.generic.GenericRequest;
import com.jvlei.modelrouter.generic.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static com.jvlei.modelrouter.BuzConstant.BUZ_CRISIS_PREDICT;

/**
 * 文本模型服务实现示例
 */
@Service
@Slf4j
public class CrisisPredictModelService implements GenericModelService<ExamInfo, String> {

    private final String serviceId = BUZ_CRISIS_PREDICT;
    private final Map<String, Object> metadata;

    @Value("${jvlei.ai.crisis.svcUrl:}")
    private String crisisSvcUrl;

    @Value("${jvlei.ai.crisis.debugResultFilePath:}")
    private String debugResultFilePath;

    @Resource
    private RestTemplate restTemplate;

    public CrisisPredictModelService() {
        metadata = new HashMap<>();
        metadata.put("name", BUZ_CRISIS_PREDICT);
        metadata.put("version", "1.0");
        metadata.put("performance", 0.92);
        metadata.put("supportedTypes", new String[]{BUZ_CRISIS_PREDICT});
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public GenericResponse<ExamInfo, String> predict(GenericRequest<ExamInfo> request) {
        if (!request.getData().isCanDo()) {
            log.warn("exam can't execute crisis predict, accnum: {} errMsg: {}", request.getData().getAccnum(), request.getData().getErrMsg());
            return GenericResponse.error(request.getRequestId(), request.getData().getErrMsg());
        }
        // todo: How to design to make it more versatile, artistically sound in design, and architectural.
        request.getData().setErrMsg(null);
        request.getData().setCanDo(true);


        // 实际应用中这里会调用真实的模型服务 // 请求服务, 后续可以考虑 onnx runtime / tensor-rt 直接本地运行模型服务 todo:
        String result;
        if (Sys.DEBUG) {
            result = FileUtil.readString(debugResultFilePath, Charset.defaultCharset());
        } else {
            result = aiSvc(request.getData().getAccnum(), request.getData().getInputFile(), request.getData().getOutputDir());
        }

        return GenericResponse.success(request.getRequestId(), request.getData(), result);
    }

    @Override
    public boolean supports(String requestType) {
        return BUZ_CRISIS_PREDICT.equals(requestType);
    }

    private String aiSvc(String accnum, String inputFile, String outputDir) {
        // 1. 请求头：multipart/form-data（文件上传必须）
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 2. 构造 form-data（包含文件和普通字段）
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        // 普通文本字段
        formData.add("version", "1.1");
        formData.add("modality", "BrainCT");
        formData.add("fpSrcImg", inputFile);
        formData.add("fpDstFolder", outputDir);
        // 3. 封装请求实体
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);

        // 4. 发送 POST 请求
        ResponseEntity<String> response = restTemplate.postForEntity(crisisSvcUrl, requestEntity, String.class);
        if (HttpStatus.OK.value() != response.getStatusCodeValue()) {
            log.warn("failed to predict crisis result, accnum: {}, inputFile: {}, code: {}", accnum, inputFile, response.getStatusCode());
            return null;
        }
        return response.getBody();
    }
}

