package com.syh.passbook.passbook.service;

import com.google.gson.GsonBuilder;
import com.syh.passbook.passbook.constant.Constants;
import com.syh.passbook.passbook.vo.PassTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConsumePassTemplate {
    private final IHBasePassTemplateService passService;

    @Autowired
    public ConsumePassTemplate(IHBasePassTemplateService passService) {
        this.passService = passService;
    }

    @KafkaListener(topics = {Constants.TEMPLATE_TOPIC})
    public void receive(@Payload String passTemplate,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Consume Receive PassTemplate: {}", passTemplate);

        PassTemplate pt;

        try {
            pt = new GsonBuilder().create().fromJson(passTemplate, PassTemplate.class);
        } catch(Exception exception) {
            log.error("Parse Template Error: {}", exception.getMessage());
            return;
        }

        log.info("distributePassTemplateToHBase: {}", passService.persistPassTemplateToHBase(pt));
    }
}
