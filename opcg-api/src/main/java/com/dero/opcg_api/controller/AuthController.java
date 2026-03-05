package com.dero.opcg_api.controller;

import com.dero.opcg_api.dto.LoginDto;
import com.dero.opcg_api.dto.RegisterDto;
import com.dero.opcg_api.model.User;
import com.dero.opcg_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    // L'INSCRIPTION
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

    // LA CONNEXION
    @PostMapping("/login")
    public User login(@RequestBody LoginDto dto) {

        // On cherche l'utilisateur par son email
        User user = userRepo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable."));

        // On vérifie que le mot de passe tapé correspond au hachage dans la base de données
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mot de passe incorrect !");
        }

        // Si tout est bon, on renvoie les infos de l'utilisateur
        return user;
    }
}