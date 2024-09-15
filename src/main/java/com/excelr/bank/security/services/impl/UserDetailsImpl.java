package com.excelr.bank.security.services.impl;

import com.excelr.bank.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// Implements UserDetails to provide user details for authentication and authorization

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDetailsImpl implements UserDetails {

  // Serial version UID for serialization
  @Serial
  private static final long serialVersionUID = 1L;

  // User ID
  private Long id;

  // Username
  private String username;

  // Email address
  private String email;

  // Password, marked with @JsonIgnore to prevent it from being serialized
  @JsonIgnore
  private String password;

  // Account creation date
  private LocalDateTime createdAt;

  // Authorities (roles/permissions) granted to the user
  private Collection<? extends GrantedAuthority> authorities;

  public UserDetailsImpl(Long id, String username, String email, String password, Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
    this.authorities = authorities;
  }

  // Builds a UserDetailsImpl instance from a User object
  public static UserDetailsImpl build(User user) throws IllegalArgumentException{
    if (user!=null) {
      // Converts user roles into a collection of GrantedAuthority
      List<GrantedAuthority> authorities = user.getRoles().stream()
              .map(role -> new SimpleGrantedAuthority(role.getName().name()))
              .collect(Collectors.toList());

      // Creates and returns a new UserDetailsImpl instance
      return new UserDetailsImpl(
              user.getUserId(),
              user.getUsername(),
              user.getEmail(),
              user.getPassword(),
              authorities);
    }else{
      throw new RuntimeException("user Details are not null");
    }
  }

  // Returns the authorities (roles/permissions) granted to the user
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  // Indicates if the account is non-expired (always true in this implementation)
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  // Indicates if the account is non-locked (always true in this implementation)
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  // Indicates if the credentials are non-expired (always true in this implementation)
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  // Indicates if the account is enabled (always true in this implementation)
  @Override
  public boolean isEnabled() {
    return true;
  }

  @PrePersist
  // @PreUpdate method to set the date and time before updating the entity
  @PreUpdate
  public void preUpdate() {
    this.createdAt = LocalDateTime.now();
  }

  // Checks equality based on user ID
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }
}