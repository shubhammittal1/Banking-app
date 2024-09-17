
package com.excelr.bank.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users_data",
    uniqueConstraints = {
      @UniqueConstraint(columnNames = "username"),
      @UniqueConstraint(columnNames = "email")
    })
public class User {

  // Primary key for the user entity
  @Id
  @Column(unique = true,name = "user_id")
  private Long userId;

  @NotBlank
  @NotNull
  @Size(max = 20)
  @Column(unique = true)
  // Username of the user, must not be blank and not null, with a maximum length of 20 characters
  private String username;

  @NotNull
  @NotBlank
  @Size(max = 50)
  @Email
  // Email of the user, must be a valid email address, not null, not blank, with a maximum length of 50 characters
  private String email;

  //Ensures password contains at least one lowercase letter, one uppercase letter, one digit,
  // one special character, and is at least 8 characters long.
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$", message = "Password must be Combination of Capital letter, special Character,small letter and number must be 8 and above characters long")
  // Password of the user, must meet complexity requirements and be at least 8 characters long
  private String password;
  //Validates phone numbers with optional country code, optional parentheses, and allows spaces, dots, or hyphens as separators.
  @Pattern(regexp = "^(\\+\\d{1,2}\\s?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$")
  @Size(min = 10, max = 13)
  // Phone number of the user, must match the pattern for phone numbers and be between 10 and 13 characters long
  private String phoneNo;

  @Min(100000)
  @Max(999999)
  private int pincode;

  //Ensures address does not contain more than 5 consecutive spaces and includes only alphanumeric characters,
  // spaces, commas, periods, single quotes, and hyphens.
  @Pattern(regexp = "^(?!.*\\s{6,})[A-Za-z0-9\\s.,'-]*$")
  // Address of the user, must not contain more than 5 consecutive spaces
  private String address;

  @Pattern(regexp = "^(?:0[1-9]|[12]\\d|3[01])([\\/.-])(?:0[1-9]|1[012])\\1(?:19|20)\\d\\d$")
  // Date of birth of the user, must match the pattern for dates in dd/MM/yyyy, dd-MM-yyyy, or dd.MM.yyyy format
  private String dateOfBirth;

  @NotNull
  private String gender;

  private String roleType;

  @Pattern(regexp = "^[0-9]{4}[ -]?[0-9]{4}[ -]?[0-9]{4}$")
  private String aadharNo;
  @Pattern(regexp="[A-Z]{5}[0-9]{4}[A-Z]{1}")
  private String pancard;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_roles",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "role_id"))
  // Many-to-many relationship with roles, fetched lazily
  private Set<Role> roles = new HashSet<>();

//  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//  private List<Account> accounts;

  @JsonIgnore
  @Column(name = "createdAt")
  // Date and time when the user was created
  private LocalDateTime createdAt;

  @JsonIgnore
  private String token;

  @JsonIgnore
  @Column(name = "tokenCreatedAt")
  // Date and time when the token was created
  private LocalDateTime tokenCreatedAt;

  private LocalDateTime tokenExpAt;

  private String status;

  private static final DateTimeFormatter FORMATTER= DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");

  public User(@NotBlank @Size(min = 3, max = 20) String username, String encode, @NotBlank @Size(max = 50) @Email String email,@NotNull String gender, @Pattern(regexp="^(?:0[1-9]|[12]\\d|3[01])([\\/.-])(?:0[1-9]|1[012])\\1(?:19|20)\\d\\d$") String dateOfBirth,@Pattern(regexp="^(?!.*\\s{6,})[A-Za-z0-9\\s.,'-]*$") String address, @Pattern(regexp = "^[0-9]{4}[ -]?[0-9]{4}[ -]?[0-9]{4}$") String aadharNo, @Pattern(regexp="[A-Z]{5}[0-9]{4}[A-Z]{1}") String pancard, @Pattern(regexp="^(\\+\\d{1,2}\\s?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$") @Size(min=10 ,max = 13) String phoneNo,@Min(100000)
  @Max(999999)int pincode) {
    this.username=username;
    this.password=encode;
    this.email=email;
    this.gender=gender;
    this.dateOfBirth=dateOfBirth;
    this.address=address;
    this.aadharNo=aadharNo;
    this.pancard=pancard;
    this.phoneNo=phoneNo;
    this.pincode=pincode;
  }

  public User(String username, String email, String encode, String dateOfBirth, String gender, String address, String phoneNo, int pincode) {
    this.username=username;
    this.password=encode;
    this.email=email;
    this.gender=gender;
    this.dateOfBirth=dateOfBirth;
    this.address=address;
    this.phoneNo=phoneNo;
    this.pincode=pincode;
  }


  // @PrePersist method to set the date and time before persisting the entity
  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }

  // @PreUpdate method to set the date and time before updating the entity
  @PreUpdate
  public void preUpdate() {
    this.createdAt = LocalDateTime.now();
  }

}
