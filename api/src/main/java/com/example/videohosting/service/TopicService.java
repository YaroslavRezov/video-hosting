package com.example.videohosting.service;

import com.example.specs.generated.model.TopicDto;
import com.example.videohosting.mapper.TopicMapper;
import com.example.videohosting.model.entity.TopicEntity;
import com.example.videohosting.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;

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

    public void delete(Long id) {
        topicRepository.deleteById(id);
    }
}
