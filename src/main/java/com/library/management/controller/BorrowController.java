package com.library.management.controller;

import com.library.management.entity.BorrowTransaction;
import com.library.management.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/borrow")
@RequiredArgsConstructor @CrossOrigin("*")
public class BorrowController {
    private final BorrowService borrowService;

    @GetMapping public List<BorrowTransaction> getAll() { return borrowService.getAll(); }

    @PostMapping("/{userId}/{bookId}")
    public BorrowTransaction borrow(@PathVariable Long userId, @PathVariable Long bookId) {
        return borrowService.borrowBook(userId, bookId);
    }

    @PostMapping("/return/{id}")
    public BorrowTransaction returnBook(@PathVariable Long id) {
        return borrowService.returnBook(id);
    }
}