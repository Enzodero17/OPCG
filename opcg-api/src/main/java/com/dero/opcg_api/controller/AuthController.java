package com.dero.opcg_api.controller;

import com.dero.opcg_api.dto.AuthResponseDto;
import com.dero.opcg_api.dto.LoginDto;
import com.dero.opcg_api.dto.RegisterDto;
import com.dero.opcg_api.model.User;
import com.dero.opcg_api.repository.UserRepository;
import com.dero.opcg_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public User register(@RequestBody RegisterDto dto) {

        // On vérifie si l'email existe déjà
        if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cet email est déjà utilisé !");
        }

        // On crée le nouveau joueur
        User newUser = new User();
        newUser.setUsername(dto.getUsername());
        newUser.setEmail(dto.getEmail());

        // On hache le mot de passe avant de le sauvegarder !
        newUser.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        // On lui donne un peu de monnaie de départ
        newUser.setCoins(5000);

        return userRepo.save(newUser);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody LoginDto dto) {
        // On demande à Spring Security de vérifier les identifiants
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        // Si on arrive ici, c'est que l'email et le mot de passe sont bons !
        User user = userRepo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // La machine fabrique le badge crypté
        String jwtToken = jwtService.generateToken(user.getEmail());

        // On l'envoie dans notre belle boîte DTO
        return new AuthResponseDto(jwtToken, user.getId().toString(), user.getUsername());}
}