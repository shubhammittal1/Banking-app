package com.excelr.bank.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="managers_details")
public class AdminBankDetails {
    @Id
    private Long id;
    private String bankName;
    @Pattern(regexp="^[A-Z]{4}0[A-Z0-9]{6}$")
    private String ifscCode;
    private String Website;
    @Pattern(regexp = "^(?!.*\\s{8,})[A-Za-z0-9\\s.,'-]*$")
    // Address of the user, must not contain more than 5 consecutive spaces
    private String address;
    private String bankAdd;
    @Size(max = 35)
    @Email
    // Email of the user, must be a valid email address, not null, not blank, with a maximum length of 35 characters
    private String bankEmail;
    //Validates phone numbers with optional country code, optional parentheses, and allows spaces, dots, or hyphens as separators.
    @Pattern(regexp = "^(\\+\\d{1,2}\\s?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$")
    @Size(min = 10, max = 13)
    private String phoneNo;
    private String country;
    @Size(min = 2,max=10)
    private String currency;
    @Column(unique = true)
    private String accountNumber;

}
