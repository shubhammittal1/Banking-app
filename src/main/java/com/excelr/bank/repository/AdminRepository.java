package com.excelr.bank.repository;

import com.excelr.bank.payload.request.AdminSignupRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminSignupRequest,Long> {

    // Finds a Admin entity by its username, returning an Optional to handle cases where the username might not exist
    Optional<AdminSignupRequest> findByName(String adminName);
    // Checks if a User entity exists with the given username
    Boolean existsByName(String username);

    Optional<AdminSignupRequest> findByAdminId(String adminId);

    // Checks if a User entity exists with the given email address
    Boolean existsByEmail(String email);

}