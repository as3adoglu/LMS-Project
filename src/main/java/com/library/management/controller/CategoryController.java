package com.library.management.controller;
import com.library.management.entity.Category;
import com.library.management.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/categories")
@RequiredArgsConstructor @CrossOrigin("*")
public class CategoryController {
    private final CategoryRepository repo;
    @GetMapping public List<Category> getAll() { return repo.findAll(); }
}