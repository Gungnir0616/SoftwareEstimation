package com.example.back.Controller;

import com.example.back.Service.UserService;
import com.example.back.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    private User updatedUser;
    private User existingUser;
    @BeforeEach
    void setup() {
        User user = new User();
        user.setUserName("testUser");
        user.setPassword("testPassword");
        user.setPhone("1234567890");
        user.setMailbox("test@example.com");
        user.setGender(true);
        user.setDepartment("IT");
        user.setRole("User");


        updatedUser = new User();
        updatedUser.setUserName("updatedUser");
        updatedUser.setPhone("987654321");
        updatedUser.setMailbox("updatedUser@example.com");
        updatedUser.setGender(false);
        updatedUser.setDepartment("HR");
        updatedUser.setRole("Manager");

        when(userService.findByUserName("testUser")).thenReturn(user);
        when(userService.checkUserCredentials(eq("testUser"), eq("testPassword"))).thenReturn(true);
        when(userService.checkUserCredentials(eq("testUser"), eq("wrongPassword"))).thenReturn(false);
        when(userService.findByUserName("existingUser")).thenReturn(existingUser);
//        when(userService.updateUser(any(User.class))).thenReturn();
    }

    @Test
    void login() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("userName", "testUser");
        loginRequest.put("password", "testPassword");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\": \"testUser\", \"password\": \"testPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\": \"testUser\", \"password\": \"wrongPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("登录失败，请检查用户名和密码"))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void register() throws Exception {
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\": \"newUser\", \"password\": \"newPassword\", \"phone\": \"9876543210\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("注册成功"));
    }

    @Test
    void getUserInfo() throws Exception {
        mockMvc.perform(get("/user/testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("testUser"))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andExpect(jsonPath("$.mailbox").value("test@example.com"))
                .andExpect(jsonPath("$.gender").value(true))
                .andExpect(jsonPath("$.department").value("IT"))
                .andExpect(jsonPath("$.role").value("User"));
    }

    @Test
    void updateUserInfo() throws Exception {
        mockMvc.perform(put("/user/testUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\": \"updatedUser\", \"phone\": \"1112223333\", \"mailbox\": \"updated@example.com\", \"gender\": false, \"department\": \"HR\", \"role\": \"Admin\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("updatedUser"))
                .andExpect(jsonPath("$.phone").value("1112223333"))
                .andExpect(jsonPath("$.mailbox").value("updated@example.com"))
                .andExpect(jsonPath("$.gender").value(false))
                .andExpect(jsonPath("$.department").value("HR"))
                .andExpect(jsonPath("$.role").value("Admin"));
    }

    @Test
    public void testUpdateUserInfo_UserNotFound() throws Exception {
        when(userService.findByUserName(anyString())).thenReturn(null);

        mockMvc.perform(put("/user/{userName}", "nonExistingUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }
    @Test
    void updatePassword() throws Exception {
        mockMvc.perform(post("/user/testUser/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\": \"testPassword\", \"newPassword\": \"newPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("密码修改成功"));

        mockMvc.perform(post("/user/testUser/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\": \"wrongPassword\", \"newPassword\": \"newPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("旧密码错误"));
    }
}
