package com.example.videohosting.service;

import com.example.specs.generated.model.VideoDto;
import com.example.videohosting.mapper.VideoMapper;
import com.example.videohosting.model.entity.TopicEntity;
import com.example.videohosting.model.entity.VideoEntity;
import com.example.videohosting.repository.TopicRepository;
import com.example.videohosting.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;
    private final TopicRepository topicRepository;

    @Value("${app.video.storage-dir}")
    private String storageDir;

    @Transactional(readOnly = true)
    public List<VideoDto> getVideos(Long topicId) {
        List<VideoEntity> videoEntities = videoRepository.findByTopicEntityIdOrderByEpisodeNumberAsc(topicId);
        return videoMapper.toVideoDtoList(videoEntities);
    }

    @Transactional(readOnly = true)
    public VideoDto getVideo(Long id) {
        VideoEntity videoEntity = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("no such video with id: " + id));
        return videoMapper.toVideoDto(videoEntity);
    }

    private Path getTopicDirectory(String topicName) {
        String safeName = topicName.replaceAll("[^a-zA-Z0-9.-]", "_");
        return Paths.get(storageDir).toAbsolutePath().resolve(safeName).normalize();
    }

    @Transactional
    public VideoDto save(MultipartFile file, String title, Integer episodeNumber, Long topicId, String description) {
        String original = file.getOriginalFilename();
        if (original == null || !original.toLowerCase().endsWith(".mp4")) {
            throw new RuntimeException("only .mp4 files are allowed");
        }

        TopicEntity foundTopicEntity = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("no such topic with id: " + topicId));

        if (videoRepository.existsByTopicEntityIdAndEpisodeNumber(topicId, episodeNumber)) {
            throw new RuntimeException(
                    "video with episode number " + episodeNumber
                            + " already exists in topic " + topicId);
        }

        Path dir = getTopicDirectory(foundTopicEntity.getName());
        String storedName = UUID.randomUUID() + ".mp4";
        Path target = dir.resolve(storedName);

        try {
            Files.createDirectories(dir);
            file.transferTo(target);
        } catch (IOException e) {
            throw new UncheckedIOException("failed to store file", e);
        }

        VideoEntity videoEntity = videoMapper
                .toVideoEntity(title, episodeNumber, description, storedName, foundTopicEntity);
        VideoEntity savedVideoEntity = videoRepository.saveAndFlush(videoEntity);

        return videoMapper.toVideoDto(savedVideoEntity);
    }

    @Transactional(readOnly = true)
    public Resource getVideoStream(Long id) {
        VideoEntity foundVideoEntity = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("no such video with id: " + id));

        Path root = getTopicDirectory(foundVideoEntity.getTopicEntity().getName());
        Path file = root.resolve(foundVideoEntity.getFilePath()).normalize();

        if (!file.startsWith(root)) {
            throw new RuntimeException("invalid file path for video id: " + id);
        }
        if (!Files.exists(file) || !Files.isRegularFile(file)) {
            throw new RuntimeException("video file missing for id: " + id);
        }

        return new FileSystemResource(file);
    }

    @Transactional(readOnly = true)
    public Optional<VideoDto> getNextVideo(Long videoId) {
        VideoEntity current = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("no such video with id: " + videoId));

        return videoRepository
                .findFirstByTopicEntityIdAndEpisodeNumberGreaterThanOrderByEpisodeNumberAsc(
                        current.getTopicEntity().getId(), current.getEpisodeNumber())
                .map(videoMapper::toVideoDto);
    }

    @Transactional(readOnly = true)
    public Optional<VideoDto> getPreviousVideo(Long videoId) {
        VideoEntity current = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("no such video with id: " + videoId));

        return videoRepository
                .findFirstByTopicEntityIdAndEpisodeNumberLessThanOrderByEpisodeNumberDesc(
                        current.getTopicEntity().getId(), current.getEpisodeNumber())
                .map(videoMapper::toVideoDto);
    }
}
