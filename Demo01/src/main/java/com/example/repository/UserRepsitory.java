package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.User;


public interface UserRepsitory extends JpaRepository<User,Long> {
    
}
