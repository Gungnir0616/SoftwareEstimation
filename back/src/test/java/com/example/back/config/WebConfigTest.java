package com.example.back.config;

import com.example.back.Service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(WebConfig.class) // 导入 WebConfig 类
class WebConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService; // 模拟 CourseService bean

    @MockBean
    private UserService userService; // 模拟 UserService bean

    @Test
    void testCorsConfiguration() throws Exception {
        mockMvc.perform(options("/some-endpoint")
                        .header("Origin", "http://localhost:8080")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:8080"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS"));

    }
}
