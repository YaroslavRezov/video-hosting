package com.example.videohosting.service;

import com.example.specs.generated.model.TopicDto;
import com.example.videohosting.mapper.TopicMapper;
import com.example.videohosting.model.entity.TopicEntity;
import com.example.videohosting.repository.TopicRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;

import static com.example.videohosting.data.TopicEntityData.createTopicDto1;
import static com.example.videohosting.data.TopicEntityData.createTopicEntity1;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = {TopicService.class})
public class TopicServiceTest {
    @MockitoBean
    private TopicRepository topicRepository;

    @MockitoBean
    private TopicMapper topicMapper;

    @Autowired
    private TopicService topicService;

    @Test
    public void getTopics() {
        TopicEntity topicEntity = new TopicEntity();
        TopicDto topicDto = new TopicDto();
        when(topicRepository.findAll()).thenReturn(List.of(topicEntity));
        when(topicMapper.toTopicDtoList(any())).thenReturn(List.of(topicDto));

        List<TopicDto> actual = topicService.getTopics();

        verify(topicRepository).findAll();
        verify(topicMapper).toTopicDtoList(List.of(topicEntity));
        assertEquals(1, actual.size());
    }

    @Test
    public void getTopic() {
        TopicEntity topicEntity = createTopicEntity1();
        when(topicRepository.findById(any())).thenReturn(Optional.of(topicEntity));
        TopicDto expected = createTopicDto1();
        when(topicMapper.toTopicDto(any())).thenReturn(expected);

        TopicDto actual = topicService.getTopic(topicEntity.getId());

        verify(topicRepository).findById(topicEntity.getId());
        verify(topicMapper).toTopicDto(topicEntity);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getTopic_whenTopicNotFound_shouldThrowException() {
        Long id = 80082L;
        when(topicRepository.findById(any())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> topicService.getTopic(id));

        verify(topicRepository).findById(id);
        verify(topicMapper, never()).toTopicDto(any());
        assertEquals("no such topic with id: " + id, exception.getMessage());
    }

    @Test
    public void save() {
        TopicEntity topicEntity = new TopicEntity();
        when(topicMapper.toTopicEntity(any())).thenReturn(topicEntity);
        when(topicRepository.save(any())).thenReturn(topicEntity);
        TopicDto topicDto = new TopicDto();
        when(topicMapper.toTopicDto(any())).thenReturn(topicDto);

        TopicDto actual = topicService.save(topicDto);

        verify(topicMapper).toTopicEntity(topicDto);
        verify(topicRepository).save(topicEntity);
        verify(topicMapper).toTopicDto(topicEntity);
        assertThat(actual).isEqualTo(topicDto);
    }

    @Test
    void delete() {
        TopicEntity topicEntity = createTopicEntity1();
        doNothing().when(topicRepository).deleteById(any());

        topicService.delete(topicEntity.getId());

        verify(topicRepository).deleteById(topicEntity.getId());
    }



}
