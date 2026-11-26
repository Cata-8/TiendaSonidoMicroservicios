package cl.sonidoVivo.mensajeria.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cl.sonidoVivo.mensajeria.model.Mensajeria;
import cl.sonidoVivo.mensajeria.repository.MensajeriaRepository;

@Service 
public class MensajeriaService {
    private final MensajeriaRepository repository;

    public MensajeriaService(MensajeriaRepository repository){
        this.repository = repository;
    }

    public Mensajeria guardar(Mensajeria contacto){
        contacto.setId(null);
        contacto.setEstado("PENDIENTE");

        return repository.save(contacto);
    }

    public List<Mensajeria> listar(){
        return repository.findAll();
    }

    public Mensajeria buscar(Long id){
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe el mensaje " + id));
    }

    public Mensajeria cambiarEstado(Long id, String estado){
        Mensajeria mensaje = buscar(id);
        mensaje.setEstado(estado);
        return repository.save(mensaje);
    }

    public void eliminar(Long id){
        buscar(id); // 404 si no existe
        repository.deleteById(id);
    }
}
