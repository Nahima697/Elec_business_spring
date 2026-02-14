package com.elec_business.service;

import com.elec_business.entity.User;
import com.elec_business.service.impl.TokenPair;
import org.springframework.http.ResponseCookie;


public interface AuthService {
    /**
     * Méthode qui va utiliser le AuthenticationManager pour récupérer le UserDetails
     * et créer un JWT de celui ci
     * @param username,password Les informations de connexion du user (email/password)
     * @return User
     */
    User authenticateUser(String username, String password);

    ResponseCookie createAccessTokenCookie(String jwt);
    /**
     * Méthode qui va utiliser l'utilitaire JWTUtil pour créer un JWT
     * et créer un JWT de celui ci
     * @param user Les informations de connexion du user (username)
     * @return La réponse contenant le token et le username lié au user
     */
    String generateJwtToken(User user);

    /**
     * Méthode qui va creer un cookie http only avec le refresh token
     * génère les en-tête Cookie Http-Only
     * @param refreshToken Le refresh token à mettre dans le cookie
     * @return ResponseCookie
     */
     ResponseCookie createRefreshTokenCookie(String refreshToken);

    /**
     * Méthode qui va valider le refreshToken
     * @param  token
     * @return token
     */

     TokenPair validateRefreshToken(String token);
    /**
     * Méthode qui va generer  le refreshToken
     * @param  user
     * @return token
     */

    String generateRefreshToken(User user);

}
