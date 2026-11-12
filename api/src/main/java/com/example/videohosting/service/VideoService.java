package com.example.videohosting.service;

import com.example.specs.generated.model.VideoDto;
import com.example.videohosting.mapper.VideoMapper;
import com.example.videohosting.model.entity.TopicEntity;
import com.example.videohosting.model.entity.VideoEntity;
import com.example.videohosting.repository.TopicRepository;
import com.example.videohosting.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;
    private final TopicRepository topicRepository;

    @Value("${app.video.storage-dir:D:/sod-project/anime/anime-videos}")
    private String storageDir;

    @Transactional(readOnly = true)
    public List<VideoDto> getVideos(Long topicId) {
        List<VideoEntity> entities = videoRepository.findByTopicEntityIdOrderByEpisodeNumberAsc(topicId);
        return videoMapper.toVideoDtoList(entities);
    }

    @Transactional(readOnly = true)
    public VideoDto getVideo(Long id) {
        VideoEntity entity = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("no such video with id: " + id));
        return videoMapper.toVideoDto(entity);
    }

    @Transactional
    public VideoDto save(MultipartFile file, String title, Integer episodeNumber, String description, Long topicId) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("file is empty");
        }
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

        Path dir = Paths.get(storageDir);
        Files.createDirectories(dir);
        String storedName = UUID.randomUUID() + ".mp4";
        Path target = dir.resolve(storedName);
        file.transferTo(target);

        VideoEntity entity = videoMapper
                .toVideoEntity(title, episodeNumber, description, target.toAbsolutePath().toString(), foundTopicEntity);
        VideoEntity savedVideoEntity = videoRepository.save(entity);

        return videoMapper.toVideoDto(savedVideoEntity);
    }
}
