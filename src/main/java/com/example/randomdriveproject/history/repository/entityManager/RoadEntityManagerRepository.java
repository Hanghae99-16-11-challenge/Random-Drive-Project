package com.example.randomdriveproject.history.repository.entityManager;

import com.example.randomdriveproject.history.entity.Road;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class RoadEntityManagerRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void batchInsert(List<Road> roads) {
        int batchSize = 50; // 배치 크기를 설정해줍니다.
        for (int i = 0; i < roads.size(); i += batchSize) {
            List<Road> batch = roads.subList(i, Math.min(i + batchSize, roads.size()));
            for (Road road : batch) {
                entityManager.persist(road);
            }
            entityManager.flush();
            entityManager.clear();
        }
    }
}
