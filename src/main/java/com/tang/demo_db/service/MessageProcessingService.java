package com.tang.demo_db.service;

import org.springframework.stereotype.Service;

@Service
public class MessageProcessingService {

    public String processErrorMessage(String message) {
        // Add any message processing logic here
        return message;
    }

    public String processNonFoodQuery(String message) {
        // Add any non-food query processing logic here
        return message;
    }
}