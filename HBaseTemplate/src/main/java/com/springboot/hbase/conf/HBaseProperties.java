package com.springboot.hbase.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.data.hbase")
public class HBaseProperties {
    private String quorum;
    private String rootDir;
    private String nodeParent;
}
