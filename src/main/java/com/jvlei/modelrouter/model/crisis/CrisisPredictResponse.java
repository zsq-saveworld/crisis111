package com.jvlei.modelrouter.model.crisis;

import lombok.Data;

import java.util.List;

@Data
public class CrisisPredictResponse {

    private String seriesId;

    private List<MaskItem> maskItems;
}
