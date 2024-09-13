package com.excelr.bank.exception;


// This class defines a custom unchecked exception for cases where an OTP (One-Time Password) is invalid
public class InvalidOtpException extends RuntimeException {

    // Default constructor that sets a predefined message for the exception
    public InvalidOtpException() {
        // Calls the constructor of the superclass (RuntimeException) with a predefined message
        super("Invalid OTP");
    }
}