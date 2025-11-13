package com.jvlei.modelrouter.generic;

/**
 * 泛型响应后置处理器，在获取模型服务结果后进行处理
 *
 * @param <R> 响应数据类型
 */
public interface ResponsePostProcessor<I, R> {

    /**
     * 处理响应
     *
     * @param response 原始响应
     * @return 处理后的响应
     */
    GenericResponse<I, R> process(GenericResponse<I, R> response);

    String interest();

    /**
     * todo:
     *
     * @param nextProcessor
     */
    default void nextProcessor(ResponsePostProcessor<I, R> nextProcessor) {
    }

}
