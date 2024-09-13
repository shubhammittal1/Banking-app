package com.excelr.bank.repository;

import com.excelr.bank.models.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<Otp,Long> {

    // Finds an Otp entity by the email address
    Otp findByEmail(String email);

    // Finds an Otp entity by the OTP value
    Otp findByOtp(int otp);
}
