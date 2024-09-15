package com.excelr.bank.controllers;

import com.excelr.bank.models.Transaction;
import com.excelr.bank.security.services.impl.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// This class handles transaction-related HTTP requests
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/transaction") // Base URL for all endpoints in this controller is "/api/transaction"
public class TransactionController {

    // Injects the service that handles transaction operations
    @Autowired
    private TransactionServiceImpl transactionService;

    // Endpoint to retrieve a transaction by its ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')  or hasRole('ROLE_ADMIN')")
    public Transaction getTransactionById(@PathVariable Long id) {
        // Calls the service method to get a transaction by its ID
        return transactionService.getTransactionById(id);
    }

    // Endpoint to retrieve all transactions
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_USER')  or hasRole('ROLE_ADMIN')")
    public List<Transaction> getAllTransactions() {
        // Calls the service method to get a list of all transactions
        return transactionService.getAllTransactions();
    }

    // Endpoint to update a transaction by its ID
    @PutMapping("/{id}")
    @PreAuthorize(" hasRole('ROLE_ADMIN')")
    public Transaction updateTransaction(@PathVariable Long id, @RequestBody Transaction transactionDetails) {
        // Calls the service method to update a transaction with the given ID
        return transactionService.updateTransaction(id, transactionDetails);
    }

    // Endpoint to delete a transaction by its ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteTransaction(@PathVariable Long id) {
        // Calls the service method to delete a transaction by its ID
        transactionService.deleteTransaction(id);
    }

    @GetMapping("/generate/statement")
    public ResponseEntity<?> generateStatement(@RequestBody Transaction transaction) throws NullPointerException{
        if(transaction.getTransactionId()!=null && transaction.getStartDate()!=null && transaction.getEndDate()!=null){
            transactionService.getStatement(transaction.getTransactionId(),transaction.getStartDate(),transaction.getEndDate());
            return ResponseEntity.status(HttpStatus.OK).body("Statement Generated Succesfully");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Data");
        }
    }

    @GetMapping("/download/statement")
    public ResponseEntity<?> downloadStatement(@RequestBody Transaction transaction) throws  NullPointerException{
        if(transaction.getTransactionId()!=null && transaction.getStartDate()!=null && transaction.getEndDate()!=null){
            transactionService.downloadStatement(transaction.getTransactionId(),transaction.getStartDate(),transaction.getEndDate());
            return ResponseEntity.status(HttpStatus.OK).body("Statement Downloaded Succesfully");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Data");        }
    }
}