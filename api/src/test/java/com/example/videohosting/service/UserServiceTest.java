package com.example.videohosting.service;

import com.example.videohosting.model.entity.UserEntity;
import com.example.videohosting.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = {UserService.class})
class UserServiceTest {

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void loadUserByUsername_returnsUserDetails_whenUserExists() {
        UserEntity entity = UserEntity.builder()
                .username("admin")
                .password("encoded_password")
                .build();
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(entity));

        UserDetails userDetails = userService.loadUserByUsername("admin");

        assertThat(userDetails.getUsername()).isEqualTo("admin");
        assertThat(userDetails.getPassword()).isEqualTo("encoded_password");

        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsername_throwsException_whenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("ghost"));

        assertThat(ex.getMessage()).isEqualTo("User not found: ghost");
    }
}
