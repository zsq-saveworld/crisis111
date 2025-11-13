package com.jvlei.modelrouter.model.crisis;

import lombok.Data;

@Data
public class CrisisPredictRequest {

    private String seriesId;

    private String accnum;

    /**
     * 文件目录
     */
    private String filePath;

}
