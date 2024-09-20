package com.excelr.bank.security.jwt;

import com.excelr.bank.security.services.impl.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
// Mark this class as a Spring component, allowing it to be detected and managed by the Spring container
@Component
public class JwtUtils {

  // Define a logger for logging messages
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  // Inject the JWT secret key from application properties
  @Value("${bank.app.jwtSecret}")
  private String jwtSecret;

  // Inject the JWT expiration time in milliseconds from application properties
  @Value("${bank.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  // Generate a JWT token for the authenticated user
  public String generateJwtToken(Authentication authentication) {

    // Retrieve the user details from the authentication object
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    // Build and return the JWT token
    return Jwts.builder()
            .setSubject(userPrincipal.getUsername()) // Set the subject (username) of the token
            .setIssuedAt(new Date()) // Set the issue date of the token
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Set the expiration date
            .signWith(key(), SignatureAlgorithm.HS256) // Sign the token with the secret key and HS256 algorithm
            .compact(); // Convert the token to a compact, URL-safe string
  }

  // Generate the signing key from the JWT secret
  private Key key() {
    // Decode the base64-encoded secret and generate a key
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  // Extract the username from the JWT token
  public String getUserNameFromJwtToken(String token) {
    // Parse the token and extract the username (subject) from the claims
    return Jwts.parserBuilder().setSigningKey(key()).build()
            .parseClaimsJws(token).getBody().getSubject();
  }

  // Validate the JWT token
  public boolean validateJwtToken(String authToken) {
    try {
      // Parse the token to check its validity
      Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
      return true; // Token is valid
    } catch (MalformedJwtException e) {

      // Log if the token is malformed
      logger.error("Invalid JWT token: {}", e.getMessage());

    } catch (ExpiredJwtException e) {

      // Log if the token is expired
      logger.error("JWT token is expired: {}", e.getMessage());

    } catch (UnsupportedJwtException e) {
      // Log if the token type is unsupported
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      // Log if the token claims string is empty
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }
    // If any exception occurs, the token is not valid
    return false;
  }

}