package com.tang.demo_db.service;

import com.tang.demo_db.dto.FeedbackDTO;
import com.tang.demo_db.entity.Feedback;
import com.tang.demo_db.entity.User;
import com.tang.demo_db.repository.FeedbackRepository;
import com.tang.demo_db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    public Feedback saveFeedback(FeedbackDTO feedbackDTO) {
        Optional<User> userOptional = userRepository.findById(feedbackDTO.getUserId());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        User user = userOptional.get();
        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setContext(feedbackDTO.getContext());
        feedback.setTime(LocalDateTime.now());

        return feedbackRepository.save(feedback);
    }

    public List<FeedbackDTO> getUserFeedback(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        List<Feedback> feedbackList = feedbackRepository.findByUser(userOptional.get());
        return feedbackList.stream().map(feedback -> {
            FeedbackDTO dto = new FeedbackDTO();
            dto.setUserId(userId);
            dto.setContext(feedback.getContext());
            return dto;
        }).collect(Collectors.toList());
    }
}

