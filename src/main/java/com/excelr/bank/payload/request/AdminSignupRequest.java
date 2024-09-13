package com.excelr.bank.payload.request;

import com.excelr.bank.models.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class AdminSignupRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Size(max = 35)
    @Email
    private String email;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$", message = "Password must be Combination of Capital letter, special Character,small letter and number must be 8 and above characters long")
    // Password of the user, must meet complexity requirements and be at least 8 characters long
    private String password;
    private String gender;
    @Pattern(regexp = "^(\\+\\d{1,2}\\s?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$")
    @Size(min = 10, max = 13)
    // Phone number of the Bank Managers, must match the pattern for phone numbers and be between 10 and 13 characters long
    private String contactNo;

    @Min(1)
    @Max(130)
    private int age;
    private String street;
    private String city;
    @Min(000000)
    @Max(999999)
    private int pincode;

    private String adminId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "admin_roles",
            joinColumns = @JoinColumn(name = "admin_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    // Many-to-many relationship with roles, fetched lazily
    private Set<Role> roles = new HashSet<>();
    @ElementCollection
    private Set<String> roles1=new HashSet<>();

    private String status;

    private String token;

    private LocalDateTime tokenCreatedAt;

    private LocalDateTime tokenExpiredAt;

    @Column(name = "createdAt")
    // Date and time when the user was created
    private LocalDateTime createdAt;

    public AdminSignupRequest(String name, @Size(max = 35) @Email String email, String encode,@Min(1)@Max(130) int age, String gender, String city, String street, @Pattern(regexp = "^(\\+\\d{1,2}\\s?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$") @Size(min = 10, max = 13) String contactNo,@Min(000000)@Max(999999) int pincode) {
        this.name=name;
        this.email=email;
        this.password=encode;
        this.age=age;
        this.gender=gender;
        this.city=city;
        this.street=street;
        this.contactNo=contactNo;
        this.pincode=pincode;
    }

    @PrePersist
    public void prePersist()     {
        this.createdAt = LocalDateTime.now();
    }
    // @PreUpdate method to set the date and time before updating the entity
    @PreUpdate
    public void preUpdate() {
        this.createdAt = LocalDateTime.now();
    }
}
