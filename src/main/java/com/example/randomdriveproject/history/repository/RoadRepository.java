package com.example.randomdriveproject.history.repository;

import com.example.randomdriveproject.history.entity.Road;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadRepository extends JpaRepository<Road, Long> {
}
