package com.excelr.bank.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="transaction_Records")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Primary key for the transaction entity
    private Long id;

    @NotNull(message = "Transaction ID cannot be null")
    @Size(min = 7, max = 40, message = "Transaction ID must be between 7 to 40 characters")
    // Unique identifier for the transaction, must be between 7 to 40 characters
    private String transactionId;

    @DecimalMin(value="0.01", message="Deposit should be greater than 0")
    // Amount deposited in the transaction, must be greater than 0
    private BigDecimal depositAmount;

    @DecimalMin(value="0.01", message="Withdrawal should be greater than 0")
    // Amount withdrawn in the transaction, must be greater than 0
    private BigDecimal withdrawalAmount;

    private String transactionType;

    private String sourceBank;

    @NotNull
    private String sourceAccount;

    private String recipient;

    private String receipientBank;

    @NotNull
    private String recipientAccount;

    private String customerName;

    private String narration;

    private BigDecimal amount;

    @DecimalMin(value="0.01", message="Balance should be greater than 0")
    // Balance after the transaction, must be greater than 0
    private BigDecimal balance;

    @NotNull(message="Transaction mode cannot be null")
    // Mode of the transaction (e.g., cash, card)
    private String transactionMode;

    @NotNull(message="Transaction date and time cannot be null")
    @Column(name = "transactionDateAndTime")
    // Date and time when the transaction occurred
    private LocalDateTime transactionDateAndTime;

    private LocalDateTime date;

    private LocalDate startDate;

    private LocalDate endDate;

//    private static final DateTimeFormatter FORMATTER= DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");

    // @PrePersist method to set the date and time before persisting the entity
    @PrePersist
    public void prePersist() {
        this.transactionDateAndTime = LocalDateTime.now();
        this.transactionId= new SecureRandom().ints(18, 0, 36).mapToObj(i -> Integer.toString(i, 36)).collect(Collectors.joining());
    }

    // @PreUpdate method to set the date and time before updating the entity
    @PreUpdate
    public void preUpdate() {
        this.transactionDateAndTime = LocalDateTime.now();
        this.transactionId= new SecureRandom().ints(18, 0, 36).mapToObj(i -> Integer.toString(i, 36)).collect(Collectors.joining());
    }

    // Sets the `transactionId` of 18 Digits
    public void setTransactionId(@NotNull(message = "Transaction ID cannot be null") @Size(min = 7, max = 40, message = "Transaction ID must be between 7 to 40 characters") String transactionId) {
        this.transactionId = "TX" + transactionId;
    }
}