package com.example.videohosting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public abstract class ControllerIT {
    @Autowired
    protected MockMvc mockMvc;

    @ServiceConnection
    static PostgreSQLContainer POSTGRES_CONTAINER =
            new PostgreSQLContainer("postgres:16-alpine");

    static {
        POSTGRES_CONTAINER.start();
    }
}
