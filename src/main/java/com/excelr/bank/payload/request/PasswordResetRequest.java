package com.excelr.bank.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequest {
        @NotNull
        private String email;
        private String otp;
        private String password;
        @NotNull
        private String confirmPassword;

        public PasswordResetRequest(String email, String password, String confirmPassword) {
                this.email = email;
                this.password = password;
                this.confirmPassword = confirmPassword;
        }

// Getters and setters

}
