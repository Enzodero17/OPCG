package com.dero.opcg_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Cette méthode s'exécute à chaque requête
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // On regarde, si le joueur a présenté un badge dans l'en-tête "Authorization"
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Si pas de badge, ou s'il ne commence pas par "Bearer " (Porteur), on le laisse passer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // On découpe le texte pour ne garder que le code secret (on enlève les 7 lettres de "Bearer ")
        jwt = authHeader.substring(7);

        // On demande à notre machine de lire l'email caché dans le badge
        userEmail = jwtService.extractEmail(jwt);

        // Si on a trouvé un email et que le joueur n'est pas encore connecté dans ce cycle...
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // On va chercher le joueur dans la base de données
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // On vérifie si le badge est toujours valide et correspond bien au joueur
            if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {

                // Si oui, on lui donne l'autorisation officielle d'entrer !
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // On passe à la suite (le contrôleur qui va donner les boosters ou les stats)
        filterChain.doFilter(request, response);
    }
}