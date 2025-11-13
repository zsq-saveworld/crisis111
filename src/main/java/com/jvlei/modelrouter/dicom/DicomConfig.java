package com.jvlei.modelrouter.dicom;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "jvlei.dicom")
public class DicomConfig {

    private String scpAe;

    private String scpIp;

    private int scpPort;

    private String localAe;

    private String localIp;

    private int localPort;

    private String storePath;

}
