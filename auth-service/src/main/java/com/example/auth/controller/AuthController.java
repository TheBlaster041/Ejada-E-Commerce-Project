package com.example.auth.controller;
import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import com.example.common.security.JwtUtil;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    public AuthController(UserRepository repo, PasswordEncoder encoder) {
        this.repo=repo; this.encoder=encoder; this.jwtUtil=new JwtUtil();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String,String> body) {
        String username=body.get("username"), password=body.get("password");
        if(username==null || password==null || username.isBlank() || password.isBlank())
            return ResponseEntity.badRequest().body(Map.of("message","username and password are required"));
        if(repo.existsByUsername(username))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","username already exists"));
        User user=new User(username, encoder.encode(password), "USER");
        repo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","registered successfully","username",username));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        return repo.findByUsername(body.get("username"))
            .filter(u->encoder.matches(body.get("password"),u.getPassword()))
            .<ResponseEntity<?>>map(u->ResponseEntity.ok(Map.of(
                "token",jwtUtil.generateToken(u.getUsername(),u.getRole()),
                "username",u.getUsername(),"role",u.getRole())))
            .orElseGet(()->ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","invalid credentials")));
    }
}
