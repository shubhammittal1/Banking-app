package com.excelr.bank.repository;

import com.excelr.bank.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  // Finds a User entity by its username, returning an Optional to handle cases where the username might not exist
  Optional<User> findByUsername(String username);


  // Finds a User entity by its email address
  User findByEmail(String email);

  // Checks if a User entity exists with the given username
  Boolean existsByUsername(String username);

  // Checks if a User entity exists with the given email address
  Boolean existsByEmail(String email);

  // Finds a User entity by its token
//  User findByToken(String token);

  Optional<User> findByRoleType(String roleType);

}
