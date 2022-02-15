package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.Service.RoleService;
import ru.kata.spring.boot_security.demo.Service.UserService;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

@RestController
public class RestUserController {

    private final UserService userService;
    private final RoleService roleService;

    public RestUserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/api/users")
    public List<User> getListUsers(){
        List<User> users = userService.users();
        return users;
    }

    @PatchMapping("/api/users/{id}")
    public User updateUser(@ModelAttribute User user,
                           @PathVariable(value="id") Long id,
                           @RequestParam(value = "checked", required = false ) Long[] checked) {

        System.out.println(user.getUsername() + user.getPassword() + user.getId());
        System.out.println(id);
        System.out.println(checked);

        if (checked == null) {
            user.setOneRole(roleService.getRoleByName("USER"));
            userService.edit(user);
        } else {
            for (Long ong : checked) {
                if (ong != null) {
                    user.setOneRole(roleService.getRoleByID(ong));
                    userService.edit(user);
                }
            }
        }
        return user;
    }

    @DeleteMapping("/api/users/{id}")
    public String deleteUserForm(@PathVariable(value="id") Long id) {
        userService.delete(id);
        return "DELETED";  // TODO: Better to return the deleted user or a simple string?
    }

    @PostMapping(value="/api/users")
    public User saveUser(@ModelAttribute User userr,
                           @RequestParam(value = "checked", required = false ) Long[] checked){
        // TODO: refactor new user instantization
        //      User userr = new User();
        if (checked == null) {
            userr.setOneRole(roleService.getRoleByName("USER"));
        } else {
            for (Long ong : checked) {
                if (ong != null) {
                    userr.setOneRole(roleService.getRoleByID(ong));
                }
            }
        }
        userService.add(userr);
        return userr;
    }

}
