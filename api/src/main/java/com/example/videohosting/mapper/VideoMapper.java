package com.example.videohosting.mapper;

import com.example.specs.generated.model.VideoDto;
import com.example.videohosting.model.entity.TopicEntity;
import com.example.videohosting.model.entity.VideoEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VideoMapper {
    public VideoDto toVideoDto(VideoEntity videoEntity) {
        return new VideoDto()
                .id(videoEntity.getId())
                .title(videoEntity.getTitle())
                .description(videoEntity.getDescription())
                .createdAt(videoEntity.getCreatedAt())
                .episodeNumber(videoEntity.getEpisodeNumber())
                .filePath(videoEntity.getFilePath())
                .topicId(videoEntity.getTopicEntity().getId());
    }
    public List<VideoDto> toVideoDtoList(List<VideoEntity> videoEntities) {
        return videoEntities.stream()
                .map(this::toVideoDto)
                .toList();
    }
    public VideoEntity toVideoEntity(VideoDto videoDto, TopicEntity foundTopicEntity) {
        return VideoEntity.builder()
                .title(videoDto.getTitle())
                .episodeNumber(videoDto.getEpisodeNumber())
                .description(videoDto.getDescription())
                .filePath(videoDto.getFilePath())
                .topicEntity(foundTopicEntity)
                .build();
    }

    public VideoEntity toVideoEntity(String title, Integer episodeNumber, String description, String filePath, TopicEntity topic) {
        return VideoEntity.builder()
                .title(title)
                .episodeNumber(episodeNumber)
                .description(description)
                .filePath(filePath)
                .topicEntity(topic)
                // createdAt — @CreationTimestamp fills it
                .build();
    }
}
