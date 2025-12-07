package com.elec_business;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class StaticResourcesConfig implements WebMvcConfigurer {

    @Value("${file.upload.folder}")
    private Path uploadFolder;

    public static final String UPLOAD_URL_PREFIX = "/uploads/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(UPLOAD_URL_PREFIX +"**")
                .addResourceLocations("file:"+uploadFolder);
    }
}

