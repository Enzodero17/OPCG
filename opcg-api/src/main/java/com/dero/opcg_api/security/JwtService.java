package com.dero.opcg_api.security;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY = "TaCleSecreteSuperLongueEtTresSecuriseePourOpcgApi123!";

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // FABRIQUER LE BADGE (Lors du Login)
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // On cache l'email du joueur dans le badge
                .setIssuedAt(new Date(System.currentTimeMillis())) // Date de création
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Expire dans 24 heures
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // LIRE LE BADGE (Lors d'une action comme ouvrir un booster)
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // VÉRIFIER SI LE BADGE EST TOUJOURS VALIDE
    public boolean isTokenValid(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration().before(new Date());
    }
}
