package com.example.videohosting.service;

import com.example.specs.generated.model.TopicDto;
import com.example.videohosting.mapper.TopicMapper;
import com.example.videohosting.model.entity.TopicEntity;
import com.example.videohosting.model.entity.VideoEntity;
import com.example.videohosting.repository.TopicRepository;
import com.example.videohosting.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;
    private final VideoRepository videoRepository;

    @Value("${app.video.storage-dir}")
    private String storageDir;

    public List<TopicDto> getTopics() {
        List<TopicEntity> topicEntities = topicRepository.findAll();
        return topicMapper.toTopicDtoList(topicEntities);
    }

    public TopicDto getTopic(Long id) {
        TopicEntity topicEntity = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("no such topic with id: " + id));
        return topicMapper.toTopicDto(topicEntity);
    }

    public TopicDto save(TopicDto requestTopicDto) {
        TopicEntity savedTopicEntity = topicRepository
                .save(topicMapper.toTopicEntity(requestTopicDto));
        return topicMapper.toTopicDto(savedTopicEntity);
    }

    @Transactional
    public void delete(Long id) {
        TopicEntity topicEntity = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("no such topic with id: " + id));

        List<VideoEntity> videos = topicEntity.getVideos();

        for (VideoEntity video : videos) {
            Path fileToDelete = Paths.get(storageDir)
                    .resolve(topicEntity.getName().replaceAll("[^a-zA-Z0-9.-]", "_"))
                    .resolve(video.getFilePath())
                    .normalize();
            try {
                if (Files.exists(fileToDelete)) {
                    Files.delete(fileToDelete);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete video file: " + fileToDelete, e);
            }
        }

        topicRepository.delete(topicEntity);

        Path topicDir = Paths.get(storageDir)
                .resolve(topicEntity.getName().replaceAll("[^a-zA-Z0-9.-]", "_"))
                .normalize();
        try {
            if (Files.exists(topicDir)) {
                Files.delete(topicDir);
            }
        } catch (IOException e) {
            System.err.println("Could not delete empty directory: " + topicDir);
        }
    }
}
