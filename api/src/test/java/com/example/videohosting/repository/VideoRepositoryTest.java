package com.example.videohosting.repository;

import com.example.videohosting.model.entity.TopicEntity;
import com.example.videohosting.model.entity.VideoEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.example.videohosting.data.TopicEntityData.createTopicEntity1;
import static com.example.videohosting.data.TopicEntityData.createTopicEntity2;
import static com.example.videohosting.data.VideoEntityData.createVideoEntity1;
import static com.example.videohosting.data.VideoEntityData.createVideoEntity2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VideoRepositoryTest extends RepositoryIT {

    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private TopicRepository topicRepository;

    @AfterEach
    public void delete() {
        topicRepository.deleteAll();
    }

    @Test
    public void save() {
        TopicEntity savedTopicEntity = topicRepository.save(createTopicEntity1());
        VideoEntity expected = videoRepository.save(createVideoEntity1(savedTopicEntity));

        VideoEntity actual = videoRepository.findById(expected.getId())
                .orElseThrow(() -> new RuntimeException("no such video with id: " + expected.getId()));

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    public void findAllVideos() {
        TopicEntity savedTopicEntity1 = topicRepository.save(createTopicEntity1());
        TopicEntity savedTopicEntity2 = topicRepository.save(createTopicEntity2());

        VideoEntity expected1 = videoRepository.save(createVideoEntity1(savedTopicEntity1));
        VideoEntity expected2 = videoRepository.save(createVideoEntity2(savedTopicEntity2));

        List<VideoEntity> actual = videoRepository.findAll();
        assertThat(actual)
                .hasSize(2)
                .anySatisfy(actual1 -> {
                    assertEquals(expected1.getId(), actual1.getId());
                    assertEquals(expected1.getTitle(), actual1.getTitle());
                    assertEquals(expected1.getEpisodeNumber(), actual1.getEpisodeNumber());
                    assertEquals(expected1.getDescription(), actual1.getDescription());
                    assertEquals(expected1.getCreatedAt(), actual1.getCreatedAt());
                    assertEquals(expected1.getFilePath(), actual1.getFilePath());
                    assertEquals(expected1.getTopicEntity(), actual1.getTopicEntity());
                })
                .anySatisfy(actual2 -> {
                    assertEquals(expected2.getId(), actual2.getId());
                    assertEquals(expected2.getTitle(), actual2.getTitle());
                    assertEquals(expected2.getEpisodeNumber(), actual2.getEpisodeNumber());
                    assertEquals(expected2.getDescription(), actual2.getDescription());
                    assertEquals(expected2.getCreatedAt(), actual2.getCreatedAt());
                    assertEquals(expected2.getFilePath(), actual2.getFilePath());
                    assertEquals(expected2.getTopicEntity(), actual2.getTopicEntity());
                });
    }

}
