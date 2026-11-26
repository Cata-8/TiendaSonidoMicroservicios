package cl.sonidoVivo.comprasRealizadas.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

// Documentación Swagger: http://localhost:PUERTO/swagger-ui.html
// El botón "Authorize" permite pegar el token JWT que entrega POST /api/usuarios/login
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Sonido Vivo - Microservicio de Compras realizadas",
        version = "1.0",
        description = "API REST de la tienda Sonido Vivo. Los endpoints protegidos requieren un token JWT."),
    security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Token que devuelve POST /api/usuarios/login (sin escribir la palabra Bearer)")
public class OpenApiConfig {
}
