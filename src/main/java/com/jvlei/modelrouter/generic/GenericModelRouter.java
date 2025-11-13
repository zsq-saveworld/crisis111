package com.jvlei.modelrouter.generic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 泛型模型路由核心类，协调整个业务流程
 *
 * @param <T> 请求数据类型
 * @param <R> 响应数据类型
 */
@Component
@Slf4j
public class GenericModelRouter<T, R> {

    private final ModelMatcher<T, R> modelMatcher;

    private final ModelDispatcher<T, R> modelDispatcher;

    private final RequestValidator<T> requestValidator;

    private final List<RequestPreProcessor<T>> preProcessors;

    private final List<ResponsePostProcessor<T, R>> postProcessors;

    private final List<GenericModelService<T, R>> modelServices;

    @Autowired
    public GenericModelRouter(ModelMatcher<T, R> modelMatcher, ModelDispatcher<T, R> modelDispatcher, RequestValidator<T> requestValidator,
            List<RequestPreProcessor<T>> preProcessors, List<ResponsePostProcessor<T, R>> postProcessors,
            List<GenericModelService<T, R>> modelServices) {
        this.modelMatcher = modelMatcher;
        this.modelDispatcher = modelDispatcher;
        this.requestValidator = requestValidator;
        this.preProcessors = preProcessors;
        this.postProcessors = postProcessors;
        this.modelServices = modelServices;
    }

    /**
     * 处理请求的核心方法，实现完整业务流程
     */
    public GenericResponse<T, R> route(GenericRequest<T> request) {
        try {
            // 1. 检查信息
            String validationError = requestValidator.validate(request);
            if (validationError != null) {
                return GenericResponse.error(request.getRequestId(), validationError);
            }
            // 2. 读取算法模型匹配规则由ModelMatcher实现 todo:

            // 3. 找到最优的一个或多个模型服务
            List<GenericModelService<T, R>> matchedServices = modelMatcher.match(request, modelServices);
            if (matchedServices.isEmpty()) {
                return GenericResponse.error(request.getRequestId(), "No suitable model service found");
            }

            // 4. 请求模型服务之前的处理
            GenericRequest<T> processedRequest = request;
            for (RequestPreProcessor<T> processor : preProcessors) {
                // todo: chain resolver
                if (!processor.interest().equals(request.getType())) {
                    continue;
                }
                processedRequest = processor.process(processedRequest);
            }

            // 5. 请求模型服务（分发请求）
            List<GenericResponse<T, R>> responses = modelDispatcher.dispatch(processedRequest, matchedServices);

            // 6. 处理模型服务结果（这里简化处理，取第一个结果）
            GenericResponse<T, R> finalResponse = responses.get(0);

            // 7. 请求结束后的处理
            for (ResponsePostProcessor<T, R> processor : postProcessors) {
                // todo: chain resolver
                if (!processor.interest().equals(request.getType())) {
                    continue;
                }
                finalResponse = processor.process(finalResponse);
            }

            return finalResponse;
        } catch (Exception e) {
            log.error("failed to route", e);
            return GenericResponse.error(request.getRequestId(), "Routing failed: " + e.getMessage());
        }
    }
}
