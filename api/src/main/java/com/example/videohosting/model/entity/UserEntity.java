package com.example.videohosting.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
    @Column(nullable = false, updatable = false)
    Long id;
    @Column(nullable = false, updatable = false, unique = true)
    String username;
    @Column(nullable = false, updatable = false)
    String password;
}
