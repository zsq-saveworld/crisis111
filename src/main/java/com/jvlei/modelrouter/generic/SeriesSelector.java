package com.jvlei.modelrouter.generic;

import com.jvlei.datadispatch.model.entity.AiExamInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class SeriesSelector {

    @Resource
    private List<SeriesSelectorItf> selectors;

    public String select(AiExamInfo aiExamInfo, String type) {
        if (!CollectionUtils.isEmpty(selectors) && StringUtils.hasText(type)) {
            for (SeriesSelectorItf selector : selectors) {
                if (type.equals(selector.matchType())) {
                    return selector.select(aiExamInfo);
                }
            }
        }
        return null;
    }

}
