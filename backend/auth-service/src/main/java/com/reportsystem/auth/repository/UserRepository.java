package com.reportsystem.auth.repository;

import com.reportsystem.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // Method er naam dekhe Spring nijei SQL banay
    User findByEmail(String email);
}