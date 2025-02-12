package com.tang.demo_db.repository;

import com.tang.demo_db.entity.LocationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationContextRepository extends JpaRepository<LocationContext, Long> {
}