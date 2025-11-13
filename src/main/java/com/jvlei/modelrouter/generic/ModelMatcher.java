package com.jvlei.modelrouter.generic;

import java.util.List;

/**
 * 泛型模型匹配器，根据请求找到最合适的模型服务
 *
 * @param <T> 请求数据类型
 * @param <R> 响应数据类型
 */
public interface ModelMatcher<T, R> {

    /**
     * 根据请求匹配最合适的模型服务
     *
     * @param request  泛型请求
     * @param services 可用的模型服务列表
     * @return 匹配到的模型服务列表
     */
    List<GenericModelService<T, R>> match(GenericRequest<T> request, List<GenericModelService<T, R>> services);
}
