package com.library.management.service;

import com.library.management.entity.*;
import com.library.management.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class BorrowService {
    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BorrowService(BorrowRepository br, BookRepository bkr, UserRepository ur) {
        this.borrowRepository = br; this.bookRepository = bkr; this.userRepository = ur;
    }

    public List<BorrowTransaction> getAll() { return borrowRepository.findAll(); }

    public BorrowTransaction borrowBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));

        // 10/10 RULE: Check Debt
        BigDecimal debt = userRepository.getTotalDebt(userId);
        if (debt.compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Borrow Denied: User has unpaid fines!");
        }

        // 10/10 RULE: Check Loan Count (Limit to 3 books)
        List<BorrowTransaction> activeLoans = borrowRepository.findAll().stream()
                .filter(t -> t.getUser().getId().equals(userId) && t.getReturnDate() == null)
                .toList();
        if (activeLoans.size() >= 3) {
            throw new RuntimeException("Borrow Denied: Maximum limit of 3 books reached!");
        }

        if (book.getStock() <= 0) throw new RuntimeException("Out of Stock");

        book.setStock(book.getStock() - 1);
        bookRepository.save(book);

        BorrowTransaction trans = new BorrowTransaction();
        trans.setUser(user);
        trans.setBook(book);
        trans.setBorrowDate(LocalDate.now());
        trans.setDueDate(LocalDate.now().plusDays(10));
        return borrowRepository.save(trans);
    }

    public BorrowTransaction returnBook(Long id) {
        BorrowTransaction trans = borrowRepository.findById(id).orElseThrow();
        if (trans.getReturnDate() != null) throw new RuntimeException("Already returned");
        trans.setReturnDate(LocalDate.now());
        Book book = trans.getBook();
        book.setStock(book.getStock() + 1);
        bookRepository.save(book);
        return borrowRepository.save(trans);
    }
}