package com.excelr.bank.payload.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignupRequest {
  // Specifies the primary key of an entity
  @Id
  // Indicates that the primary key is auto-generated
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @Column(unique = true)
  private Long customerId ;
  @NotBlank
  @Size(min = 3, max = 20)
  private String name;

  // Ensures that the field is not null and not blank
  @NotBlank
  // Specifies that the field can be up to 50 characters long
  @Size(max = 50)
  // Validates that the field contains a valid email address format
  @Email
  private String email;

  // Set of roles assigned to the user
  private Set<String> role;

  // Timestamp for when the user was created
  private LocalDateTime createdAt;

  @NotBlank
  // Ensures password contains at least one lowercase letter, one uppercase letter,
  // one digit, one special character, and is at least 8 characters long
  @Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$")
  private String password;
  // Validates phone numbers with optional country code, optional parentheses, and allows spaces, dots, or hyphens as separators
  @Pattern(regexp="^(\\+\\d{1,2}\\s?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$")
  @Size(min=10 ,max = 13)
  private String contactNo;
  // Ensures address does not contain more than 5 consecutive spaces and includes only alphanumeric characters,
  // spaces, commas, periods, single quotes, and hyphens
  @Pattern(regexp="^(?!.*\\s{6,})[A-Za-z0-9\\s.,'-]*$")
  private String address;

  @NotNull
  private String gender;

  @Pattern(regexp = "^[0-9]{4}[ -]?[0-9]{4}[ -]?[0-9]{4}$")
  private String aadharNo;
  @Pattern(regexp="[A-Z]{5}[0-9]{4}[A-Z]{1}")
  private String pancard;

  private String dateOfBirth;

  @Min(000000)
  @Max(999999)
  private int pincode;

}
