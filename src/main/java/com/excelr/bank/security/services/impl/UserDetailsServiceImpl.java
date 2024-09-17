package com.excelr.bank.security.services.impl;

import com.excelr.bank.exception.UserNotFoundException;
import com.excelr.bank.models.Otp;
import com.excelr.bank.models.User;
import com.excelr.bank.repository.OtpRepository;
import com.excelr.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;  // Repository for accessing User data

  @Autowired
  private OtpRepository otpPasswordRepository;  // Repository for accessing OTP data

  @Autowired
  public PasswordEncoder passwordEncoder;  // Password encoder for encoding passwords


  // Method to load user details by username, required by Spring Security
  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

    // Convert User entity to UserDetailsImpl instance
    return UserDetailsImpl.build(user);
  }

  // Method to retrieve all users from the repository
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public User getUserById(Long id) {
    return userRepository.findById(id).orElse(null);
  }
  // Method to retrieve a users by their ID
  public List<User> getUsersByUsername(String name) {
    return userRepository.findByUsername(name).map(Collections::singletonList)
            .orElse(Collections.emptyList());
  }

  // Method to retrieve a users by their ID
  public List<User> getByRoleType(String roleType) {
    return userRepository.findByRoleType(roleType).map(Collections::singletonList)
            .orElse(Collections.emptyList());
  }

  // Method to retrieve an OTP by its value
  public Otp getOtp(int otp) {
    return otpPasswordRepository.findByOtp(otp);
  }

  // Method to update user details
  public ResponseEntity<?> updateUser(Long id, User userDetails) {
    User user = userRepository.findById(id).orElse(null);
    if (user != null) {
      // Update user fields with provided details
      user.setUsername(userDetails.getUsername());
      user.setEmail(userDetails.getEmail());
      user.setPassword(userDetails.getPassword());
      user.setPhoneNo(userDetails.getPhoneNo());
      // Save the updated user to the repository
      User userUpdtRecord=userRepository.save(user);
      return ResponseEntity.status(HttpStatus.OK).body("Successfully Updated!!! Update Details "+userUpdtRecord);
    } else {
      return null;  // Return null if user is not found
    }
  }

  // Method to delete a user by their ID
  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

}