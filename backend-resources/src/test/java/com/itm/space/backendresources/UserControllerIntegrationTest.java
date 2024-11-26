package com.itm.space.backendresources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

    public class UserControllerIntegrationTest extends BaseIntegrationTest{
        @MockBean
        private UserService userService;
        @Autowired
        private ObjectMapper objectMapper;


        @Test
        @WithMockUser(roles = "MODERATOR")
        public void testCreateUser() throws Exception{
            UserRequest userRequest =
                    new UserRequest("username","email@example.com",
                            "password","firstName","lastName");
            mvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRequest)))
                    .andExpect(status().isOk());
            //Проверка, что метод createUser был вызван с любым объектом UserRequest и один раз
            verify(userService,times(1)).createUser(any(UserRequest.class));
    }
    @Test
    @WithMockUser(roles = "MODERATOR")
    public void testGetUserById() throws Exception {
        UUID userId = UUID.randomUUID();// генерация ID
        UserResponse userResponse =
                new UserResponse("firstName","lastName","email@example.com",
                        null,null);
        Mockito.when(userService.getUserById(userId)).thenReturn(userResponse);

        mvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk());
        //Проверка, что метод getUserById был вызван с правильным userId и один раз
        verify(userService,times(1)).getUserById(userId);
    }

    @Test
    @WithMockUser(roles="MODERATOR")
    public void testHello() throws Exception {
        mvc.perform(get("/api/users/hello"))
                .andExpect(status().isOk());
    }


}
