package com.excelr.bank.controllers;

import com.excelr.bank.payload.request.PasswordResetRequest;
import com.excelr.bank.security.services.impl.PasswordResetServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

// This class handles password reset operations
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/password") // Base URL for all endpoints in this controller is "/password"
public class PasswordResetController {

    // Injects the service that handles password reset operations
    @Autowired
    private PasswordResetServiceImpl passwordResetService;

    // Endpoint to initiate the password reset process
    @PostMapping("/reset")
    public ResponseEntity<?> initiatePasswordReset(@RequestParam String email) {

        // Check if the email is null or empty
        if (email == null || email.isEmpty()) {
            // Return a 400 Bad Request response with an error message if email is invalid
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email cannot be null or empty");
        } else {
            // Otherwise, call the service to handle the password reset initiation
            return passwordResetService.initiatePasswordReset(email);
        }
    }

    // Endpoint to change the password after receiving an OTP
    @PutMapping("/change")
    public ResponseEntity<?> changePassword( @Valid @RequestBody PasswordResetRequest request, BindingResult result) {
        // Check if there are validation errors in the request
        if (result.hasErrors()) {
            // Return a validation error message if validation fails
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation Failed");
        }
        // Check if the new password matches the confirmation password
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            // Return an error message if passwords do not match
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password do not Match");        }
        // Call the service to reset the password with the given OTP and new password
        passwordResetService.resetPassword(request);
        // Return a success message if the password is successfully changed
        return ResponseEntity.status(HttpStatus.OK).body("Password Successfully Updated");
    }
}