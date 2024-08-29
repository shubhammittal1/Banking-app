package com.example.service;
import java.util.List;
import com.example.model.User;

public interface UserService {
    User saveUser(User User);
    List<User> getAllUser();
    User getUserById(Long id);
    User updateUser(User User,Long id);
    void deleteUser(Long id);
}