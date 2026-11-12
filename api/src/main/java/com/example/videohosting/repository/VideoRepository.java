package com.example.videohosting.repository;

import com.example.videohosting.model.entity.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<VideoEntity, Long> {

    List<VideoEntity> findByTopicEntityIdOrderByEpisodeNumberAsc(Long topicId);

    Optional<VideoEntity> findFirstByTopicEntityIdAndEpisodeNumberGreaterThanOrderByEpisodeNumberAsc(
            Long topicId, Integer episodeNumber);

    Optional<VideoEntity> findFirstByTopicEntityIdAndEpisodeNumberLessThanOrderByEpisodeNumberDesc(
            Long topicId, Integer episodeNumber);

    List<VideoEntity> findByTitleContainingIgnoreCase(String q);

    List<VideoEntity> findAllByOrderByTopicEntityIdAscEpisodeNumberAsc();

    Boolean existsByTopicEntityIdAndEpisodeNumber(Long topicId, Integer episodeNumber);
}
