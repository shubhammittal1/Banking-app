package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.model.User;
import com.example.service.UserService;
import java.util.List;


@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> saveUser(@RequestBody User user){
    	System.out.print(user.getUsername());
    	System.out.print(user.getId());
    	System.out.print(user.getPhoneno());
    	System.out.print(user.getPassword());
        return new ResponseEntity<User>(userService.saveUser(user), HttpStatus.CREATED);
    }
    
    @GetMapping
    public List<User> getAllUser(){
        return userService.getAllUser();
    }

    @GetMapping(value="{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id){
        return new ResponseEntity<User>(userService.getUserById(id),HttpStatus.OK);
    }

    
    @PutMapping(value="{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id,
                                                   @RequestBody User User){
        return new ResponseEntity<User>(userService.updateUser(User,id),HttpStatus.OK);
    }

    //Delete Rest Api
    @DeleteMapping(value="{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        //delete User from db
        userService.deleteUser(id);
        return new ResponseEntity<String>("User deleted Successfully.",HttpStatus.OK);
    }

}
