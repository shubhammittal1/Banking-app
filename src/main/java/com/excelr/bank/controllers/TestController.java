package com.excelr.bank.controllers;

import com.excelr.bank.models.User;
import com.excelr.bank.security.services.impl.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Allow cross-origin requests from any origin and set the cache duration for pre-flight responses
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController // Indicate that this class is a Spring MVC controller and responses are returned as JSON
@RequestMapping("/api") // Base URL for all request mappings in this controller
public class TestController {

  // Autowire the UserDetailsServiceImpl to manage user-related operations
  @Autowired
  private UserDetailsServiceImpl userService;

  // Endpoint to get all users, accessible only by users with 'ROLE_MODERATOR' or 'ROLE_ADMIN'
  @GetMapping("/all")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<List<User>> getAllUsers() {
    // Retrieve all users from the userService and return them in the response body
    return ResponseEntity.ok(userService.getAllUsers());
  }

  // Endpoint to get user content, accessible by users with 'ROLE_USER', 'ROLE_MODERATOR', or 'ROLE_ADMIN'
  @GetMapping("/user")
  @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
  public String userAccess() {
    // Return a string indicating user-level content access
    return "User Content.";
  }

  // Endpoint to get admin content, accessible only by users with 'ROLE_ADMIN'
  @GetMapping("/admin")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public String adminAccess() {
    // Return a string indicating admin-level content access
    return "Admin Board: Has all Functionality";
  }

  // Endpoint to get a user by ID, accessible by users with 'ROLE_USER' or 'ROLE_ADMIN'
  @GetMapping("/getUserById/{id}")
  @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
  public ResponseEntity<?> getUserById(@PathVariable Long id) {
      try {
        // Retrieve the user by ID from the userService
        return ResponseEntity.ok(userService.getUserById(id));
      } catch (Exception e) {
        // Return a 404 Not Found response if the user is not found
        return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/getUserByName/{username}")
  @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
  public ResponseEntity<?> getUserByName(@PathVariable String username) {
    try {
      // Retrieve the user by ID from the userService
      return ResponseEntity.ok(userService.getUsersByUsername(username));
    } catch (Exception e) {
      // Return a 404 Not Found response if the user is not found
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/getUserByRoleType/{roleType}")
  @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
  public ResponseEntity<?> getUserByRoleType(@PathVariable String roleType) {
    try {
      // Retrieve the user by ID from the userService
      return ResponseEntity.ok(userService.getByRoleType(roleType));
    } catch (Exception e) {
      // Return a 404 Not Found response if the user is not found
      return ResponseEntity.notFound().build();
    }
  }

  // Endpoint to update a user, accessible by users with 'ROLE_ADMIN' or 'ROLE_MODERATOR'
  @PutMapping("/update/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN') ")
  public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
    try {
      // Update the user with the provided details and return the updated user
      return userService.updateUser(id, userDetails);
    } catch (Exception e) {
      // Return a 400 Bad Request response if the update fails
      return ResponseEntity.badRequest().body("Update failed: " + e.getMessage());
    }
  }

  // Endpoint to delete a user, accessible only by users with 'ROLE_ADMIN'
  @DeleteMapping("/delete/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<?> deleteUser(@PathVariable Long id) {
    try {
      // Delete the user by ID
      userService.deleteUser(id);
      // Return a success message upon successful deletion
      return ResponseEntity.ok("Deleted successfully");
    } catch (Exception e) {
      // Return a 400 Bad Request response if the deletion fails
      return ResponseEntity.badRequest().body("Deletion failed: " + e.getMessage());
    }
  }
}