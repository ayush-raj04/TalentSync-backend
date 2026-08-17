package com.example.Talentsync.repository;

import com.example.Talentsync.entity.Role;
import com.example.Talentsync.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(Role role);

    User findByResetToken(String resetToken);

    boolean existsByResetToken(String resetToken);
}