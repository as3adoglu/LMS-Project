package com.library.management.controller;

import com.library.management.entity.User;
import com.library.management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User register(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PostMapping("/login")
    public User login(@RequestBody User u) {
        return userService.login(u.getEmail(), u.getPassword());
    }

    @GetMapping("/{id}/debt")
    public BigDecimal getDebt(@PathVariable Long id) {
        return userService.getUserDebt(id);
    }

    @PostMapping("/{id}/ban")
    public void ban(@PathVariable Long id) {
        userService.banUser(id);
    }

    @PostMapping("/{id}/unban")
    public void unban(@PathVariable Long id) {
        userService.unbanUser(id);
    }

    // --- NEW: PAY DEBT ENDPOINT ---
    @PostMapping("/{id}/pay")
    public void payDebt(@PathVariable Long id) {
        userService.payUserDebt(id);
    }
}