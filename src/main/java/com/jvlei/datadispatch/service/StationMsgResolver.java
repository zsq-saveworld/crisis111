package com.jvlei.datadispatch.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jvlei.datadispatch.mapper.ExamPredictResultMapper;
import com.jvlei.datadispatch.model.dto.ExamInfo;
import com.jvlei.datadispatch.model.entity.ExamPredictResult;
import com.jvlei.modelrouter.generic.GenericModelRouter;
import com.jvlei.modelrouter.generic.GenericRequest;
import com.jvlei.modelrouter.generic.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;

import static com.jvlei.modelrouter.BuzConstant.BUZ_CRISIS_PREDICT;

@Service
@Slf4j
public class StationMsgResolver {

    @Resource
    private GenericModelRouter genericModelRouter;

    @Resource
    private ExamPredictResultMapper examPredictResultMapper;
    public String resolveExamDone(ExamInfo examInfo) {

        GenericResponse<ExamInfo, String> res = genericModelRouter.route(new GenericRequest<>(examInfo, BUZ_CRISIS_PREDICT));
        log.info("resp", res.getResult());

        return res.getResult();

    }

    public List<ExamPredictResult> getCrisisByPatientId(String patientId){
        QueryWrapper<ExamPredictResult>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("patient_id",patientId);
        return examPredictResultMapper.selectList(queryWrapper);
    }
}

