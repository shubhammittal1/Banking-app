package com.excelr.bank.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Mark this class as a Spring component, allowing it to be detected and managed by the Spring container
@Component
// Implement the AuthenticationEntryPoint interface to handle authentication errors
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

  // Define a logger to log error messages
  private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

  // Override the commence method to handle authentication exceptions
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
          throws IOException, ServletException {

      // Log the unauthorized access attempt with the exception message
      logger.error("Unauthorized error: {}", authException.getMessage());

      // Set the response content type to JSON
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      // Set the HTTP response status to 401 Unauthorized
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

      // Create a map to hold the response body data
      final Map<String, Object> body = new HashMap<>();
      body.put("status", HttpServletResponse.SC_UNAUTHORIZED); // Status code
      body.put("error", "Unauthorized"); // Error message
      body.put("message", authException.getMessage()); // Detailed message from the exception
      body.put("path", request.getServletPath()); // Path of the request that caused the error

      // Create an ObjectMapper to convert the map to JSON
      final ObjectMapper mapper = new ObjectMapper();
      // Write the JSON response to the output stream
      mapper.writeValue(response.getOutputStream(), body);
  }
}