package com.excelr.bank.controllers;

import com.excelr.bank.models.AvailableRoles;
import com.excelr.bank.models.Role;
import com.excelr.bank.models.User;
import com.excelr.bank.payload.request.AdminSignupRequest;
import com.excelr.bank.payload.request.LoginRequest;
import com.excelr.bank.payload.request.LoginRequestById;
import com.excelr.bank.payload.request.SignupRequest;
import com.excelr.bank.payload.response.JwtResponse;
import com.excelr.bank.payload.response.MessageResponse;
import com.excelr.bank.repository.AdminRepository;
import com.excelr.bank.repository.RoleRepository;
import com.excelr.bank.repository.UserRepository;
import com.excelr.bank.security.jwt.JwtUtils;
import com.excelr.bank.security.services.impl.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  AdminRepository adminRepo;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("username/signin")
  public ResponseEntity<?> authenticateUserByName(@Valid @RequestBody LoginRequest loginRequest) {

          Authentication authentication = authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

          SecurityContextHolder.getContext().setAuthentication(authentication);
          String jwt= jwtUtils.generateJwtToken(authentication);
          UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
          List<String> roles = userDetails.getAuthorities().stream()
                  .map(GrantedAuthority::getAuthority)
                  .collect(Collectors.toList());

          Optional<User> userRepo=userRepository.findByUsername(loginRequest.getUsername());
          if(userRepo.isPresent()) {
              User user = userRepo.get();
              user.setToken(jwt);
              user.setTokenCreatedAt(LocalDateTime.now());
              user.setTokenExpAt(LocalDateTime.now().plusHours(2));
              userRepository.save(user);

          }else{
              return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Does not Exist");
          }
      return ResponseEntity.ok(new JwtResponse(jwt,
              userDetails.getId(),
              userDetails.getUsername(),
              userDetails.getEmail(),
              roles));
  }
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUserById(@Valid @RequestBody LoginRequestById loginRequest) {

            Authentication authenticationById = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getId(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authenticationById);
            String jwt = jwtUtils.generateJwtToken(authenticationById);
            UserDetailsImpl adminDetails = (UserDetailsImpl) authenticationById.getPrincipal();
            List<String> roles = adminDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            Optional<AdminSignupRequest> adminRepository=adminRepo.findByAdminId(loginRequest.getId());
            if(adminRepository.isPresent()) {
                AdminSignupRequest admin = adminRepository.get();
                admin.setToken(jwt);
                admin.setTokenCreatedAt(LocalDateTime.now());
                admin.setTokenExpiredAt(LocalDateTime.now().plusHours(2));
                adminRepo.save(admin);

                return ResponseEntity.ok(new JwtResponse(jwt,
                        adminDetails.getId(),
                        adminDetails.getUsername(),
                        adminDetails.getEmail(),
                        roles));
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Does not Exist");
            }

    }


//  public void removeTokens(LocalDateTime tokenExpiredTime,User user){
//      List<User> usersList=userRepository.findAll();
//      for(User userchk:usersList){
//          if((userchk.getToken()!=null) && userchk.getTokenCreatedAt()==(tokenExpiredTime)){
//              user.setToken(null);
//              user.setTokenCreatedAt(null);
//              userRepository.save(user);
//          }
//      }
//  }

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
                signUpRequest.getPhoneNo());

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(AvailableRoles.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "ROLE_ADMIN":
                        Role adminRole = roleRepository.findByName(AvailableRoles.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;

                    default:
                        Role userRole = roleRepository.findByName(AvailableRoles.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setStatus("Active");
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

  @PostMapping("/admin/signup")
  public ResponseEntity<?> registerAdmin(@Valid @RequestBody AdminSignupRequest adminSignUpRequest) {

      if (adminRepo.existsByName(adminSignUpRequest.getName())) {
          return ResponseEntity
                  .badRequest()
                  .body(new MessageResponse("Error: AdminName is already taken!"));
      }

      if (adminRepo.existsByEmail(adminSignUpRequest.getEmail())) {
          return ResponseEntity
                  .badRequest()
                  .body(new MessageResponse("Error: Email is already in use!"));
      }

      // Create new user's account
      AdminSignupRequest admin = new AdminSignupRequest(
              adminSignUpRequest.getName(),
              adminSignUpRequest.getEmail(),
              encoder.encode(adminSignUpRequest.getPassword()),
              adminSignUpRequest.getAge(),
              adminSignUpRequest.getGender(),
              adminSignUpRequest.getCity(),
              adminSignUpRequest.getStreet(),
              adminSignUpRequest.getContactNo(),
              adminSignUpRequest.getPincode());


      Set<String> strRoles = adminSignUpRequest.getRoles1();
      Set<Role> roles = new HashSet<>();

      if (strRoles == null) {
        Role userRole = roleRepository.findByName(AvailableRoles.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
      } else {
        strRoles.forEach(role -> {
          switch (role) {
            case "ROLE_ADMIN":
              Role adminRole = roleRepository.findByName(AvailableRoles.ROLE_ADMIN)
                      .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
              roles.add(adminRole);
              break;

            default:
              Role userRole = roleRepository.findByName(AvailableRoles.ROLE_ADMIN)
                      .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
              roles.add(userRole);
          }
        });
      }
      admin.setStatus("Active");
      admin.setRoles(roles);
      adminRepo.save(admin);

      return ResponseEntity.ok(new MessageResponse("Admin registered successfully!"));
    }
}