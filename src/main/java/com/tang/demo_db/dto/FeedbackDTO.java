package com.tang.demo_db.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FeedbackDTO {
    @JsonAlias({"user_id", "userId"})  // ✅ 兼容 `user_id` & `userId`
    private Long userId;
    private String context;
}
