package com.library.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "penalties")
@Data
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relates the fine to a specific User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Double amount;
    private String reason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Automatically set the date when the fine is created
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}