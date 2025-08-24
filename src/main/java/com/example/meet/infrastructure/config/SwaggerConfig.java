package com.example.meet.infrastructure.config;

import com.example.meet.infrastructure.dto.request.place.PlaceRequestDto;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        String jwt = "accessToken";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        ).addSchemas("PlaceRequestDto", new Schema<PlaceRequestDto>().example("{ \"name\": \"강남역\", \"xPos\": \"12.11\", \"yPos\": \"123.45\" }"));;
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .components(components);
    }
    private Info apiInfo() {
        return new Info()
                .title("API Test") // API의 제목
                .description("Meet API Doc") // API에 대한 설명
                .version("1.0.0"); // API의 버전
    }
}
