package com.tang.demo_db.repository;

import com.tang.demo_db.entity.DailyView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyViewRepository extends JpaRepository<DailyView, Long> {
    Optional<DailyView> findByDate(LocalDate date);
}
