package com.jvlei.datadispatch.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jvlei.platform.constant.AppConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("ai_exam_info")
public class AiExamInfo {

    public static final String VERSION_V1 = "v1";

    @TableId(type = IdType.INPUT)
    private String accnum;

    private String hisExamNo;

    private String patientId;

    private String modality;

    private String examItems;

    private String patientName;

    private String patientSex;

    private String patientBirthday;

    @DateTimeFormat(pattern = AppConstant.PATTER_DATETIME)
    @JsonFormat(pattern = AppConstant.PATTER_DATETIME, timezone = "GMT+8")
    private Date examDateTime;

    private String reqDeptCode;

    private String reqDeptName;

    private String techUserName;

    private String techUserCode;

    private String examAe;

}
