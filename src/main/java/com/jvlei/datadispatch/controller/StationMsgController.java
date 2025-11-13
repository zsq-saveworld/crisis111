package com.jvlei.datadispatch.controller;

import com.jvlei.datadispatch.model.dto.ExamInfo;
import com.jvlei.datadispatch.model.entity.ExamPredictResult;
import com.jvlei.datadispatch.service.StationMsgResolver;
import com.jvlei.platform.config.api.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/v1/intelijcrisis/msg")
public class StationMsgController {

    @Resource
    private StationMsgResolver stationMsgResolver;

    @PostMapping("/examDone")
    public R examDoneResolve(@RequestBody ExamInfo exam) {
        return R.data(stationMsgResolver.resolveExamDone(exam));
    }



}
