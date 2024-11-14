package com.example.back.Mapper;

import com.example.back.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@MybatisTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        // 清空测试数据库中的用户表
        userMapper.deleteAllUsers();
    }

    @Test
    void findByUserName() {
        User user = new User();
        user.setUserName("testUser");
        user.setPassword("testPassword");
        user.setPhone("1234567890");
        userMapper.insertUser(user);

        User foundUser = userMapper.findByUserName("testUser");
        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUserName());
    }

    @Test
    void insertUser() {
        User user = new User();
        user.setUserName("newUser");
        user.setPassword("newPassword");
        user.setPhone("1112223333");

        userMapper.insertUser(user);

        User insertedUser = userMapper.findByUserName("newUser");
        assertNotNull(insertedUser);
        assertEquals("newUser", insertedUser.getUserName());
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setUserName("existingUser");
        user.setPassword("password");
        user.setPhone("1234567890");
        userMapper.insertUser(user);

        user.setPassword("newPassword");
        user.setPhone("0987654321");
        userMapper.updateUser(user);

        User updatedUser = userMapper.findByUserName("existingUser");
        assertNotNull(updatedUser);
        assertEquals("newPassword", updatedUser.getPassword());
        assertEquals("0987654321", updatedUser.getPhone());
    }

    @Test
    void deleteAllUsers() {
        User user1 = new User();
        user1.setUserName("user1");
        user1.setPassword("password1");
        user1.setPhone("1111111111");

        User user2 = new User();
        user2.setUserName("user2");
        user2.setPassword("password2");
        user2.setPhone("2222222222");

        userMapper.insertUser(user1);
        userMapper.insertUser(user2);

        userMapper.deleteAllUsers();

        assertNull(userMapper.findByUserName("user1"));
        assertNull(userMapper.findByUserName("user2"));
    }
}
