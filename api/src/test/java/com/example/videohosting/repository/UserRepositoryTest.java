package com.example.videohosting.repository;

import com.example.videohosting.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
class UserRepositoryTest extends RepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_returnsUser_whenExists() {
        UserEntity user = UserEntity.builder()
                .username("admin")
                .password("hashed_password")
                .build();
        userRepository.save(user);

        Optional<UserEntity> found = userRepository.findByUsername("admin");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("admin");
        assertThat(found.get().getId()).isNotNull();
    }

    @Test
    void findByUsername_returnsEmpty_whenNotFound() {
        Optional<UserEntity> found = userRepository.findByUsername("ghost");
        assertThat(found).isEmpty();
    }
}
