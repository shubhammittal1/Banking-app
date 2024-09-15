package com.excelr.bank.security.services.interfaces;

import com.excelr.bank.models.Transaction;
import jakarta.transaction.InvalidTransactionException;

import java.util.List;

public interface TransactionService {
    // Create a new transaction
    Transaction insertRecord(Long customerId,Transaction transaction,Long accountId)
            throws InvalidTransactionException;
    // Retrieve a transaction by ID
    Transaction getTransactionById(Long id);
    // Retrieve all transactions
    List<Transaction> getAllTransactions();
    // Update an existing transaction
    Transaction updateTransaction(Long id, Transaction transactionDetails);
    // Delete a transaction by ID
    void deleteTransaction(Long id);

    List<Transaction> getStatement(Long userId,
                                   String startDate, String endDate);

    void downloadStatement(Long accountId, String startDate, String endDate);
}
