package com.excelr.bank.security.services.interfaces;

import com.excelr.bank.models.Account;
import jakarta.transaction.InvalidTransactionException;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    // Create a new account
     Account createAccount(Account account);

     void deposit(Long accountId, BigDecimal amount, String narration) throws InvalidTransactionException;

     void withdraw(Long accountId, BigDecimal amount, String narration) throws InvalidTransactionException;

    //Get Account Details by Accountid
     Account getAccountDataById(Long id);

    //Search Account by id and update Account Details
     Account updateAccount(Long id, Account accountDetails);

    //Get All Records Stored in Database
    public List<Account> getAllRecords();
}
