package com.jvlei.modelrouter.customimpl;

import com.jvlei.modelrouter.generic.GenericRequest;
import com.jvlei.modelrouter.generic.GenericResponse;
import com.jvlei.modelrouter.generic.GenericModelService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 文本模型服务实现示例
 */
@Service
public class TextModelService implements GenericModelService<String, String> {

    private final String serviceId = "text-model-v1";
    private final Map<String, Object> metadata;

    public TextModelService() {
        metadata = new HashMap<>();
        metadata.put("name", "Text Classification Model");
        metadata.put("version", "1.0");
        metadata.put("performance", 0.92);
        metadata.put("supportedTypes", new String[]{"text", "document"});
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
    public GenericResponse<String, String> predict(GenericRequest<String> request) {
        // 实际应用中这里会调用真实的模型服务
        String result = "Processed: " + request.getData().substring(0, Math.min(20, request.getData().length())) + "...";
        return GenericResponse.success(request.getRequestId(), request.getData(), result);
    }

    @Override
    public boolean supports(String requestType) {
        return "text".equals(requestType) || "document".equals(requestType);
    }
}
