package com.excelr.bank.payload.response;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JwtResponse {
  // The JWT token string
  private String token;

  // The type of the token (typically "Bearer")
  private String type = "Bearer";

  // User's unique identifier
  private Long id;

  // User's username
  private String username;

  // User's email address
  private String email;

  private LocalDateTime createdAt;

  // List of roles assigned to the user
  private List<String> roles;

  public JwtResponse(String jwt, Long id, String username, String email, List<String> roles) {
    this.token=jwt;
    this.id=id;
    this.username=username;
    this.email=email;
    this.roles=roles;
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

  private static final DateTimeFormatter FORMATTER= DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
  // Formats `createdAt` using the defined pattern
  public String getFormattedCreatedAt() {
    return this.createdAt != null ? this.createdAt.format(FORMATTER) : "";
  }
}
