package com.example.videohosting.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "video")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class VideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "video_id_seq")
    @SequenceGenerator(name = "video_id_seq", sequenceName = "video_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "title", nullable = false, length = 250)
    private String title;
    @Column(name = "episode_number", nullable = false)
    private Integer episodeNumber;
    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_video_topic"))
    private TopicEntity topicEntity;
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
