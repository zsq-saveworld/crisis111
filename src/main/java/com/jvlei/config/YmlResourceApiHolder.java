package com.jvlei.config;

import com.jvlei.platform.app.base.entity.SysResourceApi;
import com.jvlei.platform.svc.config.ResourceApiHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class YmlResourceApiHolder implements ResourceApiHolder {

    SysResourceApi api = new SysResourceApi();
    public YmlResourceApiHolder() {
        api.setApiCode("api.yml.resource.api.holder");
        api.setControllerName("YmlResourceApiHolder");
        api.setMethodName("resourceApi");
        api.setApiLanguage("java");
        api.setNeedPermission(0);
        api.setNeedLog(0);
        api.setApiDesc("YmlResourceApiHolder");
        api.setApiModule("YmlResourceApiHolder");
        api.setApiSubModule("YmlResourceApiHolder");
    }

    @Override
    public SysResourceApi resourceApi(String controllerName, String methodName) {
        log.info("load yml resource api holder.");

        return null;
    }

}
