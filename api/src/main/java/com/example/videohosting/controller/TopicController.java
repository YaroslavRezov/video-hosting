package com.example.videohosting.controller;


import com.example.specs.generated.api.TopicControllerApi;
import com.example.specs.generated.model.TopicDto;
import com.example.videohosting.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TopicController implements TopicControllerApi {

    private final TopicService topicService;

    @Override
    public ResponseEntity<List<TopicDto>> getTopics() {
        return ResponseEntity.ok(topicService.getTopics());
    }

    @Override
    public ResponseEntity<TopicDto> getTopic(Long topicId) {
        return ResponseEntity.ok(topicService.getTopic(topicId));
    }

    @Override
    public ResponseEntity<TopicDto> createTopic(TopicDto topicDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(topicService.save(topicDto));
    }

    @Override
    public ResponseEntity<Void> deleteTopic(Long topicId) {
        topicService.delete(topicId);
        return ResponseEntity.noContent().build();
    }
}
