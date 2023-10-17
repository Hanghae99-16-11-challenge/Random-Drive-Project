package com.example.randomdriveproject.history.repository;

import com.example.randomdriveproject.history.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
}
