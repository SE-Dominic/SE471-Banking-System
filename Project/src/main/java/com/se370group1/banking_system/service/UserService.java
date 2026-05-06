package com.se370group1.banking_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se370group1.banking_system.dto.UserDTO;
import com.se370group1.banking_system.model.User;
import com.se370group1.banking_system.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Boolean LogInUser(String username, String password) {
        System.out.printf("Log Service called");
        List<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isEmpty()) 
        {
            throw new IllegalAccessError("Username does not exist");
        } 
        else 
        {
            User user = existingUser.get(0);
            return user.getPassword().equals(password);
        }
    }

    public UserDTO createNewUser(String userID, String username, String password, String name, String email, int phoneNum, int socialSecurityNum, String address) {
        
        Boolean userAlreadyExists = CheckIfUserExists(username);

        
        if (userAlreadyExists)
        {
            throw new IllegalAccessError("Username already exists");
        }
        else
        {
            System.out.println(socialSecurityNum);
            User newUser = new User(userID, username, password, name, email, phoneNum, socialSecurityNum, address);
            userRepository.save(newUser);
    
            UserDTO savedUser = new UserDTO(newUser);
            return savedUser;
        }
    }
    
    public Boolean CheckIfUserExists(String username)
    {
        System.out.println("Checking if user exists");
        List<User> existingUsername = userRepository.findByUsername(username);
        Boolean userExists = !existingUsername.isEmpty();
        return userExists;
    }
    
}
