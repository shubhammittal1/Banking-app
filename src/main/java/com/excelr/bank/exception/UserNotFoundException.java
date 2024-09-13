package com.excelr.bank.exception;

// This class defines a custom exception for cases where a user is not found
public class UserNotFoundException extends Exception {

    // Constructor that accepts a message to describe the exception
    public UserNotFoundException(String message) {
        // Passes the message to the superclass constructor (Exception) to set the exception message
        super(message);
    }
}