package cl.sonidoVivo.comprasRealizadas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.sonidoVivo.comprasRealizadas.model.Pedido;

public interface PedidoRepository
        extends JpaRepository<Pedido, Long> {

    boolean existsByNumero(String numero);

    List<Pedido> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
}
