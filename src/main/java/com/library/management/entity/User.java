package com.library.management.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) private String username;
    private String password;
    @Column(unique = true) private String email;
    private String role; // ADMIN or USER
    private String token;

    @Column(name = "is_active")
    private boolean active = true;

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}