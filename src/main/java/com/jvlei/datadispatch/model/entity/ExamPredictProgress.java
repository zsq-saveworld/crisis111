package com.jvlei.datadispatch.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("exam_predict_progress")
@KeySequence("seq_exam_predict_progress_id")
public class ExamPredictProgress {

    @TableId(type = IdType.INPUT)
    private Long id;

    private String accnum;

    private String hisExamNo;

    private String patientId;

    @ApiModelProperty("ai-预测类型，")
    private String predictType;

    private String progress;

    private Integer isError;

    private String errorMsg;

    private Date progressDate;

}
