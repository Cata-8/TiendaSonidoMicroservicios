package cl.sonidoVivo.usuarios.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import cl.sonidoVivo.usuarios.model.Usuario;
import cl.sonidoVivo.usuarios.repository.UsuarioRepository;

@Configuration
public class UsuariosBaseConfig {

    // Si la tabla está vacía, crea los 2 usuarios base que tenía data/usuariosBase.ts en el React.
    // Se hace por código (y no en el .sql) para poder cifrar la clave con BCrypt.
    @Bean
    public CommandLineRunner cargarUsuariosBase(UsuarioRepository repository, PasswordEncoder encoder){
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setCorreo("sonidovivo@gmail.cl");
            admin.setRol("admin");
            admin.setClave(encoder.encode("1234"));
            repository.save(admin);

            Usuario cliente = new Usuario();
            cliente.setRun("123456789");
            cliente.setNombre("Usuario");
            cliente.setApellidos("Ejemplo");
            cliente.setCorreo("usuario@gmail.cl");
            cliente.setFechaNacimiento("1998-05-14");
            cliente.setRol("cliente");
            cliente.setRegion("Región Metropolitana de Santiago");
            cliente.setComuna("Santiago");
            cliente.setDireccion("Calle Hola 456");
            cliente.setClave(encoder.encode("5678"));
            repository.save(cliente);
        };
    }
}
