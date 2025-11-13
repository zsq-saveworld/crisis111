package com.jvlei.modelrouter.generic;

import com.jvlei.platform.config.api.R;

import java.util.Map;

/**
 * 泛型模型服务接口，定义模型服务的基本能力
 *
 * @param <T> 接收的请求数据类型
 * @param <R> 返回的响应数据类型
 */
public interface GenericModelService<T, R> {

    /**
     * 获取服务唯一标识
     */
    String getServiceId();

    /**
     * 获取服务元数据，用于模型匹配
     */
    Map<String, Object> getMetadata();

    /**
     * 执行模型预测
     */
    GenericResponse<T, R> predict(GenericRequest<T> request);

    /**
     * 检查服务是否支持特定类型的请求
     */
    boolean supports(String requestType);
}
