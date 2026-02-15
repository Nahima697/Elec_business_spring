package com.elec_business.controller;

import com.elec_business.business.UserRoleBusiness;
import com.elec_business.entity.User;
import com.elec_business.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repository;
    private final UserRoleBusiness userRoleBusiness;


    @Autowired
    public UserController(UserRepository repository, UserRoleBusiness userRoleBusiness) {
        this.repository = repository;
        this.userRoleBusiness = userRoleBusiness;
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

    @PostMapping("/{id}/roles/owner")
    public ResponseEntity<Void> addOwnerRole(@PathVariable String id) {
        userRoleBusiness.addRoleOwner(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/roles/renter")
    public ResponseEntity<Void> addRenterRole(@PathVariable String id) {
        userRoleBusiness.addRoleRenter(id);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{id}/roles/{roleName}")
    public ResponseEntity<Void> removeRole(
            @PathVariable String id,
            @PathVariable String roleName
    ) {
        userRoleBusiness.removeRole(id, roleName);
        return ResponseEntity.noContent().build();
    }

}
