package com.library.management.controller;
import com.library.management.entity.Author;
import com.library.management.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/authors")
@RequiredArgsConstructor @CrossOrigin("*")
public class AuthorController {
    private final AuthorRepository repo;
    @GetMapping public List<Author> getAll() { return repo.findAll(); }
    @PostMapping public Author add(@RequestBody Author a) { return repo.save(a); }
}