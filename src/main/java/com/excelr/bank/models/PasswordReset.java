package com.excelr.bank.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class PasswordReset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Primary key for the password reset token entity
    Long Id;

    private String email;
    // Ensures password contains at least one lowercase letter, one uppercase letter, one digit,
    // one special character, and is at least 8 characters long.
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$", message = "Password must be Combination of Capital letter, special Character,small letter and number must be 8 and above characters long")
    private String password;

    // Confirm password field with validation pattern
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$", message = "Password must be Combination of Capital letter, special Character,small letter and number must be 8 and above characters long")
    private String confirmPassword;

    @Column(name="password_reset_otp")
    // Otp used for password reset
    private int resetPasswordOtp;

    @CreationTimestamp
    // Timestamp when the password reset entry was created
    private LocalDateTime createdAt;


    // Timestamp when the token expires
    private LocalDateTime expiredAt;

    // Lifecycle callback that sets `tokenCreationDate` before persisting or updating the entity.
    @PrePersist
    @PreUpdate
    public void preUpdate() {
        // If `expiredAt` is null
        this.createdAt = LocalDateTime.now();
        if (this.expiredAt == null) {
            //it initializes `expiredAt` to 30 minutes after `tokenCreatedAt`.
            this.expiredAt = this.createdAt.plusMinutes(30);
        }
    }

    @Transient
    // Checks if the reset token has expired by comparing the current time with `expiredAt`.
    public boolean isTokenExpired() {
        // Returns true if the current time is after `expiredAt`, otherwise returns false.
        return LocalDateTime.now().isAfter(expiredAt);
    }

    @Transient
    // Checks if the `password` matches `confirmPassword`.
    public boolean isPasswordMatching() {
        // Returns true if both fields are equal, otherwise returns false.
        return password != null && password.equals(confirmPassword);
    }

}