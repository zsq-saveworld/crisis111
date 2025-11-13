package com.jvlei.modelrouter.customimpl.crisis;

import com.jvlei.datadispatch.mapper.TechExceptionMapper;
import com.jvlei.datadispatch.model.dto.ExamInfo;
import com.jvlei.datadispatch.model.entity.TechException;
import com.jvlei.modelrouter.generic.GenericResponse;
import com.jvlei.modelrouter.generic.ResponsePostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.Date;

import static com.jvlei.modelrouter.BuzConstant.BUZ_CRISIS_PREDICT;

@Component
@Slf4j
public class CrisisResponsePostProcessor implements ResponsePostProcessor<ExamInfo, String> {

    @Resource
    private TechExceptionMapper techExceptionMapper;

    @Override
    public GenericResponse<ExamInfo, String> process(GenericResponse<ExamInfo, String> response) {
        log.info("start to process post response, result： {}", response.getResult());
        // 接收结果，做后置处理 todo:

        insertExceptionToTech(response);

        return response;
    }

    @Override
    public String interest() {
        return BUZ_CRISIS_PREDICT;
    }

    private void insertExceptionToTech(GenericResponse<ExamInfo, String> response) {
        // just to insert record into database.
        TechException record = new TechException();
        ExamInfo ei = response.getInput();
        String result = response.getResult();

        record.setAccno(ei.getAccnum());
        record.setHisExamNo(ei.getHisExamNo());
        record.setPatientId(ei.getPatientId());
        record.setPatientName(ei.getPatientName());
        record.setReadState(TechException.STATE_UN_READ);
        record.setErrorLevel("");
        record.setTechuserCode(ei.getTechUserCode());
        record.setScoringTime(new Date());
        record.setScoringInfo("影像质控未通过通知");
        record.setTypeScoring(0);


        techExceptionMapper.insert(record);
    }

}
