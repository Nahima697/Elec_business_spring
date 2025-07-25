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
    public boolean updateUser(@RequestBody AppUser appUser) {
        if(appUser.getId() == null){
            repository.save(appUser);
            return true;
        }
        return false;
    }
}
