package com.example.back;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class BackApplicationTest {

    @Test
    void testMain() {
        // Mock SpringApplication.run
        try (var mockedSpringApplication = Mockito.mockStatic(SpringApplication.class)) {
            BackApplication.main(new String[]{});
            mockedSpringApplication.verify(() -> SpringApplication.run(BackApplication.class, new String[]{}));
        }
    }

}