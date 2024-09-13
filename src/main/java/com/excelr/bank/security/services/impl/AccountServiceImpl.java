package com.excelr.bank.security.services.impl;

import com.excelr.bank.exception.AccountDetailsException;
import com.excelr.bank.exception.InsufficientBalanceException;
import com.excelr.bank.models.Account;
import com.excelr.bank.models.Transaction;
import com.excelr.bank.repository.AccountRepository;
import com.excelr.bank.repository.UserRepository;
import com.excelr.bank.security.services.interfaces.AccountService;
import com.excelr.bank.util.Generator;
import jakarta.transaction.InvalidTransactionException;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Account createAccount(Account account) throws  IllegalArgumentException {
        if(accountRepo.count()==0){
            Generator generate=new Generator();
            account.setAccountId(generate.generateID());
            account.setAccountNumber(generate.generateAcc());
            account.setStatus("Active");
            return accountRepo.save(account);
        }else {
            Account account1 = accountRepo.findById(account.getAccountId())
                    .orElseThrow(() -> new AccountDetailsException("Account Id not Found with account id: " + account.getAccountId()));
            if (account1 == null) {
                throw new RuntimeException("Account Details cannot be null");
            } else {
                Generator generate=new Generator();
                account.setAccountId(generate.generateID());
                account.setAccountNumber(generate.generateAcc());
                account.setStatus("Active");
                return accountRepo.save(account);
            }// Save and return the newly created account
        }
    }

    // Retrieves account data by its ID
    @Override
    public Account getAccountDataById(Long id) {
        // Finds the account by ID and throws an exception if not found
        Optional<Account> optAccount = accountRepo.findById(id);
        return optAccount.orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
    }

    @Override
    public void deposit(Long accountId, BigDecimal amount, String narration) throws InvalidTransactionException {
        Account account = accountRepo.findById(accountId).orElseThrow();
        Transaction transaction = new Transaction();
        if(amount.compareTo(BigDecimal.ZERO)>0) {
            // populate transaction fields
            transaction.setDepositAmount(amount);
            transactionService.insertRecord(transaction);
            account.setBalance(account.getBalance().add(amount));
            accountRepo.save(account);
        }else{
            throw new InvalidTransactionException("Deposit Amount Greater than 0");
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
            transactionService.insertRecord(transaction);
            account.setBalance(account.getBalance().subtract(amount));
            accountRepo.save(account);
        } else if (amount.compareTo(BigDecimal.ZERO)<=0) {
            throw new InvalidTransactionException("Amount Must be positive");
        }else if(!amount.equals(availableBalance)){
            throw new InvalidTransactionException("Amount must be less than Available Balance");
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
        return accountRepo.findAll(); // Return the list of all accounts
    }
}