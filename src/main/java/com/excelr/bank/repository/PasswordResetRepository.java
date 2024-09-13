package com.excelr.bank.repository;

import com.excelr.bank.models.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset,Long> {
    // Finds a PasswordReset entity by the reset password OTP
    PasswordReset findByResetPasswordOtp(int otp);

    // Finds a PasswordReset entity by the email address
    PasswordReset findByEmail(String email);
}
