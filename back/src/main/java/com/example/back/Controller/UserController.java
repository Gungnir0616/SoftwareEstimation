package com.example.back.Controller;

import com.example.back.Service.UserService;
import com.example.back.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class UserController {

    private static final String MESSAGE = "message";

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody UserLoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        if (userService.checkUserCredentials(request.getUserName(), request.getPassword())) {
            response.put(MESSAGE, "登录成功");
            response.put("success", true);
        } else {
            response.put(MESSAGE, "登录失败，请检查用户名和密码");
            response.put("success", false);
        }
        return response;
    }

    @PostMapping("/register")
    public String register(@RequestBody UserRegisterRequest request) {
        System.out.println(request);
        if(request.getPhone().length() != 11) {
            System.out.println("Phone");
            return "手机号格式不正确";
        }
        User user = new User();
        user.setUserName(request.getUserName());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        userService.registerUser(user);
        return "注册成功";
    }

    @GetMapping("/user/{userName}")
    public User getUserInfo(@PathVariable("userName") String userName) {
        return userService.findByUserName(userName);
    }

    @PutMapping("/user/{userName}")
    public String updateUserInfo(@PathVariable("userName") String userName, @RequestBody User updatedUser) {
        User existingUser = userService.findByUserName(userName);
        if(updatedUser.getPhone().length() != 11) {
            return "手机号格式不正确";
        }
        if(!updatedUser.getMailbox().contains("@")) {
            return "邮箱格式不正确";
        }
        if (existingUser != null) {
            existingUser.setUserName(updatedUser.getUserName());
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setMailbox(updatedUser.getMailbox());
            existingUser.setGender(updatedUser.isGender());
            existingUser.setDepartment(updatedUser.getDepartment());
            existingUser.setRole(updatedUser.getRole());
            userService.updateUser(existingUser);
            return "保存成功";
//            return existingUser;
        }
//        else {
//            throw new RuntimeException("User not found");
//        }
        return null;
    }

    @PostMapping("/user/{userName}/change-password")
    public Map<String, String> updatePassword(@PathVariable("userName") String userName, @RequestBody Map<String, String> passwordForm) {
        String oldPassword = passwordForm.get("oldPassword");
        String newPassword = passwordForm.get("newPassword");
        User user = userService.findByUserName(userName);
        Map<String, String> response = new HashMap<>();
        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            userService.updateUser(user);
            response.put(MESSAGE, "密码修改成功");
        } else {
            response.put(MESSAGE, "旧密码错误");
        }
        return response;
    }
}
