package com.example.randomdriveproject.navigation.random.repository;

import com.example.randomdriveproject.navigation.random.entity.Edges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EdgesRepository extends JpaRepository<Edges, Long> {
}
