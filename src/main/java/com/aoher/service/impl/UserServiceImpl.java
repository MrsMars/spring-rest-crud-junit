package com.aoher.service.impl;

import com.aoher.model.User;
import com.aoher.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserServiceImpl implements UserService {

    private static final AtomicInteger counter = new AtomicInteger();

    static List<User> users = new ArrayList<>(Arrays.asList(
            new User(counter.incrementAndGet(), "Daenerys Targaryen"),
            new User(counter.incrementAndGet(), "John Snow"),
            new User(counter.incrementAndGet(), "Arya Stark"),
            new User(counter.incrementAndGet(), "Cersei Baratheon"))
    );

    @Override
    public List<User> getAll() {
        return users;
    }

    @Override
    public User findById(int id) {
        return users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }

    @Override
    public User findByName(String name) {
        return users.stream().filter(u -> u.getUsername().equals(name)).findFirst().orElse(null);
    }

    @Override
    public void create(User user) {
        if (user != null) {
            user.setId(counter.incrementAndGet());
            users.add(user);
        }
    }

    @Override
    public void update(User user) {
        if (user != null) {
            int index = users.indexOf(user);
            users.set(index, user);
        }
    }

    @Override
    public void delete(int id) {
        User user = findById(id);
        users.remove(user);
    }

    @Override
    public boolean exists(User user) {
        if (user != null) {
            return findByName(user.getUsername()) != null;
        }
        return false;
    }
}
