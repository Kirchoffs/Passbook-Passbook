package com.springboot.hbase.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    private Long userId;
    private String type;

    // PassTemplate's row key, if the feedback is for App, then it is null
    private String templateId;

    private String comment;
}
