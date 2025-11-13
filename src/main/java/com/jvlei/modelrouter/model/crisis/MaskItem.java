package com.jvlei.modelrouter.model.crisis;

import lombok.Data;

import java.util.List;

@Data
public class MaskItem {

    private int maskId;

    private String label;

    private List<List<Integer>> points;
}
