package com.dero.opcg_api.security;

import com.dero.opcg_api.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class SecurityUtils {

    // Renvoie le joueur actuellement authentifié.
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof User currentUser)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise.");
        }
        return currentUser;
    }

    // Bloque avec un 403 si le joueur connecté essaie d'agir sur le compte d'un autre.
    public void requireSelf(UUID requestedUserId) {
        User currentUser = getCurrentUser();

        if (!currentUser.getId().equals(requestedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Tu n'as pas le droit d'accéder aux données d'un autre joueur");
        }
    }
}
