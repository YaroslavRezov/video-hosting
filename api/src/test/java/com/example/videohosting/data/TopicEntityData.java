package com.example.videohosting.data;

import com.example.videohosting.model.entity.TopicEntity;

import java.time.LocalDateTime;

public class TopicEntityData {
    public static TopicEntity createTopicEntity1() {
        TopicEntity topicEntity = TopicEntity.builder()
//                .id(1L)
                .name("Delete")
                .description("Testing")
//                .createdAt(LocalDateTime.now())
                .build();
        return topicEntity;
    }

    public static TopicEntity createTopicEntity2() {
        TopicEntity topicEntity = TopicEntity.builder()
//                .id(1L)
                .name("Delete Shippuden")
                .description("Testing suuii")
//                .createdAt(LocalDateTime.now())
                .build();
        return topicEntity;
    }
}
