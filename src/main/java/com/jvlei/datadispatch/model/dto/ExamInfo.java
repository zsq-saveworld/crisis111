package com.jvlei.datadispatch.model.dto;

import com.jvlei.datadispatch.model.entity.AiExamInfo;
import lombok.Data;

@Data
public class ExamInfo extends AiExamInfo {

    private String inputFile;

    private String outputDir;

    private boolean canDo;

    private String errMsg;

}
