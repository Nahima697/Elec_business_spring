package com.elec_business.controller;

import com.elec_business.entity.AppUser;
import com.elec_business.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class AppUserController {

    private final AppUserRepository repository;

    @Autowired
    public AppUserController(AppUserRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<AppUser> getUsers() {
        return repository.findAll();
    }

    @PostMapping
    public AppUser createUser(@RequestBody AppUser appUser) {
        return repository.save(appUser);
    }
}
