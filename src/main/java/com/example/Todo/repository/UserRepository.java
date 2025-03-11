package com.example.Todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Todo.model.MyUser;

import java.util.Optional;

public interface UserRepository extends JpaRepository<MyUser, Long> {

    // Find a user by their username (for authentication)
    Optional<MyUser> findByUsername(String username);
}
