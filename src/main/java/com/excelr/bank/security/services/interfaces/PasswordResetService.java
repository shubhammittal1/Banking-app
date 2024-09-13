package com.excelr.bank.security.services.interfaces;

import com.excelr.bank.payload.request.PasswordResetRequest;
import org.springframework.http.ResponseEntity;

public interface PasswordResetService {

   ResponseEntity<?> initiatePasswordReset(String email);

   void resetPassword(PasswordResetRequest request);

}
