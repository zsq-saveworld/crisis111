package com.jvlei.modelrouter.generic;

/**
 * 泛型请求前置处理器，在请求模型服务前进行处理
 *
 * @param <T> 请求数据类型
 */
public interface RequestPreProcessor<T> {

    /**
     * 处理请求
     *
     * @param request 原始请求
     * @return 处理后的请求
     */
    GenericRequest<T> process(GenericRequest<T> request);

    String interest();
}
