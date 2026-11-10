package com.example.videohosting.repository;

import com.example.videohosting.model.entity.TopicEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.example.videohosting.data.TopicEntityData.createTopicEntity2;
import static org.assertj.core.api.Assertions.assertThat;
import static com.example.videohosting.data.TopicEntityData.createTopicEntity1;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TopicRepositoryTest extends RepositoryIT {

    @Autowired
    private TopicRepository topicRepository;

    @AfterEach
    public void delete() {
        topicRepository.deleteAll();
    }

    @Test
    public void save() {
        TopicEntity expected = topicRepository.save(createTopicEntity1());

        TopicEntity actual = topicRepository.findById(expected.getId())
                .orElseThrow(() -> new RuntimeException("no such topic with id: " + expected.getId()));

        assertEquals(expected, actual);
    }

    @Test
    public void findAllTopics() {
        TopicEntity expected1 = topicRepository.save(createTopicEntity1());
        TopicEntity expected2 = topicRepository.save(createTopicEntity2());

        List<TopicEntity> actual = topicRepository.findAll();

        assertThat(actual)
                .hasSize(2)
                .anySatisfy(actual1 -> {
                    assertEquals(expected1.getId(), actual1.getId());
                    assertEquals(expected1.getName(), actual1.getName());
                    assertEquals(expected1.getDescription(), actual1.getDescription());
                    assertEquals(expected1.getCreatedAt(), actual1.getCreatedAt());
                })
                .anySatisfy(actual2 -> {
                    assertEquals(expected2.getId(), actual2.getId());
                    assertEquals(expected2.getName(), actual2.getName());
                    assertEquals(expected2.getDescription(), actual2.getDescription());
                    assertEquals(expected2.getCreatedAt(), actual2.getCreatedAt());
                });
    }
}
