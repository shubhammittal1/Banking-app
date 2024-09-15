package com.excelr.bank.security.services.impl;

import com.excelr.bank.exception.InsufficientBalanceException;
import com.excelr.bank.models.Account;
import com.excelr.bank.models.Transaction;
import com.excelr.bank.models.User;
import com.excelr.bank.repository.AccountRepository;
import com.excelr.bank.repository.UserRepository;
import com.excelr.bank.security.services.interfaces.AccountService;
import com.excelr.bank.util.Generator;
import jakarta.transaction.InvalidTransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// Service class that implements AccountService to handle business logic related to accounts
@Service
public class AccountServiceImpl implements AccountService {

    // Injects the AccountRepository to interact with account data
    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private UserRepository userRepository;

    // Creates a new account and saves it to the repository
    @Override
    public ResponseEntity<?> createAccount(Account account, Long userId) throws  IllegalArgumentException {
        Optional<Account> chkAcc = accountRepo.findById(userId);
        if (chkAcc.isEmpty()) {
            Optional<User> user = userRepository.findById(userId);
            System.out.print("User Data"+user);
            account.setAccountHolderName(user.get().getUsername());
            Generator generate = new Generator();
            account.setAccountId(generate.generateID());
            account.setAccountNumber(generate.generateAcc());
            account.setStatus("Active");
            account.setUserId(user.get().getUserId());
            if (account.getBalance() == null) {
                account.setBalance(BigDecimal.valueOf(0));
            }
            System.out.println("Account Data"+account);
            accountRepo.save(account);
            return ResponseEntity.status(HttpStatus.OK).body("Account Generated Successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Account Already Exist");
        }
            // Save and return the newly created account
    }

    // Retrieves account data by its ID
    @Override
    public Account getAccountDataById(Long id) {
        // Finds the account by ID and throws an exception if not found
        Optional<Account> optAccount = accountRepo.findById(id);
        return optAccount.orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
    }

    @Override
    public ResponseEntity<?> deposit(Long accountId,Transaction transaction, String narration) throws InvalidTransactionException {
        System.out.print("accountId"+accountId);
        Account account = accountRepo.findById(accountId).orElseThrow();
        if(account.getStatus().contains("Active")) {
//            Transaction transaction = new Transaction();
            if (transaction.getDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
                // populate transaction fields
//                transaction.setDepositAmount(amount);
                transactionService.insertRecord(account.getUserId(), transaction,accountId);
//                account.setBalance(account.getBalance().add(amount));
//                accountRepo.save(account);
                return ResponseEntity.status(HttpStatus.OK).body("");
            } else {
                throw new InvalidTransactionException("Deposit Amount Greater than 0");
            }
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account is Locked");
        }
    }

    @Override
    public void withdraw(Long accountId, BigDecimal amount, String narration) throws InvalidTransactionException {
        Account account = accountRepo.findById(accountId).orElseThrow();
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
        BigDecimal availableBalance=account.getBalance();
        Transaction transaction = new Transaction();
        // populate transaction fields
        if(availableBalance.compareTo(amount)>=0 && amount.compareTo(BigDecimal.ZERO)>0) {
            transaction.setDepositAmount(amount.negate()); // negate the amount for withdrawal
            if(transaction.getNarration().isEmpty() || transaction.getNarration().isBlank()) {
                transaction.setNarration("Withdrawal");
            }
            if(transaction.getTransactionMode().isBlank() || transaction.getTransactionMode().isEmpty()) {
                transaction.setTransactionMode("Online");
            }
            transaction.setSourceAccount(account.getAccountNumber());
            transactionService.insertRecord(accountId,transaction,accountId);
            account.setBalance(account.getBalance().subtract(amount));
            accountRepo.save(account);
        } else if (amount.compareTo(BigDecimal.ZERO)<=0) {
            throw new InvalidTransactionException("Amount Must be positive");
        }else if(!amount.equals(availableBalance)){
            throw new InvalidTransactionException("Amount must be less than or equal to Available Balance");
        }else{
            throw new InvalidTransactionException("Withdraw Not Allowed");
        }
    }

    // Updates an existing account with new details
    @Override
    public Account updateAccount(Long id, Account accountDetails) {
        // Fetches the existing account data by ID
        Account account = getAccountDataById(id);

        if (account != null) {
            // Updates the fields of the existing account with new details
            account.setAccountNumber(accountDetails.getAccountNumber());
            account.setBalance(accountDetails.getBalance());
            account.setAccountType(accountDetails.getAccountType());
            account.setCreatedAt(accountDetails.getCreatedAt()); // Note: This might be unnecessary if you don't want to update the creation date

            // Saves the updated account and returns it
            return accountRepo.save(account);
        }

        // If account is not found, return null (could be improved to throw an exception)
        return null;
    }

    // Retrieves all account records from the repository
    @Override
    public List<Account> getAllRecords() {
        return accountRepo.findAll();
    }// Return the list of all accounts
}