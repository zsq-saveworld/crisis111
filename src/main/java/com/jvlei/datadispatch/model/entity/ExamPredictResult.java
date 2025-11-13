package com.jvlei.datadispatch.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("exam_predict_result")
@KeySequence("seq_exam_predict_result_id")
public class ExamPredictResult {

    public static final String VERSION_V1 = "v1";

    @TableId(type = IdType.INPUT)
    private Long id;

    private String accnum;

    private String hisExamNo;

    private String patientId;

    @ApiModelProperty("ai-预测类型，")
    private String predictType;

    @ApiModelProperty("预测服务")
    private String predictService;

    private String predictDesc;

    private String predictResult;

    private String structuralVersion = VERSION_V1;

}
