package com.elec_business.controller;

import com.elec_business.entity.User;
import com.elec_business.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repository;

    @Autowired
    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<User> getUsers() {
        return repository.findAll();
    }

    @PostMapping
    public boolean updateUser(@RequestBody User user) {
        if(user.getId() == null){
            repository.save(user);
            return true;
        }
        return false;
    }
}
