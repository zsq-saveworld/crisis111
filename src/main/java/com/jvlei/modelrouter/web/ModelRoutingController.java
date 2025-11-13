package com.jvlei.modelrouter.web;

import com.jvlei.modelrouter.generic.GenericRequest;
import com.jvlei.modelrouter.generic.GenericResponse;
import com.jvlei.modelrouter.generic.GenericModelRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模型路由控制器，处理HTTP请求
 */
@RestController
@RequestMapping("/api/models")
public class ModelRoutingController {

    private final GenericModelRouter<String, String> textModelRouter;

    @Autowired
    public ModelRoutingController(GenericModelRouter<String, String> textModelRouter) {
        this.textModelRouter = textModelRouter;
    }

    @PostMapping("/route/text")
    public GenericResponse<String, String> routeTextRequest(@RequestBody String text) {
        GenericRequest<String> request = new GenericRequest<>(text, "text");
        return textModelRouter.route(request);
    }

    // 可以添加更多处理不同类型请求的接口
    // @PostMapping("/route/image")
    // public GenericResponse<ImageResult> routeImageRequest(@RequestBody ImageData image) {
    //     ...
    // }
}
