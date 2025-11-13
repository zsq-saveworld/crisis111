package com.jvlei.datadispatch.model.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@TableName("EMRGRADE_EXCEPTION")
@KeySequence(value = "SEQ_YJ_EMRGRADE_EXCEPTION_LONG", dbType = DbType.ORACLE_12C)
@DS("yjstation")
@NoArgsConstructor
@AllArgsConstructor
public class TechException {

    private static final long serialVersionUID = 1L;

    public static final String STATE_READ = "已读";

    public static final String STATE_UN_READ = "未读";

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;

    /**
     * 检查单号
     */
    private String hisExamNo;

    /**
     * 评价时间
     */
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private Date scoringTime;

    /**
     * 异常评价说明
     */
    private String scoringInfo;

    /**
     * 异常评价等级
     */
    private String errorLevel;

    /**
     * 技师编号
     */
    private String techuserCode;

    /**
     * 患者id
     */
    private String patientId;

    /**
     * 患者姓名
     */
    private String patientName;

    /**
     * 已读未读状态
     */
    private String readState;

    /**
     * 类型 0:图像; 1:影像; 2:AI质控
     */
    private Integer typeScoring;

    /**
     * 影像号
     */
    private String accno;

}
