package com.example.randomdriveproject.history.repository;

import com.example.randomdriveproject.history.entity.Road;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoadRepository extends JpaRepository<Road, Long> {
    @Query("SELECT MAX(r.id) FROM Road r")
    Long findMaxId();
}
