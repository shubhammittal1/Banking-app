package com.excelr.bank.controllers;

import com.excelr.bank.models.AvailableRoles;
import com.excelr.bank.models.Role;
import com.excelr.bank.models.User;
import com.excelr.bank.payload.request.LoginRequest;
import com.excelr.bank.payload.request.SignupRequest;
import com.excelr.bank.payload.response.JwtResponse;
import com.excelr.bank.payload.response.MessageResponse;
import com.excelr.bank.repository.RoleRepository;
import com.excelr.bank.repository.UserRepository;
import com.excelr.bank.security.jwt.JwtUtils;
import com.excelr.bank.security.services.impl.UserDetailsImpl;
import com.excelr.bank.util.Generator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("username/signin")
    public ResponseEntity<?> authenticateUserByName(@Valid @RequestBody LoginRequest loginRequest) {

        Optional<User> userRepo = userRepository.findById(loginRequest.getUserId());
        User user = userRepo.get();
        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());


        user.setToken(jwt);
        user.setTokenCreatedAt(LocalDateTime.now());
        user.setTokenExpAt(LocalDateTime.now().plusHours(2));
        userRepository.save(user);

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("admin/signin")
    public ResponseEntity<?> authenticateAdminById(@Valid @RequestBody LoginRequest loginRequest) {

        Optional<User> userRepoResult = userRepository.findById(loginRequest.getAdminId());
        User admin = userRepoResult.get();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(admin.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        admin.setToken(jwt);
        admin.setTokenCreatedAt(LocalDateTime.now());
        admin.setTokenExpAt(LocalDateTime.now().plusHours(2));
        userRepository.save(admin);

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));

    }
    @PostMapping("/user/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(
                signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getEmail(),
                signUpRequest.getGender(),
                signUpRequest.getDateOfBirth(),
                signUpRequest.getAddress(),
                signUpRequest.getAadharNo(),
                signUpRequest.getPancard(),
                signUpRequest.getPhoneNo(),
                signUpRequest.getPincode());

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(AvailableRoles.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if (role.equals("ROLE_ADMIN")) {
                    Role adminRole = roleRepository.findByName(AvailableRoles.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.findByName(AvailableRoles.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
                }
            });
        }
        user.setUserId(new Generator().generateUserId());
        user.setStatus("Active");
        user.setRoles(roles);
        user.setRoleType("ROLE_USER");
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!!! with  UserId: " +user.getUserId()+" UserName: "+user.getUsername()));
    }

    @PostMapping("/admin/signup")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: AdminName is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User admin = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getDateOfBirth(),
                signUpRequest.getGender(),
                signUpRequest.getAddress(),
                signUpRequest.getPhoneNo(),
                signUpRequest.getPincode());

        Set<Role> roles = new HashSet<>();
        Role managerRole=roleRepository.findByName(AvailableRoles.ROLE_ADMIN).orElseThrow(()->new RuntimeException("Role is not Found"));
        roles.add(managerRole);
        admin.setUserId(new Generator().generateAdminId());
        admin.setStatus("Active");
        admin.setRoles(roles);
        admin.setRoleType("ROLE_ADMIN");
        userRepository.save(admin);

        return ResponseEntity.ok(new MessageResponse("Admin registered successfully!!! with AdminId: "+admin.getUserId()+" UserName: "+admin.getUsername()));
    }
}