package com.syh.passbook.passbook.service;

import com.syh.passbook.passbook.vo.Feedback;
import com.syh.passbook.passbook.vo.Response;

public interface IFeedbackService {
    Response createFeedback(Feedback feedback);
    Response getFeedback(Long userId);
}
