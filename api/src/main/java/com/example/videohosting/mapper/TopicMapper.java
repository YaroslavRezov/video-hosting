package com.example.videohosting.mapper;

import com.example.specs.generated.model.TopicDto;
import com.example.videohosting.model.entity.TopicEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TopicMapper {
    public TopicDto toTopicDto(TopicEntity topicEntity) {
        return new TopicDto()
                .id(topicEntity.getId())
                .name(topicEntity.getName())
                .description(topicEntity.getDescription())
                .releasedAt(topicEntity.getReleasedAt());
    }

    public List<TopicDto> toTopicDtoList(List<TopicEntity> topicEntities) {
        return topicEntities.stream()
                .map(this::toTopicDto)
                .toList();
    }

    public TopicEntity toTopicEntity(TopicDto requestTopicDto) {
        TopicEntity topicEntity = TopicEntity.builder()
                .name(requestTopicDto.getName())
                .releasedAt(requestTopicDto.getReleasedAt())
                .description(requestTopicDto.getDescription())
                .build();
        return topicEntity;
    }
}
