package com.medsync.cadastroagendamento.infrastructure.config;

import com.medsync.cadastroagendamento.infrastructure.config.properties.AppProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI(AppProperties appProperties) {
        return new OpenAPI()
                .info(new Info()
                        .title(appProperties.getName())
                        .description("API para gerenciamento de usuários e agendamento de consultas médicas")
                            .version(appProperties.getVersion())
                        .contact(new Contact()
                                .name("MedSync Team")
                                .email("contato@medsync.com")
                                .url("https://medsync.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Insira o token JWT no formato: Bearer {token}")));
    }
}
