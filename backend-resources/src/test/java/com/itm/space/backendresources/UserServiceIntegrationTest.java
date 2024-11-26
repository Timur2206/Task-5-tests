package com.itm.space.backendresources;

import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserServiceIntegrationTest extends BaseIntegrationTest {
    private final UserService userService;
    private final Keycloak keycloak;

    @Autowired
    public UserServiceIntegrationTest(UserService userService, Keycloak keycloak) {
        this.userService = userService;
        this.keycloak = keycloak;
    }
    private final UserRequest userRequest = new UserRequest("username",
            "email@example.com",
            "password",
            "First",
            "Last");

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void testCreateUser() throws Exception {
        mvc.perform(requestWithContent(post("/api/users"), userRequest))
                .andExpect(status().isOk());
        UserRepresentation createdUser = keycloak.realm("ITM").users().search(userRequest.getUsername()).get(0);
        // Сравниваем ожидаемые данные с фактическими данными пользователя
        assertEquals("username", createdUser.getUsername()); // Проверяем имя пользователя
        assertEquals("email@example.com", createdUser.getEmail()); // Проверяем email
        assertEquals("First", createdUser.getFirstName()); // Проверяем имя
        assertEquals("Last", createdUser.getLastName()); // Проверяем фамилию

        keycloak.realm("ITM").users().get(createdUser.getId()).remove();
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void testGetUserById() throws Exception {
        mvc.perform(requestWithContent(post("/api/users"), userRequest))
                .andExpect(status().isOk());
        UserRepresentation createdUser = keycloak.realm("ITM").users().search(userRequest.getUsername()).get(0);
        UserResponse userResponse = userService.getUserById(UUID.fromString(createdUser.getId()));

        assertNotNull(userResponse);
        assertEquals(userRequest.getFirstName(), userResponse.getFirstName());
        assertEquals(userRequest.getEmail(), userResponse.getEmail());
        System.out.println("User retrieved successfully: " + userResponse.getFirstName());
        keycloak.realm("ITM").users().get(createdUser.getId()).remove();
    }

}