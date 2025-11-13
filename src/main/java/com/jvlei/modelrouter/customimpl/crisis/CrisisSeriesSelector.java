package com.jvlei.modelrouter.customimpl.crisis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jvlei.datadispatch.mapper.AeSeriesConfigMapper;
import com.jvlei.datadispatch.model.entity.AeSeriesConfig;
import com.jvlei.datadispatch.model.entity.AiExamInfo;
import com.jvlei.modelrouter.generic.SeriesSelectorItf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

import static com.jvlei.modelrouter.BuzConstant.BUZ_CRISIS_PREDICT;

@Component
@Slf4j
public class CrisisSeriesSelector implements SeriesSelectorItf {

    @Resource
    private AeSeriesConfigMapper aeSeriesConfigMapper;

    /**
     * 对应危急值的序列选择器
     * 应该通过数据库，哪个设备做的，哪个项目，就取哪个 序列名
     */
    @Override
    public String select(AiExamInfo exam) {
        List<AeSeriesConfig> seriesList = aeSeriesConfigMapper.selectList(new LambdaQueryWrapper<AeSeriesConfig>()
                .select(AeSeriesConfig::getSeriesDescription)
                .eq(AeSeriesConfig::getAe, exam.getExamAe())
                .eq(AeSeriesConfig::getModality, exam.getModality())

        );
        if (!CollectionUtils.isEmpty(seriesList)) {
            return seriesList.get(0).getSeriesDescription();
        }
        return null;
    }

    @Override
    public String matchType() {
        return BUZ_CRISIS_PREDICT;
    }
}
