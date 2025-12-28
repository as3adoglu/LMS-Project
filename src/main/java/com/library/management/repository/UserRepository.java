package com.library.management.repository;

import com.library.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Query(value = "SELECT COALESCE(SUM(amount), 0) FROM penalties WHERE user_id = :userId", nativeQuery = true)
    BigDecimal getTotalDebt(@Param("userId") Long userId);
}