package com.excelr.bank.security.jwt;

import com.excelr.bank.security.services.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Extend OncePerRequestFilter to ensure the filter is applied only once per request
public class AuthTokenFilter extends OncePerRequestFilter {

  // Autowire JwtUtils to handle JWT operations
  @Autowired
  private JwtUtils jwtUtils;

  // Autowire UserDetailsServiceImpl to load user details
  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  // Define a logger to log messages for debugging and monitoring
  private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

  // Override the doFilterInternal method to implement custom filter logic
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {

    try {
      // Extract JWT from the request header
      String jwt = parseJwt(request);

      // Validate the JWT and if valid, set the authentication in the security context
      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
        // Extract the username from the JWT
        String username = jwtUtils.getUserNameFromJwtToken(jwt);

        // Load user details based on the username
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Create an authentication token with user details and authorities
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

        // Set the details of the authentication token
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set the authentication token in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      // Log any exception that occurs during authentication
      logger.error("Cannot set user authentication: {}", e);
    }

    // Continue with the next filter in the chain
    filterChain.doFilter(request, response);
  }

  // Extract the JWT from the Authorization header
  private String parseJwt(HttpServletRequest request) {
    // Get the Authorization header from the request
    String headerAuth = request.getHeader("Authorization");

    // Check if the header is present and starts with "Bearer "
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      // Extract the JWT token from the header
      return headerAuth.substring(7);
    }

    // Return null if no JWT token is found
    return null;
  }
}