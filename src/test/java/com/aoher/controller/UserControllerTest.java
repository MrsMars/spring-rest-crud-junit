package com.aoher.controller;

import com.aoher.config.WebConfig;
import com.aoher.filter.CORSFilter;
import com.aoher.model.User;
import com.aoher.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebConfig.class})
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Before
    public void init() {
        initMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .addFilters(new CORSFilter())
                .build();
    }

    @Test
    public void testGetAllSuccess() throws Exception {
        List<User> users = Arrays.asList(
                new User(1, "Daenerys Targaryen"),
                new User(2, "John Snow"));

        when(userService.getAll()).thenReturn(users);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(users.size())))
                .andExpect(jsonPath("$[0].id", is(users.get(0).getId())))
                .andExpect(jsonPath("$[0].username", is(users.get(0).getUsername())))
                .andExpect(jsonPath("$[1].id", is(users.get(1).getId())))
                .andExpect(jsonPath("$[1].username", is(users.get(1).getUsername())));
        verify(userService, times(1)).getAll();
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testGetByIdSuccess() throws Exception {
        User user = new User(1, "Daenerys Targaryen");

        when(userService.findById(user.getId())).thenReturn(user);

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.username", is(user.getUsername())));
        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testGetByIdFail404() throws Exception {
        int id = 1;

        when(userService.findById(id)).thenReturn(null);

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(id);
        verifyNoMoreInteractions(userService);

    }

    @Test
    public void testCreateUserSuccess() throws Exception {
        User user = new User("Arya Stark");

        when(userService.exists(user)).thenReturn(false);
        doNothing().when(userService).create(user);

        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isCreated())
                .andExpect(header().string("location",
                        containsString("http://localhost/user/" + user.getId())));

        verify(userService, times(1)).exists(user);
        verify(userService, times(1)).create(user);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testCreateUserFail409() throws Exception {
        User user = new User("username exists");

        when(userService.exists(user)).thenReturn(true);

        mockMvc.perform(
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(user)))
                .andExpect(status().isConflict());

        verify(userService, times(1)).exists(user);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testUpdateUserSuccess() throws Exception {
        User user = new User(1, "Arya Stark");

        when(userService.findById(user.getId())).thenReturn(user);
        doNothing().when(userService).update(user);

        mockMvc.perform(
                put("/users/{id}", user.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(user)))
                .andExpect(status().isOk());

        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).update(user);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testUpdateUserFail404() throws Exception {
        User user = new User(1, "user not found");

        when(userService.findById(user.getId())).thenReturn(null);

        mockMvc.perform(
                put("/users/{id}", user.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(user)))
                .andExpect(status().isNotFound());
        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testDeleteUserSuccess() throws Exception {
        User user = new User(1, "Arya Stark");

        when(userService.findById(user.getId())).thenReturn(user);
        doNothing().when(userService).delete(user.getId());

        mockMvc.perform(delete("/users/{id}", user.getId()))
                .andExpect(status().isOk());

        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).delete(user.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testDeleteUserFail404() throws Exception {
        User user = new User(1, "user not found");

        when(userService.findById(user.getId())).thenReturn(null);

        mockMvc.perform(delete("/users/{id}", user.getId()))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testCorsHeaders() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(header().string("Access-Control-Allow-Origin", "*"))
                .andExpect(header().string("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE"))
                .andExpect(header().string("Access-Control-Allow-Headers", "*"))
                .andExpect(header().string("Access-Control-Max-Age", "3600"));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}