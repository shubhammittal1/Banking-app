package com.excelr.bank.security.services.impl;

import com.excelr.bank.exception.UserNotFoundException;
import com.excelr.bank.models.Otp;
import com.excelr.bank.models.User;
import com.excelr.bank.repository.OtpRepository;
import com.excelr.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

  // Method to retrieve an OTP by its value
  public Otp getOtp(int otp) {
    return otpPasswordRepository.findByOtp(otp);
  }

  // Method to update user details
  public User updateUser(Long id, User userDetails) {
    User user = userRepository.findById(id).orElse(null);
    if (user != null) {
      // Update user fields with provided details
      user.setUsername(userDetails.getUsername());
      user.setEmail(userDetails.getEmail());
      user.setPassword(userDetails.getPassword());
      user.setPhoneNo(userDetails.getPhoneNo());
      // Save the updated user to the repository
      return userRepository.save(user);
    } else {
      return null;  // Return null if user is not found
    }
  }

  // Method to delete a user by their ID
  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  // Method to generate a random OTP
  private String generateOtp() {
    SecureRandom random = new SecureRandom();
    // Generate a 6-digit OTP
    return String.format("%06d", random.nextInt(1000000));
  }

  // Method to send an email with a reset link to the user
  public String sendEmail(User user) {
    String resetLink = generateOtp();  // Generate OTP
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom("demobankingapp@gmail.com");  // Sender's email address
      message.setTo(user.getEmail());  // Recipient's email address
      message.setSubject("Welcome to ExcelR Banking Services");  // Subject of the email
      message.setText("To Reset the Password Click on the Link Below " + resetLink);  // Email body
      return "Success";  // Return success message
    } catch (Exception e) {
      e.printStackTrace();  // Print stack trace for debugging
      return "error";  // Return error message
    }
  }

  // Method to update the reset password OTP for a user
  public void updateResetPasswordOtp(int otp, String email) throws UserNotFoundException {
    User user = userRepository.findByEmail(email);  // Fetch user by email
    Otp passwordResetOtp = otpPasswordRepository.findByEmail(email);  // Fetch OTP by email
    if (user != null) {
      // Update OTP associated with the user
      passwordResetOtp.setOtp(otp);
      userRepository.save(user);  // Save user with updated OTP
    } else {
      // Throw exception if user is not found
      throw new UsernameNotFoundException("Could not find any User with the email " + email);
    }
  }

  // Method to retrieve a user by their reset password token
  public User getByResetPasswordToken(String token) {
    return userRepository.findByToken(token);
  }

  // Method to update a user's password
  public void updatePassword(User user, String newPassword) {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();  // Create a new password encoder
    String encodedPassword = passwordEncoder.encode(newPassword);  // Encode the new password
    user.setPassword(encodedPassword);  // Set the encoded password to the user
    userRepository.save(user);  // Save user with updated password
  }
}