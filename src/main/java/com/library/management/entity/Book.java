package com.library.management.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "books")
@Data
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Integer stock; // Critical for logic

    @ManyToOne @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne @JoinColumn(name = "category_id")
    private Category category;
}