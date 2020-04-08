package com.aoher.controller;

import com.aoher.config.WebConfig;
import com.aoher.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { WebConfig.class })
public class UserControllerIT {

    private static final int UNKNOWN_ID = Integer.MAX_VALUE;
    private static final String BASE_URI = "http://localhost:8081/spring-mvc-rest/users";

    @Autowired
    private RestTemplate template;

    @Test
    public void test_get_all_success(){
        ResponseEntity<User[]> response = template.getForEntity(BASE_URI, User[].class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        validateCORSHttpHeaders(response.getHeaders());
    }


    private User getLastUser(){
        ResponseEntity<User[]> response = template.getForEntity(BASE_URI, User[].class);
        User[] users = response.getBody();
        return users[users.length - 1];
    }

    private void validateCORSHttpHeaders(HttpHeaders headers) {
        assertEquals("*", headers.getAccessControlAllowOrigin());
        assertTrue(headers.getAccessControlAllowHeaders().contains("*"));
        assertEquals(3600L, headers.getAccessControlMaxAge());
        assertTrue(headers.getAccessControlAllowMethods().containsAll(
                Arrays.asList(
                        HttpMethod.GET,
                        HttpMethod.POST,
                        HttpMethod.PUT,
                        HttpMethod.OPTIONS,
                        HttpMethod.DELETE)
        ));
    }
}
