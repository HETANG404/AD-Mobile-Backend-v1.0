package com.tang.demo_db.controller;

import com.tang.demo_db.dto.FeedbackDTO;
import com.tang.demo_db.entity.Feedback;
import com.tang.demo_db.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // 用户提交反馈
    @PostMapping("/submit")
    public ResponseEntity<Map<String, String>> submitFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        System.out.println("收到的 JSON: " + feedbackDTO);
        try {
            feedbackService.saveFeedback(feedbackDTO);
            return ResponseEntity.ok(Collections.singletonMap("message", "Feedback submitted successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }


    // 获取用户的所有反馈
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FeedbackDTO>> getUserFeedback(@PathVariable Long userId) {
        List<FeedbackDTO> feedbackList = feedbackService.getUserFeedback(userId);
        return ResponseEntity.ok(feedbackList);
    }
}
