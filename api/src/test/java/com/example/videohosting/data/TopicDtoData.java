package com.example.videohosting.data;

import com.example.specs.generated.model.TopicDto;

import java.time.LocalDateTime;

public class TopicDtoData {

    public static TopicDto createTopicDto(Long id, String name, String description, LocalDateTime releasedAt) {
        return new TopicDto()
                .id(id)
                .name(name)
                .description(description)
                .releasedAt(releasedAt);
    }

    public static TopicDto createTopicDto1() {
        return new TopicDto()
                .id(1L)
                .name("Delete")
                .description("Testing")
                .releasedAt(LocalDateTime.of(2002, 10, 3, 0, 0));
    }

    public static TopicDto createTopicDto2() {
        return new TopicDto()
                .id(1L)
                .name("Delete Shippuden")
                .description("Testing suuii")
                .releasedAt(LocalDateTime.of(2002, 10, 3, 0, 0));
    }
}
