package com.aoher.controller;

import com.aoher.model.User;
import com.aoher.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        logger.info("getting all users");
        List<User> users = userService.getAll();

        return users == null || users.isEmpty() ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<User> getById(@PathVariable("id") int id) {
        logger.info("getting user with id: {}", id);
        User user = userService.findById(id);

        if (user == null) {
            logger.info("user with id {} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody User user, UriComponentsBuilder ucBuilder) {
        logger.info("creating new user: {}", user);

        if (userService.exists(user)) {
            logger.info("a user with name {} already exists", user.getUsername());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        userService.create(user);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ucBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri());
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<User> update(@PathVariable int id, @RequestBody User user) {
        logger.info("updating user: {}", user);
        User currentUser = userService.findById(id);

        if (currentUser == null) {
            logger.info("User with id {} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        currentUser.setId(user.getId());
        currentUser.setUsername(user.getUsername());

        userService.update(user);
        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        logger.info("deleting user with id: {}", id);
        User user = userService.findById(id);

        if (user == null) {
            logger.info("Unable to delete. User with id {} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
