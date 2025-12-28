package com.library.management.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity @Table(name = "authors") @Data
public class Author {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String bio;
}