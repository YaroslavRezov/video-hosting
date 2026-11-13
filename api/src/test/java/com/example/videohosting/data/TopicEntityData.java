package com.example.videohosting.data;

import com.example.specs.generated.model.TopicDto;
import com.example.videohosting.model.entity.TopicEntity;

import java.time.LocalDateTime;

public class TopicEntityData {
    public static TopicEntity createTopicEntity1() {
        TopicEntity topicEntity = TopicEntity.builder()
//                .id(1L)
                .name("Delete")
                .description("Testing")
                .releasedAt(LocalDateTime.now())
                .build();
        return topicEntity;
    }

    public static TopicEntity createTopicEntity2() {
        TopicEntity topicEntity = TopicEntity.builder()
//                .id(1L)
                .name("Delete Shippuden")
                .description("Testing suuii")
                .releasedAt(LocalDateTime.now())
                .build();
        return topicEntity;
    }

    public static TopicDto createTopicDto1() {
        return new TopicDto()
                .id(1L)
                .name("Delete")
                .description("Testing")
                .releasedAt(LocalDateTime.now());
    }

    public static TopicDto createTopicDto2() {
        return new TopicDto()
                .id(1L)
                .name("Delete Shippuden")
                .description("Testing suuii")
                .releasedAt(LocalDateTime.now());
    }
}
