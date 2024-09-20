package com.excelr.bank.security.services.impl;

import com.excelr.bank.exception.InsufficientBalanceException;
import com.excelr.bank.exception.UserNotFoundException;
import com.excelr.bank.models.Account;
import com.excelr.bank.models.Transaction;
import com.excelr.bank.models.User;
import com.excelr.bank.payload.request.ElectricityBillRequest;
import com.excelr.bank.payload.request.MobileRechargeRequest;
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
            account.setAccountId(generate.generateAccId());
            account.setAccountNumber(generate.generateAcc());
            account.setStatus("Active");
            account.setUserId(user.get().getUserId());
            if (account.getBalance() == null) {
                account.setBalance(BigDecimal.valueOf(0));
            }
            System.out.println("Account Data"+account);
            accountRepo.save(account);
            return ResponseEntity.status(HttpStatus.OK).body("Account Generated Successfully!!! Your Account Number: "+account.getAccountNumber());
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
    public ResponseEntity<?> deposit(String accountNumber,Transaction request) throws InvalidTransactionException {

        if(request.getNarration().isBlank()||request.getNarration().isEmpty()){
            request.setNarration("Deposit");
        }
        Account account = accountRepo.findByAccountNumber(accountNumber).orElseThrow(()->new RuntimeException("Account not found"));
        if(account.getStatus().contains("Active")) {
           request.setRecipientAccount(accountNumber);
            if (request.getDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("Transaction object"+request);
                Transaction transaction=transactionService.insertDepositRecord(account.getUserId(), request,account.getAccountId());
                return ResponseEntity.status(HttpStatus.OK).body("Amount: "+request.getDepositAmount()+" Deposit Successfully in "+transaction.getRecipientAccount().replaceFirst("(^\\d{7})" ,"XXXXXXX"));
            } else {
                throw new InvalidTransactionException("Deposit Amount Greater than 0");
            }
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account is Locked");
        }
    }

    @Override
    public ResponseEntity<?> withdraw(String accountNumber, Transaction request) throws InvalidTransactionException,InsufficientBalanceException {
        Account account = accountRepo.findByAccountNumber(accountNumber).orElseThrow(()->new RuntimeException("Account Not Found"));
        if(account.getStatus().equals("Active")) {
            if (account.getBalance().compareTo(request.getWithdrawalAmount()) < 0) {
                throw new InsufficientBalanceException("Entered Amount is Greater than Available Balance");
            }
            BigDecimal availableBalance = account.getBalance();
            if (availableBalance.compareTo(request.getWithdrawalAmount()) >= 0 && request.getWithdrawalAmount().compareTo(BigDecimal.ZERO) > 0) {
                request.setTransactionMode("Online");
                request.setSourceAccount(accountNumber);
                request.setDepositAmount(BigDecimal.ZERO);
                transactionService.insertWithdrawRecord(account.getUserId(), request, account);
                account.setBalance(account.getBalance().subtract(request.getWithdrawalAmount()));
                accountRepo.save(account);
                return ResponseEntity.status(HttpStatus.OK).body("Withdraw Success from "+accountNumber.replaceFirst("(^\\d{7})" ,"XXXXXXX"));

            } else if (request.getWithdrawalAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidTransactionException("Amount Must be positive");
            } else if (!request.getWithdrawalAmount().equals(availableBalance)) {
                throw new InvalidTransactionException("Amount must be less than or equal to Available Balance");
            } else {
                throw new InvalidTransactionException("Withdraw Not Allowed");
            }
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Account Lock");
        }
    }

    @Override
    public ResponseEntity<?> recharge(String accNumber, MobileRechargeRequest request, Transaction transaction) throws InvalidTransactionException {
        Account account = accountRepo.findByAccountNumber(accNumber).orElseThrow(()->new RuntimeException("Account not found with userId"));
        if(account.getStatus().equals("Active")) {
            if (account.getBalance().compareTo(request.getRechargeAmount()) < 0) {
                throw new InsufficientBalanceException("Recharge Amount: "+request.getRechargeAmount()+" is Greater than Available Balance");
            }
            BigDecimal availableBalance = account.getBalance();
            if (availableBalance.compareTo(request.getRechargeAmount()) >= 0 && request.getRechargeAmount().compareTo(BigDecimal.ZERO) > 0) {
                transaction.setTransactionMode("Online");
                transaction.setNarration(request.getVendorName()+" Recharge to "+request.getMobileNumber()+" is Success");
                transaction.setSourceAccount(account.getAccountNumber());
                transaction.setDepositAmount(BigDecimal.ZERO);
                account.setBalance(account.getBalance().subtract(request.getRechargeAmount()));
                transactionService.insertWithdrawRecord(account.getUserId(), transaction, account);
                accountRepo.save(account);
                return ResponseEntity.status(HttpStatus.OK).body("Recharge of Amount "+request.getRechargeAmount()+" Success to:"+request.getMobileNumber()+ " and Amount Deducted from "+accNumber.replaceFirst("(^\\d{7})" ,"XXXXXXX"));
            } else if (request.getRechargeAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidTransactionException("Amount Must be positive");
            } else if (!request.getRechargeAmount().equals(availableBalance)) {
                throw new InvalidTransactionException("Amount must be less than or equal to Available Balance");
            } else {
                throw new InvalidTransactionException("Recharge Not Allowed");
            }
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Account Lock");
        }
    }

    @Override
    public ResponseEntity<?> electricityBill( ElectricityBillRequest request, Transaction transaction) throws InvalidTransactionException {
        Account account = accountRepo.findByAccountNumber(request.getAccNum()).orElseThrow(()->new RuntimeException("Account not found with userId"));
        if(account.getStatus().equals("Active")) {
            if (account.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientBalanceException("Bill Amount: "+request.getAmount()+" is Greater than Available Account Balance");
            }
            BigDecimal availableBalance = account.getBalance();
            if (availableBalance.compareTo(request.getAmount()) >= 0 && request.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                transaction.setTransactionMode("Online");
                transaction.setNarration(request.getProvidor()+" Bill payment to "+request.getCustomerId()+" is Success");
                transaction.setSourceAccount(account.getAccountNumber());
                transaction.setDepositAmount(BigDecimal.ZERO);
                account.setBalance(account.getBalance().subtract(request.getAmount()));
                transactionService.insertWithdrawRecord(account.getUserId(), transaction, account);
                accountRepo.save(account);
                return ResponseEntity.status(HttpStatus.OK).body("Bill Amount of "+request.getAmount()+" Success to:"+request.getCustomerId()+ " and Amount Deducted from "+request.getAccNum().replaceFirst("(^\\d{7})" ,"XXXXXXX"));
            } else if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidTransactionException("Amount Must be positive");
            } else if (!request.getAmount().equals(availableBalance)) {
                throw new InvalidTransactionException("Amount must be less than or equal to Available Balance");
            } else {
                throw new InvalidTransactionException("Recharge Not Allowed");
            }
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Account Lock");
        }
    }

    public ResponseEntity<?> transfer(Transaction request,BigDecimal amount) throws InsufficientBalanceException, InvalidTransactionException ,UserNotFoundException {
        Account srcAccount=accountRepo.findByAccountNumber(request.getSourceAccount()).orElseThrow(()->new RuntimeException("Source Account Not Found"));
        Account destAccount=accountRepo.findByAccountNumber(request.getRecipientAccount()).orElseThrow(()->new RuntimeException("Destination Account Not Found"));
        if(srcAccount.getBalance().compareTo(amount) < 0 ){
            throw new InsufficientBalanceException();
        }else{
            if(request.getSourceAccount().equals(request.getRecipientAccount())) {
                throw new InvalidTransactionException("Source and Destination Account Same");
            }else {
                srcAccount.setBalance(srcAccount.getBalance().subtract(amount));
                destAccount.setBalance(destAccount.getBalance().add(amount));
                accountRepo.save(srcAccount);
                accountRepo.save(destAccount);
                if(null==request.getNarration()){
                    request.setNarration("Money Transfer");

                }
                Transaction transaction=transactionService.insertTransferRecord(srcAccount.getUserId(), request,srcAccount);
                transaction.setUserId(null);
                transaction.setRecipientAccount("XXXX");
                transaction.setSourceAccount("XXXX");
                return ResponseEntity.status(HttpStatus.OK).body("Transfer Successful to " +transaction.getRecipientName()+
                                                                "in Account Number : "+ transaction.getRecipientAccount().substring(0,6).replace("","X"));
            }
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