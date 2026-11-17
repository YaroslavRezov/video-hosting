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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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

        TopicEntity topicEntity = new TopicEntity();
        topicEntity.setId(1L);

        VideoEntity toSave = new VideoEntity();
        VideoEntity savedVideoEntity = new VideoEntity();
        savedVideoEntity.setId(10L);
        savedVideoEntity.setTitle("Naruto Episode 136");
        savedVideoEntity.setEpisodeNumber(136);
        savedVideoEntity.setTopicEntity(topicEntity);
        savedVideoEntity.setFilePath("D:/sod-project/anime/anime-videos/xxx.mp4");

        VideoDto expected = new VideoDto()
                .id(10L)
                .title("Naruto Episode 136")
                .episodeNumber(136)
                .topicId(1L)
                .filePath("D:/sod-project/anime/anime-videos/xxx.mp4");

        when(topicRepository.findById(any())).thenReturn(Optional.of(topicEntity));
        when(videoRepository.existsByTopicEntityIdAndEpisodeNumber(any(), any())).thenReturn(false);
        when(videoMapper.toVideoEntity(anyString(), any(), any(), anyString(), any()))
                .thenReturn(toSave);
        when(videoRepository.saveAndFlush(any())).thenReturn(savedVideoEntity);
        when(videoMapper.toVideoDto(any())).thenReturn(expected);

        VideoDto actual = videoService.save(file, "Naruto Episode 136", 136, 1L ,"desc" );

        verify(topicRepository).findById(1L);
        verify(videoRepository).existsByTopicEntityIdAndEpisodeNumber(1L, 136);
        verify(videoRepository).saveAndFlush(toSave);
        assertThat(actual).isEqualTo(expected);

        try (var files = Files.list(tempDir)) {
            assertThat(files)
                    .hasSize(1)
                    .allMatch(p -> p.toString().endsWith(".mp4"));
        }
    }

    @Test
    public void getVideo() {
        TopicEntity topicEntity = new TopicEntity();
        topicEntity.setId(1L);
        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setId(10L);
        videoEntity.setTitle("Naruto Episode 136");
        videoEntity.setEpisodeNumber(136);
        videoEntity.setTopicEntity(topicEntity);

        VideoDto expected = new VideoDto()
                .id(10L)
                .title("Naruto Episode 136")
                .episodeNumber(136)
                .topicId(1L);

        when(videoRepository.findById(any())).thenReturn(Optional.of(videoEntity));
        when(videoMapper.toVideoDto(any())).thenReturn(expected);

        VideoDto actual = videoService.getVideo(10L);

        verify(videoRepository).findById(10L);
        verify(videoMapper).toVideoDto(videoEntity);
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
        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setId(1L);
        VideoDto videoDto = new VideoDto().id(1L);

        when(videoRepository.findByTopicEntityIdOrderByEpisodeNumberAsc(any()))
                .thenReturn(List.of(videoEntity));
        when(videoMapper.toVideoDtoList(any())).thenReturn(List.of(videoDto));

        List<VideoDto> actual = videoService.getVideos(5L);

        verify(videoRepository).findByTopicEntityIdOrderByEpisodeNumberAsc(5L);
        verify(videoRepository, never()).findAllByOrderByTopicEntityIdAscEpisodeNumberAsc();
        assertThat(actual).containsExactly(videoDto);
    }

    @Test
    void getVideoStream_returnsResourceForExistingFile() throws IOException {
        String storedName = "abc-123.mp4";
        Path file = tempDir.resolve(storedName);
        Files.write(file, new byte[]{1, 2, 3});

        TopicEntity topicEntity = new TopicEntity();
        topicEntity.setId(1L);

        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setId(10L);
        videoEntity.setFilePath(storedName);
        videoEntity.setTopicEntity(topicEntity);

        when(videoRepository.findById(10L)).thenReturn(Optional.of(videoEntity));

        Resource resource = videoService.getVideoStream(10L);

        verify(videoRepository).findById(10L);
        assertThat(resource).isInstanceOf(FileSystemResource.class);
        assertThat(resource.exists()).isTrue();
        assertThat(resource.getFile().toPath()).isEqualTo(file.toAbsolutePath().normalize());
    }

    @Test
    void getVideoStream_whenVideoNotFound_shouldThrow() {
        when(videoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> videoService.getVideoStream(99L));

        verify(videoRepository).findById(99L);
        assertEquals("no such video with id: 99", ex.getMessage());
    }

    @Test
    void getVideoStream_whenFileMissingOnDisk_shouldThrow() {
        TopicEntity topicEntity = new TopicEntity();
        topicEntity.setId(1L);

        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setId(10L);
        videoEntity.setFilePath("does-not-exist.mp4");
        videoEntity.setTopicEntity(topicEntity);

        when(videoRepository.findById(10L)).thenReturn(Optional.of(videoEntity));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> videoService.getVideoStream(10L));

        assertEquals("video file missing for id: 10", ex.getMessage());
    }

    @Test
    void getVideoStream_whenPathEscapesStorageDir_shouldThrow() {
        TopicEntity topicEntity = new TopicEntity();
        topicEntity.setId(1L);

        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setId(10L);
        videoEntity.setFilePath("../../etc/passwd");
        videoEntity.setTopicEntity(topicEntity);

        when(videoRepository.findById(10L)).thenReturn(Optional.of(videoEntity));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> videoService.getVideoStream(10L));

        assertEquals("invalid file path for video id: 10", ex.getMessage());
    }
}
