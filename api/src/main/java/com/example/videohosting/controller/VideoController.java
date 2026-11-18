package com.example.videohosting.controller;

import com.example.specs.generated.api.VideoControllerApi;
import com.example.specs.generated.model.VideoDto;
import com.example.videohosting.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class VideoController implements VideoControllerApi {

    private final VideoService videoService;

    @Override
    public ResponseEntity<VideoDto> getVideo(Long videoId) {
        return ResponseEntity.ok(videoService.getVideo(videoId));
    }

    @Override
    public ResponseEntity<List<VideoDto>> getVideosByTopic(Long topicId) {
        return ResponseEntity.ok(videoService.getVideos(topicId));
    }

    @Override
    public ResponseEntity<VideoDto> createVideo(MultipartFile file, String title, Integer episodeNumber, Long topicId, String description) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(videoService.save(file, title, episodeNumber, topicId, description));
    }

    @Override
    public ResponseEntity<Resource> streamVideo(Long videoId) {
        Resource video = videoService.getVideoStream(videoId);
        return ResponseEntity.ok()
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + video.getFilename() + "\"")
                .body(video);
    }

    @Override
    public ResponseEntity<VideoDto> getNextVideo(Long videoId) {
        return videoService.getNextVideo(videoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<VideoDto> getPreviousVideo(Long videoId) {
        return videoService.getPreviousVideo(videoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
