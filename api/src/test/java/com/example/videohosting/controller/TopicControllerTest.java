package com.example.videohosting.controller;

import com.example.specs.generated.model.TopicDto;
import com.example.videohosting.model.entity.TopicEntity;
import com.example.videohosting.repository.TopicRepository;
import com.example.videohosting.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;


import java.time.LocalDateTime;
import java.util.List;

import static com.example.videohosting.data.TopicDtoData.createTopicDto;
import static com.example.videohosting.data.TopicEntityData.createTopicEntity1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TopicControllerTest extends ControllerIT {

    @Autowired
    private TopicRepository topicRepository;

    @AfterEach
    void cleanUp() {
        topicRepository.deleteAllInBatch();
    }

    @Test
    void getTopics() throws Exception {
        TopicEntity savedTopicEntity = topicRepository.save(createTopicEntity1());
        TopicDto expected = createTopicDto(savedTopicEntity.getId(), savedTopicEntity.getName(), savedTopicEntity.getDescription(), savedTopicEntity.getReleasedAt());

        MvcResult result = mockMvc.perform(get("/api/topics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<TopicDto> actual = TestUtils.asList(result.getResponse().getContentAsString(), TopicDto.class);

        assertThat(actual)
                .hasSize(1)
                .usingRecursiveComparison()
                .isEqualTo(List.of(expected));
    }

    @Test
    void getTopics_whenEmpty_shouldReturnEmptyArray() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/topics"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(TestUtils.asList(result.getResponse().getContentAsString(), TopicDto.class)).isEmpty();
    }

    @Test
    void getTopic() throws Exception {
        TopicEntity savedTopicEntity = topicRepository.save(createTopicEntity1());
        MvcResult result = mockMvc.perform(get("/api/topics/{topicId}", savedTopicEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        TopicDto expected = createTopicDto(savedTopicEntity.getId(), savedTopicEntity.getName(), savedTopicEntity.getDescription(), savedTopicEntity.getReleasedAt());

        TopicDto actual = TestUtils.asObject(result.getResponse().getContentAsString(), TopicDto.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getTopic_whenNotFound_shouldReturn5xx() throws Exception {
        mockMvc.perform(get("/api/topics/{topicId}", 999_999L))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void createTopic() throws Exception {
        TopicDto request = createTopicDto(1L, "Bleach", "Shinigami anime", LocalDateTime.of(2004, 10, 5, 0, 0));

        MvcResult result = mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        TopicDto actual = TestUtils.asObject(result.getResponse().getContentAsString(), TopicDto.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(request);
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void createTopic_whenMissingName_shouldReturn4xx() throws Exception {
        TopicDto request = createTopicDto(null, null, "no name", LocalDateTime.of(2002, 10, 3, 0, 0));

        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createTopic_whenDuplicateName_shouldReturn5xx() throws Exception {
        topicRepository.save(createTopicEntity1());
        TopicDto duplicate = createTopicDto(1L, "Delete", "Testing", LocalDateTime.of(2002, 10, 3, 0, 0));

        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(duplicate)))
                .andExpect(status().is5xxServerError());
    }
}