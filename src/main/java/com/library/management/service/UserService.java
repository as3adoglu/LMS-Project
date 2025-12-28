package com.library.management.service;

import com.library.management.entity.User;
import com.library.management.repository.UserRepository;
import com.library.management.repository.PenaltyRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PenaltyRepository penaltyRepository; // Added this

    public UserService(UserRepository userRepository, PenaltyRepository penaltyRepository) {
        this.userRepository = userRepository;
        this.penaltyRepository = penaltyRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setActive(true);
        return userRepository.save(user);
    }

    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new RuntimeException("User not found!");
        if (!user.isActive()) throw new RuntimeException("ACCOUNT BANNED");

        if (BCrypt.checkpw(rawPassword, user.getPassword())) {
            user.setToken(UUID.randomUUID().toString());
            return userRepository.save(user);
        }
        throw new RuntimeException("Invalid Password");
    }

    public void banUser(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setActive(false);
        userRepository.save(user);
    }

    public void unbanUser(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setActive(true);
        userRepository.save(user);
    }

    public BigDecimal getUserDebt(Long userId) {
        return userRepository.getTotalDebt(userId);
    }

    // --- NEW: CLEAR DEBT LOGIC ---
    public void payUserDebt(Long userId) {
        penaltyRepository.deleteByUserId(userId);
    }
}