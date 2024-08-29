package com.example.service.impl;


import com.example.model.User;
import com.example.repository.UserRepsitory;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepsitory userRepo;

    //save User in database
    @Override
    public User saveUser(User User){
        return userRepo.save(User);
    }

    //get all User form database
    @Override
    public List<User> getAllUser() {
        return userRepo.findAll();
    }

    //get User using id
    @Override
    public User getUserById(Long id) {
        Optional<User> User =  userRepo.findById(id);
        if(User.isPresent()){
            return User.get();
        }else {
            throw new RuntimeException();
        }
    }

    //update User
    @Override
    public User updateUser(User User, Long id) {
        User existingUser = userRepo.findById(id).orElseThrow(
                ()-> new RuntimeException()
        );
        existingUser.setUsername(User.getUsername());
        existingUser.setEmail(User.getEmail());
        // save
        userRepo.save(existingUser);
        return existingUser;
    }

    //delete User
    @Override
    public void deleteUser(Long id) {
        //check
        userRepo.findById(id).orElseThrow(()-> new RuntimeException());
        //delete
        userRepo.deleteById(id);
    }

	
}
