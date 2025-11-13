package com.jvlei.modelrouter.customimpl;

import com.jvlei.modelrouter.generic.GenericModelService;
import com.jvlei.modelrouter.generic.GenericRequest;
import com.jvlei.modelrouter.generic.ModelMatcher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 最优模型匹配器实现，选择性能最好的模型服务
 */
@Component
public class OptimalModelMatcher<T, R> implements ModelMatcher<T, R> {

    @Override
    public List<GenericModelService<T, R>> match(GenericRequest<T> request, List<GenericModelService<T, R>> services) {

        // 筛选支持当前请求类型的服务
        List<GenericModelService<T, R>> supportedServices = services.stream().filter(service -> service.supports(request.getType()))
                .collect(Collectors.toList());

        // 按性能排序，选择性能最好的前N个服务
        return supportedServices.stream().sorted((s1, s2) -> {
                    double p1 = (double) s1.getMetadata().getOrDefault("performance", 0.0);
                    double p2 = (double) s2.getMetadata().getOrDefault("performance", 0.0);
                    return Double.compare(p2, p1);
                }).limit(1)
                .collect(Collectors.toList());
    }
}
