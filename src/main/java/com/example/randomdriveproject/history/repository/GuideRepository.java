package com.example.randomdriveproject.history.repository;

import com.example.randomdriveproject.history.entity.Guide;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideRepository extends JpaRepository<Guide, Long> {
}
