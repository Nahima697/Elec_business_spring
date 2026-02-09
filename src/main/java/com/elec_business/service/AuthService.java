package com.elec_business.service;

import com.elec_business.entity.User;
import com.elec_business.service.impl.TokenPair;
import org.springframework.http.ResponseCookie;


public interface AuthService {
    /**
     * Méthode qui va utiliser le AuthenticationManager pour récupérer le UserDetails
     * et créer un JWT de celui ci
     * @param username,password Les informations de connexion du user (email/password)
     * @return La réponse contenant le token et le user lié à ce token
     */
    User authenticateUser(String username, String password);

    ResponseCookie createRefreshTokenCookie(User user);

    String generateJwtToken(User user);                /**
     * Méthode qui va créer un nouveau refresh token et le faire persister en database
     * @param idUser L'id du user pour lequel on souhaite générer un refresh token
     * @return L'id du token généré
     */
    /**
     * Méthode qui va vérifier qu'un token existe en bdd, qu'il n'est pas expiré et s'il est ok,
     * génère un JWT, regénère un refresh token et supprimer l'ancien refresh token
     * @param token Le refresh token à valider
     * @return Les nouveaux refresh et jwt token
     */
     ResponseCookie createRefreshTokenCookie(String refreshToken);

        TokenPair validateRefreshToken(String token);

    String generateRefreshToken(User user);
}
