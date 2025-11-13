package com.jvlei.dicom;

import com.jvlei.utils.Dcm2NiiUtil;
import org.junit.jupiter.api.Test;

public class SimpleItkTest {

    @Test
    public void testItk() {
        Dcm2NiiUtil.convertDicomToNiiGz("D:\\workstation\\201_5_0_x_5_0", "D:\\workstation\\201_5_0_x_5_1\\test.nii.gz");

    }

}
