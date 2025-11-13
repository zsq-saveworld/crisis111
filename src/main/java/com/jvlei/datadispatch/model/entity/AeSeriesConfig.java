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
@KeySequence("SEQ_CONFIG_AE_SERIES_ID")
@NoArgsConstructor
@AllArgsConstructor
@TableName("config_ae_series")
public class AeSeriesConfig {

    @TableId(type = IdType.INPUT)
    private Long id;

    private String ae;

    private String modality;

    @ApiModelProperty("预测目标的key")
    private String targetKey;

    @ApiModelProperty("序列描述")
    private String seriesDescription;

}
