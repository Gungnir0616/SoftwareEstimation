package com.example.back.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    public void testUserConstructor() {
        // Given
        String expectedUserName = "testUser";
        String expectedPhone = "1234567890";
        String expectedPassword = "password123";

        // When
        User user = new User(expectedUserName, expectedPhone, expectedPassword);

        // Then
        assertEquals(expectedUserName, user.getUserName());
        assertEquals(expectedPhone, user.getPhone());
        assertEquals(expectedPassword, user.getPassword());
    }

}