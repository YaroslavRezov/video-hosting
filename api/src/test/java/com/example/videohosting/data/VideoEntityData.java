package com.example.videohosting.data;

import com.example.videohosting.model.entity.TopicEntity;
import com.example.videohosting.model.entity.VideoEntity;

import java.time.LocalDateTime;

import static com.example.videohosting.data.TopicEntityData.createTopicEntity1;
import static com.example.videohosting.data.TopicEntityData.createTopicEntity2;

public class VideoEntityData {
    public static VideoEntity createVideoEntity1() {
        TopicEntity topicEntity = createTopicEntity1();
        VideoEntity videoEntity = VideoEntity.builder()
//                .id(1L)
                .topicEntity(topicEntity)
                .title("Episode delete")
                .description("delete video")
                .filePath("url")
                .episodeNumber(1)
//                .createdAt(LocalDateTime.now())
        .build();
        return videoEntity;
    }

    public static VideoEntity createVideoEntity1(TopicEntity topicEntity) {
        VideoEntity videoEntity = VideoEntity.builder()
//                .id(id)
                .topicEntity(topicEntity)
                .title("Episode delete")
                .description("delete video")
                .filePath("url")
                .episodeNumber(1)
//                .createdAt(createAt)
                .build();
        return videoEntity;
    }

    public static VideoEntity createVideoEntity2() {
        TopicEntity topicEntity = createTopicEntity2();
        VideoEntity videoEntity = VideoEntity.builder()
//                .id(1L)
                .topicEntity(topicEntity)
                .title("Episode delete")
                .description("delete video")
                .filePath("url")
                .episodeNumber(2)
//                .createdAt(LocalDateTime.now())
                .build();
        return videoEntity;
    }
    public static VideoEntity createVideoEntity2(TopicEntity topicEntity) {
        VideoEntity videoEntity = VideoEntity.builder()
//                .id(1L)
                .topicEntity(topicEntity)
                .title("Episode delete")
                .description("delete video")
                .filePath("url")
                .episodeNumber(2)
//                .createdAt(LocalDateTime.now())
                .build();
        return videoEntity;
    }
}
