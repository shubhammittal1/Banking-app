package com.excelr.bank.security.services.impl;

import com.excelr.bank.models.Account;
import com.excelr.bank.models.Transaction;
import com.excelr.bank.models.User;
import com.excelr.bank.repository.AccountRepository;
import com.excelr.bank.repository.TransactionRepository;
import com.excelr.bank.repository.UserRepository;
import com.excelr.bank.security.services.interfaces.TransactionService;
import com.excelr.bank.util.FormatterUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.InvalidTransactionException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Transaction insertDepositRecord(Long customerId, Transaction transaction,Long accountId) throws InvalidTransactionException {
            Account account= accountRepo.findById(accountId).orElseThrow();
            User user= userRepository.getReferenceById(customerId);
            System.out.println("User Details"+user);
            if (StringUtils.isNotEmpty(transaction.getRecipientAccount())){
                Account acc1=accountRepo.findByAccountNumber(transaction.getRecipientAccount()).orElseThrow(()->new RuntimeException("Account Number Not Found"));
                transaction.setRecipientName(acc1.getAccountHolderName());
            }
            transaction.setCustomerName(account.getAccountHolderName());
            BigDecimal AmountDeposit=account.getBalance().add(transaction.getDepositAmount());
            transaction.setUserId(user.getUserId());
            transaction.setSourceAccount(account.getAccountNumber());
            transaction.setCustomerName(user.getUsername());
            transaction.setTransactionMode("offline");
            account.setBalance(AmountDeposit);
            accountRepo.save(account);
            if (transaction.getDepositAmount() == null ) {
                throw new InvalidTransactionException("null is not allowed");
            }
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction insertTransferRecord(Long customerId, Transaction transaction,Account account) throws InvalidTransactionException {
        User user= userRepository.getReferenceById(customerId);

        if(null==transaction.getTransactionType()){
            throw new InvalidTransactionException("Transaction Mode is not Described");
        }
        transaction.setCustomerName(account.getAccountHolderName());
        transaction.setUserId(user.getUserId());
        transaction.setSourceAccount(account.getAccountNumber());
        transaction.setCustomerName(user.getUsername());
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction insertWithdrawRecord(Long customerId, Transaction transaction,Account account) throws InvalidTransactionException {
        if (transaction.getNarration().isEmpty() || transaction.getNarration().isBlank()) {
            transaction.setNarration("Withdrawal");
        }
        transaction.setUserId(account.getUserId());
        transaction.setSourceAccount(account.getAccountNumber());
        transaction.setRecipientAccount("NA");
        transaction.setCustomerName(account.getAccountHolderName());
        transaction.setTransactionMode("offline");
        if (transaction.getDepositAmount() == null ) {
            throw new InvalidTransactionException("null is not allowed");
        }
        return transactionRepository.save(transaction);
    }
    @Override
    public List<Transaction> getStatement(Long userId, String startDate, String endDate) {
        try {
            // Retrieve account transactions from repository
            List<Transaction> transactions = transactionRepository.findTransactionsByUserId(userId);
            // Filter transactions by date range
            transactions = transactions.stream()
                    .filter(transaction -> transaction.getTransactionDateAndTime().isAfter
                            (ChronoLocalDateTime.from(FormatterUtil.formatData(startDate)))
                            && transaction.getTransactionDateAndTime().isBefore(ChronoLocalDateTime.from(
                                    FormatterUtil.formatData(endDate))))
                    .collect(Collectors.toList());

            if (transactions.isEmpty()) {
                throw new ServiceException("No transactions found for the given account ID and date range");
            }

            return transactions;
        } catch (NullPointerException e) {
            throw new ServiceException("Invalid account ID or date range", e);
        }
    }

    @Override
    public void downloadStatement(Long userId, String startDate, String endDate) {
        try {
            List<Transaction> transactions = getStatement(userId, startDate, endDate);

            // Create a CSV file or other format
            File statementFile = new File("statement_" + userId + ".csv");
            try (PrintWriter writer = new PrintWriter(statementFile)) {
                writer.println("Transaction ID,Transaction Type,Amount,Beneficiary Amount,Beneficiary Account,Description,Transaction Time");

//                BigDecimal totalWithdrawalAmount = BigDecimal.ZERO;
//                BigDecimal totalDepositAmount = BigDecimal.ZERO;
                DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd");
                for (Transaction transaction : transactions) {
                    writer.println(
                            transaction.getId() + "," +
                                    transaction.getTransactionType() + "," +
                                    transaction.getAmount() + "," +
                                    transaction.getDepositAmount() + "," +
                                    transaction.getRecipientAccount() + "," +
                                    transaction.getNarration() + "," +
                                    formatter.format(transaction.getTransactionDateAndTime())
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            throw new ServiceException("An unexpected error occurred", e);
        }
    }


    @Override
    public Transaction getTransactionById(Long id) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        return optionalTransaction.orElse(null);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        Transaction transaction = getTransactionById(id);
        if (transaction != null) {
            transaction.setId(id);
            transaction.setTransactionId(transaction.getTransactionId());
            transaction.setBalance(transactionDetails.getBalance());
            transaction.setTransactionMode(transaction.getTransactionMode());
            transaction.setTransactionDateAndTime(transaction.getTransactionDateAndTime());
            transaction.setWithdrawalAmount(transaction.getWithdrawalAmount());
            transaction.setDepositAmount(transaction.getDepositAmount());
            return transactionRepository.save(transaction);
        }
        return null;
    }

    @Override
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
        System.out.println("transaction succesfully deleted");
    }
}
