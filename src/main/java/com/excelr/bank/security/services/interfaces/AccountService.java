package com.excelr.bank.security.services.interfaces;

import com.excelr.bank.exception.InsufficientBalanceException;
import com.excelr.bank.models.Account;
import com.excelr.bank.models.Transaction;
import com.excelr.bank.payload.request.MobileRechargeRequest;
import jakarta.transaction.InvalidTransactionException;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    // Create a new account
     ResponseEntity<?> createAccount(Account account, Long userId);

     ResponseEntity<?> deposit(String accountNumber, Transaction transaction) throws InvalidTransactionException;

     ResponseEntity<?> withdraw(String accountNumber, Transaction transaction) throws InvalidTransactionException, InsufficientBalanceException;

    ResponseEntity<?> electricityBill(String accNumber, MobileRechargeRequest request, Transaction transaction) throws InvalidTransactionException;

    ResponseEntity<?> recharge(String accNumber, MobileRechargeRequest request, Transaction transaction)throws InvalidTransactionException;
        //Get Account Details by Accountid
     Account getAccountDataById(Long id);

    //Search Account by id and update Account Details
     Account updateAccount(Long id, Account accountDetails);

    //Get All Records Stored in Database
    public List<Account> getAllRecords();
}
