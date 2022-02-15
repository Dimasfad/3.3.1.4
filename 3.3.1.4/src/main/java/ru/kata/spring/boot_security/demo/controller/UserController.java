package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.Service.RoleService;
import ru.kata.spring.boot_security.demo.Service.UserService;
import ru.kata.spring.boot_security.demo.model.User;

@Controller
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/")
    public String login(){
        return "redirect:/login";
    }

    @GetMapping("/admin")
    public String getListUsers(@AuthenticationPrincipal UserDetails userDetails, Model model){
        String name = userDetails.getUsername();
        User user = userService.getByName(name);
        model.addAttribute("user", user);
        model.addAttribute("userList", userService.users());
        model.addAttribute("userr", new User());
        model.addAttribute("roleList", roleService.getAllRoles());
        return "admin";
    }

    @PostMapping(value="/admin/add")
    public String saveUser(@ModelAttribute User userr,
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
        return "redirect:/admin";
    }

    @PatchMapping(value="/admin/edit/{id}")
    public String updateUser(@ModelAttribute User user,
                             @RequestParam(value = "checked", required = false ) Long[] checked) {
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
        return "redirect:/admin";
    }

    @DeleteMapping("/delete/{id}")
    public String getUserId(@PathVariable(value="id")Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }

    @GetMapping("admin/form/edit/{id}")
    public String updateUserForm(@PathVariable(value="id") Long id,
                                 Model model) {
        User user = userService.getById(id);
        model.addAttribute("roleList", roleService.getAllRoles());
        model.addAttribute("user", user);
        return "user_edit_form.html";
    }

    @GetMapping("admin/form/delete/{id}")
    public String deleteUserForm(@PathVariable(value="id") Long id,
                                 Model model) {
        User user = userService.getById(id);
        model.addAttribute("roleList", roleService.getAllRoles());
        model.addAttribute("user", user);
        return "user_delete_form.html";
    }

    @GetMapping("admin/form/new")
    public String newUserForm(Model model) {
        model.addAttribute("roleList", roleService.getAllRoles());
        model.addAttribute("userr", new User());
        return "user_new_form.html";
    }

    @GetMapping("/user")
    public String getUserInfo(@AuthenticationPrincipal UserDetails userDetails,
                              Model model){
        String name = userDetails.getUsername();
        User user = userService.getByName(name);
        model.addAttribute("user", user);
        return "user";
    }

}
