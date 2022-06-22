package com.syh.passbook.passbook.vo;

import com.syh.passbook.passbook.constant.FeedbackType;
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

    public boolean validate() {
        try {
            FeedbackType.valueOf(this.type.toUpperCase());
        } catch (Exception exception) {
            return false;
        }
        return null != comment;
    }
}
