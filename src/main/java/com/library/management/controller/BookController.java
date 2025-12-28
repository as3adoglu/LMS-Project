package com.library.management.controller;
import com.library.management.entity.Book;
import com.library.management.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/books")
@RequiredArgsConstructor @CrossOrigin("*")
public class BookController {
    private final BookRepository repo;
    @GetMapping public List<Book> getAll() { return repo.findAll(); }
    @PostMapping public Book add(@RequestBody Book b) { return repo.save(b); }
}