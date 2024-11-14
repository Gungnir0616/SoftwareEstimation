package com.example.back.Service;

import com.example.back.Mapper.UserMapper;
import com.example.back.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User user = new User();
        user.setUserName("testUser");
        user.setPassword("testPassword");
        user.setPhone("1234567890");
        user.setMailbox("test@example.com");
        user.setGender(true);
        user.setDepartment("IT");
        user.setRole("User");

        when(userMapper.findByUserName("testUser")).thenReturn(user);
        when(userMapper.findByUserName("unknownUser")).thenReturn(null);
    }

    @Test
    void findByUserName() {
        User user = userService.findByUserName("testUser");
        assertNotNull(user);
        assertEquals("testUser", user.getUserName());

        User unknownUser = userService.findByUserName("unknownUser");
        assertNull(unknownUser);
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setUserName("testUser");
        user.setPassword("newPassword");
        user.setPhone("9876543210");

        userService.updateUser(user);

        verify(userMapper, times(1)).updateUser(user);
    }

    @Test
    void checkUserCredentials() {
        assertTrue(userService.checkUserCredentials("testUser", "testPassword"));
        assertFalse(userService.checkUserCredentials("testUser", "wrongPassword"));
        assertFalse(userService.checkUserCredentials("unknownUser", "anyPassword"));
    }

    @Test
    void registerUser() {
        User newUser = new User();
        newUser.setUserName("newUser");
        newUser.setPassword("newPassword");
        newUser.setPhone("1112223333");

        userService.registerUser(newUser);

        verify(userMapper, times(1)).insertUser(newUser);
    }
}
