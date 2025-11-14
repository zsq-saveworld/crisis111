package com.jvlei.datadispatch.controller;

import com.jvlei.datadispatch.model.dto.ExamInfo;
import com.jvlei.datadispatch.model.entity.ExamPredictResult;
import com.jvlei.datadispatch.service.StationMsgResolver;
import com.jvlei.platform.config.api.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/v1/intelijcrisis/msg")
@Slf4j
public class StationMsgController {

    @Resource
    private StationMsgResolver stationMsgResolver;

    @PostMapping("/examDone")
    public R examDoneResolve(@RequestBody ExamInfo exam) {
        return R.data(stationMsgResolver.resolveExamDone(exam));
    }
    @GetMapping("/crisisByPatientId")
    public R<List<ExamPredictResult>>getCrisisByPatientId(@RequestParam String patientId){
        log.info("患者id：{}",patientId);
        log.info("=================================");
        return R.data(stationMsgResolver.getCrisisByPatientId(patientId));
    }
}

