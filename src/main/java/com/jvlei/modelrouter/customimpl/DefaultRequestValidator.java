package com.jvlei.modelrouter.customimpl;

import com.jvlei.modelrouter.generic.GenericRequest;
import com.jvlei.modelrouter.generic.RequestValidator;
import org.springframework.stereotype.Component;

@Component
public class DefaultRequestValidator implements RequestValidator<String> {

    @Override
    public String validate(GenericRequest<String> request) {
        return null;
    }
}
