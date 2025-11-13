package com.jvlei.datadispatch.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jvlei.datadispatch.model.dto.ExamInfo;
import com.jvlei.datadispatch.model.dto.PredictResult;
import com.jvlei.datadispatch.model.entity.ExamPredictResult;
import com.jvlei.modelrouter.generic.GenericModelRouter;
import com.jvlei.modelrouter.generic.GenericRequest;
import com.jvlei.modelrouter.generic.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.jvlei.modelrouter.BuzConstant.BUZ_CRISIS_PREDICT;
import static com.jvlei.modelrouter.BuzConstant.BUZ_CRISIS_SELECT;

@Service
@Slf4j
public class StationMsgResolver {

    @Resource
    private GenericModelRouter genericModelRouter;

    public String resolveExamDone(ExamInfo examInfo) {

        GenericResponse<ExamInfo, String> res = genericModelRouter.route(new GenericRequest<>(examInfo, BUZ_CRISIS_PREDICT));
        log.info("resp", res.getResult());

        return res.getResult();

    }

    public ExamPredictResult queryCrisisResult(String accnum) {
        // 查询条件构造
        QueryWrapper<ExamPredictResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accnum", accnum);
        queryWrapper.eq("predict_type", "crisis"); // 假设有预测类型字段区分危急值

        // 执行查询
        return examPredictResultMapper.selectOne(queryWrapper);
    }

}
