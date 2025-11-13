package com.jvlei.modelrouter.generic;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 泛型请求体类，支持任意类型的请求数据
 *
 * @param <T> 请求数据的类型
 */
@Data
public class GenericRequest<T> {

    private final String requestId;

    private final String type;

    private final Map<String, Object> metadata = new HashMap<>();

    private T data;

    private Map<String, String> preProcessorResult;

    private Map<String, String> postProcessorResult;

    // 构造函数
    public GenericRequest(T data, String type) {
        this.data = data;
        this.type = type;
        this.requestId = generateRequestId();
        preProcessorResult = new ConcurrentHashMap<>();
        postProcessorResult = new ConcurrentHashMap<>();
    }

    // 生成唯一请求ID
    private String generateRequestId() {
        return "req_" + System.currentTimeMillis() + "_" + hashCode();
    }
}
