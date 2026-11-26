package cl.sonidoVivo.usuarios.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import cl.sonidoVivo.usuarios.model.Usuario;

// Crea el token JWT que identifica al usuario en todos los microservicios.
// Contenido (claims): sub = id del usuario, rol = ADMIN o CLIENTE, correo, nombre, y fecha de expiración.
// Va firmado (HS256) con el secreto compartido: si alguien lo modifica, la firma deja de coincidir.
@Service
public class JwtServicio {
    private final JwtEncoder encoder;
    private final long horas;

    public JwtServicio(JwtEncoder encoder, @Value("${seguridad.jwt.horas:2}") long horas){
        this.encoder = encoder;
        this.horas = horas;
    }

    public String generar(Usuario usuario){
        Instant ahora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("sonido-vivo-usuarios")
            .issuedAt(ahora)
            .expiresAt(ahora.plus(horas, ChronoUnit.HOURS))
            .subject(String.valueOf(usuario.getId()))
            .claim("rol", usuario.getRol().toUpperCase())
            .claim("correo", usuario.getCorreo())
            .claim("nombre", usuario.getNombre())
            .build();

        JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(cabecera, claims)).getTokenValue();
    }
}
