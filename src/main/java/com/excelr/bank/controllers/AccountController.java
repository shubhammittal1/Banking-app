package com.excelr.bank.controllers;

// Import necessary classes and libraries

import com.excelr.bank.exception.UserNotFoundException;
import com.excelr.bank.models.Account;
import com.excelr.bank.models.Transaction;
import com.excelr.bank.payload.request.MobileRechargeRequest;
import com.excelr.bank.security.services.impl.AccountServiceImpl;
import jakarta.transaction.InvalidTransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
// Annotation to mark this class as a REST controller
@RestController
// Annotation to define the base URL for this controller's endpoints
@RequestMapping("/api/account")
public class AccountController {

	// Autowire the AccountServiceImpl to use its methods for business logic
	@Autowired
	private AccountServiceImpl accountService;


	// Autowire the AccountRepository to perform CRUD operations on the account data

	// Define a POST endpoint for creating a new account
	@PostMapping("/create")
	//Assign Roles to access Endpoints
	@PreAuthorize("hasRole('ROLE_USER')  or hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> createAccount(@RequestBody Account accountData,@RequestParam Long userId){
		// Call service method to create a new account
		 return accountService.createAccount(accountData,userId);
	}

	@PostMapping("/{accountNumber}/deposit")
	//Assign Roles to access this EndPoint
	@PreAuthorize("hasRole('ROLE_USER')  or hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> deposit(@PathVariable String accountNumber, @RequestBody Transaction transaction) throws InvalidTransactionException {
			try {
				return accountService.deposit(accountNumber, transaction);
			}catch (InvalidTransactionException e){
				throw new InvalidTransactionException("Invalid Data"+e.getMessage());
			}
    }

	@PostMapping("/{accountNumber}/withdraw")
	//Assign Roles to access this EndPoint
	@PreAuthorize("hasRole('ROLE_USER')  or hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> withdraw(@PathVariable String accountNumber, @RequestBody Transaction request) throws InvalidTransactionException {
		try {
			return accountService.withdraw(accountNumber, request);
		}catch (InvalidTransactionException e){
			throw new InvalidTransactionException("Invalid Data"+e.getMessage());
		}
	}
	@PostMapping("/{accountNumber}/recharge")
	//Assign Roles to access this EndPoint
	@PreAuthorize("hasRole('ROLE_USER')  or hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> recharge(@PathVariable String accountNumber, @RequestBody MobileRechargeRequest request) throws InvalidTransactionException {
		try {
			Transaction transaction=new Transaction();
			return accountService.recharge(accountNumber, request,transaction);
		}catch (InvalidTransactionException e){
			throw new InvalidTransactionException("Invalid Data"+e.getMessage());
		}
	}

	@PostMapping("/transfer")
	public ResponseEntity<?> transfer(@RequestBody Transaction request)  {
		try {
				request.setTransactionMode("Online");
				return accountService.transfer(request, request.getDepositAmount());
		}catch (InvalidTransactionException e ){
			e.getMessage();
			return ResponseEntity.status(HttpStatus.OK).body("Transaction Success");
		} catch (UserNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

	// Define a GET endpoint to retrieve all account records
	@GetMapping("/Records")
	//Assign Roles to access this EndPoint
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<Account>> getAllRecords() {
		List<Account> accounts = accountService.getAllRecords();
		return ResponseEntity.ok(accounts);
	}

	// Define a GET endpoint to retrieve an account by its ID
	@GetMapping("{id}")
	//Assign Roles to access this EndPoint
	@PreAuthorize("hasRole('ROLE_USER')  or hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> getAccountById(@PathVariable Long id){
		// Call service method to get account data by ID
		Account account = accountService.getAccountDataById(id);
		// Check if the account is found and return appropriate HTTP response
		if(account !=null) {
			// Return HTTP response with the list of accounts
			return ResponseEntity.status(HttpStatus.OK).body(account);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	// Update an existing account based on ID
    @PutMapping("/{id}")
	//Assign Roles to access this EndPoint
	@PreAuthorize("hasRole('ROLE_USER')  or hasRole('ROLE_ADMIN')")
	public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account accountDetails) {
		// Call service method to update the account with provided details
        Account updatedAccount = accountService.updateAccount(id, accountDetails);
		// Check if the update was successful and return appropriate HTTP response
        if (updatedAccount != null) {
            return ResponseEntity.ok(updatedAccount);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}