package com.jvlei.modelrouter.generic;

import java.util.List;

/**
 * 泛型模型分发器，将请求分发给匹配到的模型服务
 *
 * @param <T> 请求数据类型
 * @param <R> 响应数据类型
 */
public interface ModelDispatcher<T, R> {

    /**
     * 将请求分发给指定的模型服务
     *
     * @param request  泛型请求
     * @param services 匹配到的模型服务列表
     * @return 模型服务的响应结果
     */
    List<GenericResponse<T, R>> dispatch(GenericRequest<T> request, List<GenericModelService<T, R>> services);
}
