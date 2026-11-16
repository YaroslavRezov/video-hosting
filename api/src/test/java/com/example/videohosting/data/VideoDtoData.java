package com.example.videohosting.data;

import com.example.specs.generated.model.VideoDto;

import java.time.LocalDateTime;

public class VideoDtoData {
    public static VideoDto createVideoDto(Long id, String title, int episodeNumber, Long topicId) {
        return new VideoDto()
                .id(id)
                .title(title)
                .episodeNumber(episodeNumber)
                .topicId(topicId)
                .createdAt(LocalDateTime.now())
                .description("desc")
                .filePath("stored-" + episodeNumber + ".mp4");
    }

}
