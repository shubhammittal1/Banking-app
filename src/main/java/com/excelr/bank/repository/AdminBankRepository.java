package com.excelr.bank.repository;

import com.excelr.bank.models.AdminBankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminBankRepository extends JpaRepository<AdminBankDetails,Long> {
}
