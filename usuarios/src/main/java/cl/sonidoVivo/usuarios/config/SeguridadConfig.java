package cl.sonidoVivo.usuarios.config;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

// Seguridad del microservicio de Usuarios: API sin sesión de servidor (STATELESS) protegida con tokens JWT.
// El token lo emite el microservicio de usuarios al hacer login y cada servicio lo valida con el mismo secreto.
@Configuration
@EnableWebSecurity
public class SeguridadConfig {

    @Value("${seguridad.jwt.secreto}")
    private String secreto;

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())            // no hay cookies de sesión, así que CSRF no aplica
            .cors(Customizer.withDefaults())          // usa el bean corsConfigurationSource de abajo
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Swagger
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Registro y login son públicos
                .requestMatchers(HttpMethod.POST, "/api/usuarios/login", "/api/usuarios").permitAll()
                // Ver un usuario por id: cualquiera con sesión (el controlador exige que sea admin o el mismo usuario)
                .requestMatchers(HttpMethod.GET, "/api/usuarios/*").authenticated()
                // Listar, editar y eliminar usuarios: solo administrador
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            // Lee "Authorization: Bearer <token>", valida firma y expiración, y toma el rol del claim "rol"
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(convertidorDeRoles())));

        return http.build();
    }

    private SecretKey claveSecreta(){
        return new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    public JwtDecoder jwtDecoder(){
        return NimbusJwtDecoder.withSecretKey(claveSecreta()).macAlgorithm(MacAlgorithm.HS256).build();
    }

    // El claim "rol" del token (ADMIN o CLIENTE) pasa a ser la autoridad ROLE_ADMIN o ROLE_CLIENTE
    private JwtAuthenticationConverter convertidorDeRoles(){
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("rol");
        roles.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter convertidor = new JwtAuthenticationConverter();
        convertidor.setJwtGrantedAuthoritiesConverter(roles);
        return convertidor;
    }

    // Permite que el React (Vite, puerto 5173) llame a la API, incluida la cabecera Authorization
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of("http://localhost:5173"));
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/api/**", cors);
        return fuente;
    }

    // Cifra y compara claves con BCrypt
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // Firma los tokens JWT con el secreto compartido (solo este servicio emite tokens)
    @Bean
    public JwtEncoder jwtEncoder(){
        return new NimbusJwtEncoder(new ImmutableSecret<>(claveSecreta()));
    }
}
