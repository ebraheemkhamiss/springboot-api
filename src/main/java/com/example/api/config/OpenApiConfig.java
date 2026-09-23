package com.example.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * إعدادات صفحة توثيق الـ API (Swagger UI).
 * بعد تشغيل المشروع، تقدر تفتح التوثيق التفاعلي على:
 * http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Products API")
                        .version("1.0.0")
                        .description("A REST API for managing and storing product data in a database"
                                + "\n\nPowered and developed by: Ibrahim khamiss — +201014778296")
                        .contact(new Contact()
                                .name("Ibrahim khamiss")
                                .email("support@example.com")));
    }
}
