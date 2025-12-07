package com.elec_business.service;

import com.elec_business.StaticResourcesConfig;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Classe utilitaire qu'on pourra assigner à des DTO ou autre pour faire en sorte de leur rajouter les liens
 * absolus vers leurs images
 */
public class  UrlBuilder {
    private String filename;

    public UrlBuilder(String filename) {
        this.filename = filename;
    }


    /**
     * Méthode qui renvoie le lien absolu vers l'image en prenant en compte le protocol et le nom de domaine
     * du serveur. Par exemple si mon image est "test.jpg", cette méthode peut renvoyer "http://localhost:8080/uploads/test.jpg"
     */
    public String getOriginal() {

        return ServletUriComponentsBuilder.fromCurrentContextPath().toUriString() +
                StaticResourcesConfig.UPLOAD_URL_PREFIX  + filename;
    }

    /**
     * Même chose qu'au dessus, mais avec thumbnail- avant le nom du fichier picture, pour renvoyer spécifiquement
     * le thumbnail
     */
    public String getThumbnail() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().toUriString() +
                StaticResourcesConfig.uUPLOAD_URL_PREFIX+"thumbnail-" +  filename;
    }
}
