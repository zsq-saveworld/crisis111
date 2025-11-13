package com.jvlei.modelrouter.generic;

/**
 * 泛型请求验证器，检查请求的合法性
 *
 * @param <T> 请求数据类型
 */
@FunctionalInterface
public interface RequestValidator<T> {

    /**
     * 验证请求
     *
     * @param request 需要验证的请求
     * @return 验证结果，如果合法返回null，否则返回错误信息
     */
    String validate(GenericRequest<T> request);
}
