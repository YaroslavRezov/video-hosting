package com.example.videohosting.repository;

import com.example.videohosting.model.entity.TopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<TopicEntity, Long> {
}
