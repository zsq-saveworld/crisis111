package com.jvlei.modelrouter.customimpl;

import com.jvlei.modelrouter.generic.GenericModelService;
import com.jvlei.modelrouter.generic.GenericRequest;
import com.jvlei.modelrouter.generic.GenericResponse;
import com.jvlei.modelrouter.generic.ModelDispatcher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultModelDispatcher<T, R> implements ModelDispatcher<T, R> {

    @Override
    public List<GenericResponse<T, R>> dispatch(GenericRequest<T> request, List<GenericModelService<T, R>> genericModelServices) {
        GenericResponse<T, R> item = genericModelServices.get(0).predict(request);

        // todo: 默认取 get(0)
        List<GenericResponse<T, R>> data = new ArrayList<>();
        //        GenericResponse<String> item = GenericResponse.success(request.getRequestId(), "OK");
        data.add(item);
        return data;
    }
}
