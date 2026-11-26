package cl.sonidoVivo.comprasRealizadas.cliente;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

// Habla con el microservicio de usuarios (puerto 8081)
@Component
public class UsuarioCliente {
    private final RestClient restClient;

    public UsuarioCliente(@Value("${servicios.usuarios.url}") String url){
        // Reenvía el token JWT del comprador, porque usuarios también está protegido
        this.restClient = RestClient.builder()
            .baseUrl(url)
            .requestInterceptor((request, body, ejecucion) -> {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth instanceof JwtAuthenticationToken jwt) {
                    request.getHeaders().setBearerAuth(jwt.getToken().getTokenValue());
                }
                return ejecucion.execute(request, body);
            })
            .build();
    }

    // Solo comprueba que el usuario exista; lanza error si no
    public void verificarExiste(Long usuarioId){
        try {
            restClient.get()
                .uri("/api/usuarios/{id}", usuarioId)
                .retrieve()
                .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El usuario " + usuarioId + " no existe");
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE, "El servicio de usuarios no está disponible");
        }
    }
}
