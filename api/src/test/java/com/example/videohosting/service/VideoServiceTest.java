package com.example.videohosting.service;

import com.example.specs.generated.model.VideoDto;
import com.example.videohosting.mapper.VideoMapper;
import com.example.videohosting.model.entity.TopicEntity;
import com.example.videohosting.model.entity.VideoEntity;
import com.example.videohosting.repository.TopicRepository;
import com.example.videohosting.repository.VideoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringJUnitConfig(classes = {VideoService.class})
public class VideoServiceTest {

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void storageDir(DynamicPropertyRegistry registry) {
        registry.add("app.video.storage-dir", () -> tempDir.toString());
    }

    @MockitoBean
    private TopicRepository topicRepository;
    @MockitoBean
    private VideoRepository videoRepository;
    @MockitoBean
    private VideoMapper videoMapper;

    @Autowired
    private VideoService videoService;

    @Test
    public void save() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "file", "naruto-136.mp4", "video/mp4", new byte[]{1, 2, 3});

        TopicEntity topic = new TopicEntity();
        topic.setId(1L);

        VideoEntity toSave = new VideoEntity();
        VideoEntity saved = new VideoEntity();
        saved.setId(10L);
        saved.setTitle("Naruto Episode 136");
        saved.setEpisodeNumber(136);
        saved.setTopicEntity(topic);
        saved.setFilePath("D:/sod-project/anime/anime-videos/xxx.mp4");

        VideoDto expected = new VideoDto()
                .id(10L)
                .title("Naruto Episode 136")
                .episodeNumber(136)
                .topicId(1L)
                .filePath("D:/sod-project/anime/anime-videos/xxx.mp4");

        when(topicRepository.findById(any())).thenReturn(Optional.of(topic));
        when(videoRepository.existsByTopicEntityIdAndEpisodeNumber(any(), any())).thenReturn(false);
        when(videoMapper.toVideoEntity(anyString(), any(), any(), anyString(), any()))
                .thenReturn(toSave);
        when(videoRepository.save(any())).thenReturn(saved);
        when(videoMapper.toVideoDto(any())).thenReturn(expected);

        VideoDto actual = videoService.save(file, "Naruto Episode 136", 136, "desc", 1L);

        verify(topicRepository).findById(1L);
        verify(videoRepository).existsByTopicEntityIdAndEpisodeNumber(1L, 136);
        verify(videoRepository).save(toSave);
        assertThat(actual).isEqualTo(expected);

        try (var files = Files.list(tempDir)) {
            assertThat(files)
                    .hasSize(1)
                    .allMatch(p -> p.toString().endsWith(".mp4"));
        }
    }

    @Test
    public void getVideo() {
        TopicEntity topic = new TopicEntity();
        topic.setId(1L);
        VideoEntity entity = new VideoEntity();
        entity.setId(10L);
        entity.setTitle("Naruto Episode 136");
        entity.setEpisodeNumber(136);
        entity.setTopicEntity(topic);

        VideoDto expected = new VideoDto()
                .id(10L)
                .title("Naruto Episode 136")
                .episodeNumber(136)
                .topicId(1L);

        when(videoRepository.findById(any())).thenReturn(Optional.of(entity));
        when(videoMapper.toVideoDto(any())).thenReturn(expected);

        VideoDto actual = videoService.getVideo(10L);

        verify(videoRepository).findById(10L);
        verify(videoMapper).toVideoDto(entity);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getVideo_whenNotFound_shouldThrow() {
        when(videoRepository.findById(any())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> videoService.getVideo(99L));

        verify(videoRepository).findById(99L);
        verify(videoMapper, never()).toVideoDto(any());
        assertEquals("no such video with id: 99", ex.getMessage());
    }

    @Test
    public void getVideos_whenTopicIdGiven_filtersByTopic() {
        VideoEntity e1 = new VideoEntity();
        e1.setId(1L);
        VideoDto d1 = new VideoDto().id(1L);

        when(videoRepository.findByTopicEntityIdOrderByEpisodeNumberAsc(any()))
                .thenReturn(List.of(e1));
        when(videoMapper.toVideoDtoList(any())).thenReturn(List.of(d1));

        List<VideoDto> actual = videoService.getVideos(5L);

        verify(videoRepository).findByTopicEntityIdOrderByEpisodeNumberAsc(5L);
        verify(videoRepository, never()).findAllByOrderByTopicEntityIdAscEpisodeNumberAsc();
        assertThat(actual).containsExactly(d1);
    }
}
