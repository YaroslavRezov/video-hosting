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
                .releasedAt(LocalDateTime.of(2002, 10, 3, 0, 0))
                .build();
        return topicEntity;
    }

    public static TopicEntity createTopicEntity2() {
        TopicEntity topicEntity = TopicEntity.builder()
//                .id(1L)
                .name("Delete Shippuden")
                .description("Testing suuii")
                .releasedAt(LocalDateTime.of(2002, 10, 3, 0, 0))
                .build();
        return topicEntity;
    }
}
