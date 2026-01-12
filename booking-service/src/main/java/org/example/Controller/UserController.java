package org.example.Controller;

import org.example.DTO.UserDTO;
import org.example.Request.UserRequest;
import org.example.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDTO> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public UserDTO getUserById (@PathVariable Long id){
        return userService.getUsersById(id);
    }

    @PostMapping("/register")
    public UserDTO registerUser(@RequestBody UserRequest request){
        return userService.createUser(request);
    }
}
