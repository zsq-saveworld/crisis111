package com.jvlei.modelrouter.generic;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 泛型响应体类，支持任意类型的响应数据
 *
 * @param <R> 响应数据的类型
 */
@Data
public class GenericResponse<I, R> {

    private final String requestId;

    private final LocalDateTime timestamp;

    private final Map<String, Object> metadata = new HashMap<>();

    private R result;

    private I input;

    private String status;

    private String message;

    // 构造函数
    public GenericResponse(String requestId) {
        this.requestId = requestId;
        this.timestamp = LocalDateTime.now();
    }

    // 静态工厂方法
    public static <I, R> GenericResponse<I, R> success(String requestId, I input, R result) {
        GenericResponse<I, R> response = new GenericResponse<>(requestId);
        response.setInput(input);
        response.setStatus("SUCCESS");
        response.setResult(result);
        return response;
    }

    public static <I, R> GenericResponse<I, R> error(String requestId, String message) {
        GenericResponse<I, R> response = new GenericResponse<>(requestId);
        response.setStatus("ERROR");
        response.setMessage(message);
        return response;
    }

    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
}
