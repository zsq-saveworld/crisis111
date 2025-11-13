package com.jvlei.datadispatch.model.dto;

import com.jvlei.datadispatch.model.entity.ExamPredictResult;
import lombok.Data;

@Data
public class PredictResult extends ExamPredictResult {

    private String inputFile;

    private String outputDir;

    private boolean canDo;

    private String errMsg;
}
