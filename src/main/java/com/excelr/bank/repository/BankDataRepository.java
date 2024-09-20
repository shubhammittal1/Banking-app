package com.excelr.bank.repository;

import com.excelr.bank.models.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankDataRepository extends JpaRepository<Bank,Long> {
   List<Bank> findByBankName(String bankName);
}
