package com.springboot.hbase.conf;

import com.springboot.hbase.api.HBaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration(
    proxyBeanMethods = false
)
@EnableConfigurationProperties(HBaseProperties.class)
public class HBaseBeanConfiguration {
    private static final String HBASE_QUORUM = "hbase.zookeeper.quorum";
    private static final String HBASE_ROOT_DIR = "hbase.rootdir";
    private static final String HBASE_ZNODE_PARENT = "hbase.znode.parent";

    private final HBaseProperties hbaseProperties;

    @Autowired
    public HBaseBeanConfiguration(HBaseProperties hbaseProperties) {
        this.hbaseProperties = hbaseProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public HBaseTemplate getHBaseTemplate() {
        org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();

        configuration.set(HBASE_QUORUM, hbaseProperties.getQuorum());
        configuration.set(HBASE_ROOT_DIR, hbaseProperties.getRootDir());
        configuration.set(HBASE_ZNODE_PARENT, hbaseProperties.getNodeParent());

        return new HBaseTemplate(configuration);
    }
}
