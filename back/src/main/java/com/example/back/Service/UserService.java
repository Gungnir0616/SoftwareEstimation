package com.example.back.Service;

import com.example.back.entity.User;
import com.example.back.Mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findByUserName(String userName) {
        return userMapper.findByUserName(userName);
    }

    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    public boolean checkUserCredentials(String userName, String password) {
        User user = findByUserName(userName);
//        return user != null && user.getPassword().equals(password);
        return user != null && BCrypt.checkpw(password, user.getPassword());
    }

    public void registerUser(User user) {
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        userMapper.insertUser(user);
    }
}

