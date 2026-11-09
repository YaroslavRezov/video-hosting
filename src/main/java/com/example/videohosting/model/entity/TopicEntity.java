package com.example.videohosting.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "topic")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class TopicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "topic_id_seq")
    @SequenceGenerator(name = "topic_id_seq", sequenceName = "topic_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

//    @OneToMany(mappedBy = "topicEntity", cascade = CascadeType.ALL, orphanRemoval = true)
//    @OrderBy("episodeNumber ASC")
//    private List<VideoEntity> videos = new ArrayList<>();

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    } //ai bollocks

}
