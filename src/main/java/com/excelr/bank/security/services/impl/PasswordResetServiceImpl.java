package com.excelr.bank.security.services.impl;

import com.excelr.bank.models.EmailDetails;
import com.excelr.bank.models.Otp;
import com.excelr.bank.models.User;
import com.excelr.bank.payload.request.PasswordResetRequest;
import com.excelr.bank.repository.OtpRepository;
import com.excelr.bank.repository.UserRepository;
import com.excelr.bank.security.services.interfaces.PasswordResetService;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

// Service class responsible for handling password reset functionality
@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    // Injects the UserRepository to interact with the user data
    @Autowired
    private UserRepository userRepository;

    // Injects the OtpRepository to interact with the OTP data
    @Autowired
    private OtpRepository otpRepository;

    // Injects the UserDetailsServiceImpl to access user details
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // Injects EmailServiceImpl for sending emails
    @Autowired
    EmailServiceImpl emailService;

    // Encoder for password hashing
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Initiates the password reset process by generating an OTP and sending a reset email
    public ResponseEntity<?> initiatePasswordReset(String email) {
        // Finds the user by email
        User user = userRepository.findByEmail(email);

        // If user exists, generate OTP and send reset email
        if (user != null) {
            // Generate a 6-digit OTP
            Random random = new Random();
            int OTP = 100000 + random.nextInt(900000);
            String fontOTP=String.valueOf(OTP);
            String enlargedOTP = fontOTP.toUpperCase(); // Convert to uppercase for larger appearance
            String note="\n".repeat(8) +"Note : If  it's  not  You  Ignore \n";
            String enlargedNote=note.toUpperCase();
            String enlargedOTPWithUnicode = "";
            for (char c : enlargedOTP.toCharArray()) {
                enlargedOTPWithUnicode += c + "\u2000\u2000"; // Add a Unicode space character after each character
            }

            // Create and save OTP entity
            Otp resetPasswordOtp = new Otp();
            resetPasswordOtp.setOtp(OTP);
            resetPasswordOtp.setEmail(email);

            // Create the reset URL
            String msg = "\t$$$$-Welcome To ExcelR Banking Services-$$$$"+"\n".repeat(5) +
                    " ".repeat(20)+"Your  OTP  to  Reset  The  Password"+"\n".repeat(5) +
                    " ".repeat(35)+ enlargedOTPWithUnicode+ enlargedNote ;


            // Save OTP to the database
            otpRepository.save(resetPasswordOtp);

            // Prepare email details
            EmailDetails emailDetails = new EmailDetails();
            emailDetails.setSubject("Password Reset Request");
            emailDetails.setMsgBody(msg);
            emailDetails.setRecipient(email);

            // Send the email
            String response = emailService.sendSimpleMail(emailDetails);

            // Return success response
            return ResponseEntity.status(HttpStatus.OK).body("Password Reset Link Sent Successfully");
        } else {
            // Return error response if email is not found
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email Id Mismatch");
        }
    }

    // Resets the user's password using the provided OTP and new password
    public void resetPassword(PasswordResetRequest request) {
        String str=request.getOtp();
        int otp =Integer.parseInt(str);
        String newPassword=request.getConfirmPassword();
        // Find OTP entry in the database
        Otp resetOtp = otpRepository.findByOtp(otp);

        userDetailsService.getOtp(otp);
        // Validate OTP and its expiration
        if ( resetOtp != null && !resetOtp.isOtpExpired()) {
            // Find the user associated with the OTP
            User user = userRepository.findByEmail(resetOtp.getEmail());
            if (user != null) {
                // Update the user's password
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
            }
        } else {
            // Throw exception if OTP is invalid or expired
            throw new RuntimeException("Invalid or expired OTP.");
        }
    }

    //    @Override
//    public String generateToken() {
//        return UUID.randomUUID().toString() +
//                UUID.randomUUID().toString();
//    }
//
//    public boolean isTokenExpired(final LocalDateTime tokenCreationDate) {
//
//        LocalDateTime now = LocalDateTime.now();
//        Duration duration = Duration.between(tokenCreationDate, now);
//
//        return duration.toMinutes() >= EXPIRE_TOKEN;
//    }
}
