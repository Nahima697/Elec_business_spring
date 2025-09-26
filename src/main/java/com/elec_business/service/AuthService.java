package com.elec_business.service;

import com.elec_business.controller.dto.LoginCredentialsDTO;
import com.elec_business.controller.dto.LoginResponseDTO;
import com.elec_business.service.impl.TokenPair;


public interface AuthService {
    /**
     * Méthode qui va utiliser le AuthenticationManager pour récupérer le UserDetails
     * et créer un JWT de celui ci
     * @param credentials Les informations de connexion du user (email/password)
     * @return La réponse contenant le token et le user lié à ce token
     */
    LoginResponseDTO login(LoginCredentialsDTO credentials);

    /**
     * Méthode qui va créer un nouveau refresh token et le faire persister en database
     * @param idUser L'id du user pour lequel on souhaite générer un refresh token
     * @return L'id du token généré
     */
    String generateRefreshToken(String idUser);
    /**
     * Méthode qui va vérifier qu'un token existe en bdd, qu'il n'est pas expiré et s'il est ok,
     * génère un JWT, regénère un refresh token et supprimer l'ancien refresh token
     * @param token Le refresh token à valider
     * @return Les nouveaux refresh et jwt token
     */
    TokenPair validateRefreshToken(String token);
}
