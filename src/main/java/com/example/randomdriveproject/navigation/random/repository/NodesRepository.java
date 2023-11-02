package com.example.randomdriveproject.navigation.random.repository;

import com.example.randomdriveproject.navigation.random.entity.Nodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodesRepository extends JpaRepository<Nodes, Long> {
    Nodes findByName(String name);
}
