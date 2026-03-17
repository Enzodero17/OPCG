# 🏴‍☠️ OPCG Gacha API - Backend

Une API RESTful développée en Java avec Spring Boot, simulant la mécanique complète d'un jeu de cartes à collectionner (Gacha) basé sur l'univers de One Piece.

## 🛠️ Technologies Utilisées

-   **Langage :** Java
-   **Framework :** Spring Boot (Web, Data JPA)
-   **Base de Données :** PostgreSQL / MySQL (via DBeaver)
-   **Sécurité :** Spring Security, BCrypt, JWT (JSON Web Tokens)
-   **Outils :** Maven, Lombok, Postman (pour les tests)

## ✨ Fonctionnalités Principales

### 1\. 🔒 Authentification & Sécurité (JWT)

-   **Inscription (**`**/register**`**) :** Création de compte sécurisée avec hachage du mot de passe via BCrypt et attribution d'un solde de départ (1000 pièces).
-   **Connexion (**`**/login**`**) :** Vérification des identifiants et génération d'un jeton d'accès (JWT) valable 24h.
-   **Filtre de Sécurité :** Protection de toutes les routes sensibles du jeu exigeant un token valide dans l'en-tête (Bearer Token) pour autoriser l'action.

### 2\. 🃏 Architecture des Cartes (Le Catalogue)

-   Modélisation complexe des données : **Sets** (Extensions), **Cards** (Cartes de base avec règles de jeu) et **CardVariants** (Les différentes illustrations, raretés et finitions comme _Parallel_ ou _Manga_).
-   **Route Pokédex / Bibliothèque :** Consultation de toutes les cartes existantes pour une extension donnée.

### 3\. 📦 Moteur de Gacha (Boosters)

-   **Algorithme de Tirage :** Simulation de hasard avec des taux de drop (probabilités) précis pour chaque rareté (Commune, Peu Commune, Rare, Super Rare, Secret Rare, etc.).
-   **Achat :** L'ouverture d'un booster coûte 500 pièces virtuelles, débitées automatiquement du compte joueur.

### 4\. 🎒 Inventaire & Économie (Collection)

-   **Stockage :** Enregistrement des cartes piochées dans l'inventaire personnel du joueur avec gestion intelligente des quantités (doublons).
-   **Statistiques :** Calcul dynamique du pourcentage de complétion d'une extension (ex: 85% de OP-01 possédé).
-   **Revente :** Système de "Crafting/Recyclage" permettant de détruire une carte en double contre des pièces virtuelles. Le prix de rachat est indexé sur la rareté de la carte (avec un multiplicateur pour les versions alternatives).

### 5\. 🎯 Rétention & Engagement (Missions)

-   **Récompense Quotidienne :** Un "Daily Login" offrant 1000 pièces gratuites toutes les 24h (basé sur l'horloge du serveur).
-   **Système de Quêtes :** Traçage des actions du joueur en arrière-plan (ex: "Ouvrir 10 boosters", "Vendre 5 cartes") pour faire avancer des jauges de progression.
-   **Réclamation :** Possibilité de récupérer des récompenses uniques une fois l'objectif d'une mission atteint.
