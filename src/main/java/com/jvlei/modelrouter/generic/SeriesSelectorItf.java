package com.jvlei.modelrouter.generic;

import com.jvlei.datadispatch.model.entity.AiExamInfo;

public interface SeriesSelectorItf {

    String select(AiExamInfo exam);

    String matchType();

}
