package com.excelr.bank.models;

import com.excelr.bank.util.Generator;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor  // Generates a constructor with parameters for all fields
@NoArgsConstructor   // Generates a no-argument constructor
@Data  // Generates getters, setters, equals, hashCode, and toString methods
@Entity  // Marks this class as a JPA entity
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

    @DecimalMin(value="0", message="Deposit should be greater than 0")
    // Amount deposited in the transaction, must be greater than 0
    private BigDecimal depositAmount;

    @DecimalMin(value="0", message="Withdrawal should be greater than 0")
    // Amount withdrawn in the transaction, must be greater than 0
    private BigDecimal withdrawalAmount;

    private String transactionType;

    private String sourceBank;

    private String sourceAccount;

    private String recipientName;

    private String receipientBank;

    private String recipientAccount;

    private String customerName;

    private String narration;

    private BigDecimal amount;

    @JoinTable(name = "users_data",
            joinColumns = @JoinColumn(name = "user_id"))
    private Long userId;

    @DecimalMin(value="0.00", message="Balance should be greater than 0")
    // Balance after the transaction, must be greater than 0
    private BigDecimal balance;

//    @NotNull(message="Transaction mode cannot be null")
    // Mode of the transaction (e.g., cash, card)
    private String transactionMode;

    @NotNull(message="Transaction date and time cannot be null")
    @Column(name = "transactionDateAndTime")
    // Date and time when the transaction occurred
    private LocalDateTime transactionDateAndTime;

    private LocalDateTime date;

    private LocalDateTime startDate;

    private LocalDateTime endDate;



//    private static final DateTimeFormatter FORMATTER= DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");

    // @PrePersist method to set the date and time before persisting the entity
    @PrePersist
    public void prePersist() {

        this.transactionDateAndTime = LocalDateTime.now();
        this.transactionId=new Generator().generateTransactionId();    }

    // @PreUpdate method to set the date and time before updating the entity
    @PreUpdate
    public void preUpdate() {

        this.transactionDateAndTime = LocalDateTime.now();
        this.transactionId=new Generator().generateTransactionId();

    }

}