package com.library.management.repository;
import com.library.management.entity.BorrowTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BorrowRepository extends JpaRepository<BorrowTransaction, Long> {}