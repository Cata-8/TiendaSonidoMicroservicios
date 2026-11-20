package cl.sonidoVivo.mensajeria.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.sonidoVivo.mensajeria.model.Mensajeria;

public interface MensajeriaRepository 
        extends JpaRepository<Mensajeria, Long>{
}
