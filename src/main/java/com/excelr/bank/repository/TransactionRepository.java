package com.excelr.bank.repository;

import com.excelr.bank.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    List<Transaction> findTransactionsByTransactionIdAndDateBetween(String transactionId, LocalDate startDate, LocalDate endDate);
}
