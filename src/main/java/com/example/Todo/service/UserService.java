package com.example.Todo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.Todo.model.MyUser;
import com.example.Todo.repository.UserRepository;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MyUser> user = userRepository.findByUsername(username);
        if (user.isPresent()){
        var userObj = user.get();
        return User.builder()
                .username(userObj.getUsername())
                .password(userObj.getPassword())
                .roles("USER")
                .build();    
        }else{
        throw new UsernameNotFoundException(username);
        }
    }


    // Find user by username (for authentication purposes)
    public Optional<MyUser> findByUsername(String username) {
        return userRepository.findByUsername(username);  // Find user by username
    }

    // Create a new user
    public MyUser createUser(MyUser user) {
        return userRepository.save(user);  // Save new user
    }

    public List<MyUser> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<MyUser> getUserById(Long id) {
        return userRepository.findById(id);
    }



}
