package com.excelr.bank.controllers;

import com.excelr.bank.payload.request.PasswordResetRequest;
import com.excelr.bank.security.services.impl.PasswordResetServiceImpl;
import com.excelr.bank.security.services.impl.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

// This class handles requests related to password recovery and reset
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/password") // Base URL for all endpoints in this controller is "/password"
public class ForgotPasswordController {

    // Injects the service for handling user details
    @Autowired
    private UserDetailsServiceImpl userService;

    // Injects the service for handling forgot and reset password functionality


    @Autowired
    private PasswordResetServiceImpl passwordResetService;

    // Endpoint to handle forgot password requests
    @PostMapping("/forgot")
    public String forgotPass(@RequestParam String email) {
        // Calls the service method to handle forgot password logic
         passwordResetService.initiatePasswordReset(email);

        // Return the response, which is either the URL or an error message
        return "Otp Send SuccessFully";
    }

    // Endpoint to handle password reset requests
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPass(@Valid @RequestBody PasswordResetRequest request, BindingResult result) {
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