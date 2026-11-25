package cl.sonidoVivo.catalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.sonidoVivo.catalogo.model.Producto;

public interface ProductoRepository
        extends JpaRepository<Producto, String> {

    // Descuenta solo si alcanza el stock. Devuelve cuántas filas cambió (0 = no alcanzó o no existe).
    // Al hacerlo en una sola sentencia SQL, dos compras simultáneas no pueden dejar el stock negativo.
    @Modifying
    @Query("update Producto p set p.stock = p.stock - :cantidad "
            + "where p.codigo = :codigo and p.stock >= :cantidad")
    int descontarStock(@Param("codigo") String codigo, @Param("cantidad") int cantidad);
}
