package com.tang.demo_db.repository;

import com.tang.demo_db.entity.Feedback;
import com.tang.demo_db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    // 根据用户获取所有反馈
    List<Feedback> findByUser(User user);
}

