package com.example.videohosting.controller;

import com.example.specs.generated.model.VideoDto;
import com.example.videohosting.model.entity.TopicEntity;
import com.example.videohosting.model.entity.VideoEntity;
import com.example.videohosting.repository.TopicRepository;
import com.example.videohosting.repository.VideoRepository;
import com.example.videohosting.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.example.videohosting.data.TopicEntityData.createTopicEntity1;
import static com.example.videohosting.data.VideoDtoData.createVideoDto;
import static com.example.videohosting.data.VideoEntityData.createVideoEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class VideoControllerTest extends ControllerIT {

    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private VideoRepository videoRepository;

    private TopicEntity topicEntity;

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void storageDir(DynamicPropertyRegistry registry) {
        registry.add("app.video.storage-dir", () -> tempDir.toString());
    }

    @BeforeEach
    void setUp() {
        topicEntity = createTopicEntity1();
        topicEntity.setName("Naruto");
        topicRepository.save(topicEntity);
    }

    @AfterEach
    void cleanUp() {
        videoRepository.deleteAllInBatch();
        topicRepository.deleteAllInBatch();
    }

    @Test
    void getVideo() throws Exception {
        VideoEntity savedVideoEntity = videoRepository.save(createVideoEntity(topicEntity, "Naruto Episode 1", 1));

        MvcResult result = mockMvc.perform(get("/api/videos/{videoId}", savedVideoEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        VideoDto expected = createVideoDto(savedVideoEntity.getId(), "Naruto Episode 1", 1, topicEntity.getId());

        VideoDto actual = TestUtils.asObject(result.getResponse().getContentAsString(), VideoDto.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "filePath")
                .isEqualTo(expected);
    }

    @Test
    void getVideo_whenNotFound_shouldReturn5xx() throws Exception {
        mockMvc.perform(get("/api/videos/{videoId}", 999_999L))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getVideosByTopic() throws Exception {
        videoRepository.save(createVideoEntity(topicEntity, "Naruto Episode 1", 1));
        videoRepository.save(createVideoEntity(topicEntity, "Naruto Episode 2", 2));

        MvcResult result = mockMvc.perform(get("/api/topics/{topicId}/videos", topicEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<VideoDto> actual = TestUtils.asList(result.getResponse().getContentAsString(), VideoDto.class);

        assertThat(actual)
                .hasSize(2)
                .extracting(VideoDto::getEpisodeNumber)
                .containsExactly(1, 2);
    }

    @Test
    void getVideosByTopic_whenNoVideos_shouldReturnEmptyArray() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/topics/{topicId}/videos", topicEntity.getId()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(TestUtils.asList(result.getResponse().getContentAsString(), VideoDto.class)).isEmpty();
    }

    @Test
    void createVideo() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "naruto-1.mp4", "video/mp4", new byte[]{1, 2, 3});

        MvcResult result = mockMvc.perform(multipart("/api/videos")
                        .file(file)
                        .param("title", "Naruto Episode 1")
                        .param("episodeNumber", "1")
                        .param("topicId", topicEntity.getId().toString())
                        .param("description", "Pilot"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        VideoDto actual = TestUtils.asObject(result.getResponse().getContentAsString(), VideoDto.class);
        Path expectedDir = tempDir.resolve("Naruto");

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getTitle()).isEqualTo("Naruto Episode 1");
        assertThat(actual.getEpisodeNumber()).isEqualTo(1);
        assertThat(actual.getTopicId()).isEqualTo(topicEntity.getId());
        assertThat(actual.getFilePath()).endsWith(".mp4");
        assertThat(actual.getCreatedAt()).isNotNull();
        assertThat(expectedDir).exists();
    }

    @Test
    void createVideo_whenTopicNotFound_shouldReturn5xx() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "x.mp4", "video/mp4", new byte[]{1});

        mockMvc.perform(multipart("/api/videos")
                        .file(file)
                        .param("title", "Orphan")
                        .param("episodeNumber", "1")
                        .param("topicId", "999999"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void createVideo_whenDuplicateEpisode_shouldReturn5xx() throws Exception {
        videoRepository.save(createVideoEntity(topicEntity, "Naruto Episode 1", 1));

        MockMultipartFile file = new MockMultipartFile(
                "file", "naruto-1-dup.mp4", "video/mp4", new byte[]{1});

        mockMvc.perform(multipart("/api/videos")
                        .file(file)
                        .param("title", "Duplicate episode")
                        .param("episodeNumber", "1")
                        .param("topicId", topicEntity.getId().toString()))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void createVideo_whenNotMp4_shouldReturn5xx() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "malware.exe", "application/octet-stream", new byte[]{1});

        mockMvc.perform(multipart("/api/videos")
                        .file(file)
                        .param("title", "Bad file")
                        .param("episodeNumber", "1")
                        .param("topicId", topicEntity.getId().toString()))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void createVideo_whenFileMissing_shouldReturn4xx() throws Exception {
        mockMvc.perform(multipart("/api/videos")
                        .param("title", "No file")
                        .param("episodeNumber", "1")
                        .param("topicId", topicEntity.getId().toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void streamVideo_returnsFileBytes() throws Exception {
        String storedName = "stream-test.mp4";
        byte[] content = "fake-mp4-bytes".getBytes();
        Path topicDir = tempDir.resolve("Naruto");
        Files.createDirectories(topicDir);
        Path file = topicDir.resolve(storedName);
        Files.write(file, content);

        VideoEntity savedVideoEntity = videoRepository.save(VideoEntity.builder()
                .title("Naruto Episode 1")
                .episodeNumber(1)
                .filePath(storedName)
                .topicEntity(topicEntity)
                .build());

        MvcResult result = mockMvc.perform(get("/api/videos/{videoId}/stream", savedVideoEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCEPT_RANGES, "bytes"))
                .andExpect(content().contentType("video/mp4"))
                .andReturn();

        assertThat(result.getResponse().getContentAsByteArray()).isEqualTo(content);
    }

    @Test
    void streamVideo_whenVideoNotFound_shouldReturn5xx() throws Exception {
        mockMvc.perform(get("/api/videos/{videoId}/stream", 999_999L))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void streamVideo_whenFileMissingOnDisk_shouldReturn5xx() throws Exception {
        VideoEntity savedVideoEntity = videoRepository.save(VideoEntity.builder()
                .title("Orphan")
                .episodeNumber(1)
                .filePath("not-on-disk.mp4")
                .topicEntity(topicEntity)
                .build());

        mockMvc.perform(get("/api/videos/{videoId}/stream", savedVideoEntity.getId()))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void streamVideo_supportsRangeRequests() throws Exception {
        String storedName = "range-test.mp4";
        byte[] content = new byte[100];
        for (int i = 0; i < content.length; i++) content[i] = (byte) i;
        Path topicDir = tempDir.resolve("Naruto");
        Files.createDirectories(topicDir);
        Files.write(topicDir.resolve(storedName), content);

        VideoEntity savedVideoEntity = videoRepository.save(VideoEntity.builder()
                .title("Range test")
                .episodeNumber(1)
                .filePath(storedName)
                .topicEntity(topicEntity)
                .build());

        MvcResult result = mockMvc.perform(get("/api/videos/{videoId}/stream", savedVideoEntity.getId())
                        .header(HttpHeaders.RANGE, "bytes=10-19"))
                .andExpect(status().isPartialContent())
                .andExpect(header().string(HttpHeaders.CONTENT_RANGE, "bytes 10-19/100"))
                .andReturn();

        byte[] returned = result.getResponse().getContentAsByteArray();
        assertThat(returned).hasSize(10);
        assertThat(returned[0]).isEqualTo((byte) 10);
        assertThat(returned[9]).isEqualTo((byte) 19);
    }
}
