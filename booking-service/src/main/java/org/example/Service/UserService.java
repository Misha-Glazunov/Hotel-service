package org.example.Service;

import lombok.RequiredArgsConstructor;
import org.example.DTO.AuthRequest;
import org.example.DTO.AuthResponse;
import org.example.DTO.UserDTO;
import org.example.Entity.Role;
import org.example.Entity.User;
import org.example.security.JwtUtil;
import org.example.Map.UserMapper;
import org.example.Repository.UserRepository;
import org.example.Request.UserRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers(){
        List<User> users = userRepository.findAll();
        return userMapper.toUserDTOList(users);
    }

    public UserDTO getUsersById(Long id){
        User users = userRepository.findById(id).orElseThrow();
        return userMapper.toUserDTO(users);
    }

    public UserDTO createUser(UserRequest request) {
        // Проверяем, нет ли пользователя с таким логином
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // Шифруем пароль!
                .role(Role.USER) // По умолчанию роль USER
                .build();

        User savedUser = userRepository.save(user);
        return userMapper.toUserDTO(savedUser);
    }

    public AuthResponse authenticateUser(AuthRequest request) {
        // Находим пользователя
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        // Проверяем пароль
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // Генерируем токен
        String token = jwtUtil.generateToken(user);

        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }
}
