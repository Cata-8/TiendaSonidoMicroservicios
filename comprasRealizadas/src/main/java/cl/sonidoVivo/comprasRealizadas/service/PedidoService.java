package cl.sonidoVivo.comprasRealizadas.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import cl.sonidoVivo.comprasRealizadas.cliente.CatalogoCliente;
import cl.sonidoVivo.comprasRealizadas.cliente.ItemStockRemoto;
import cl.sonidoVivo.comprasRealizadas.cliente.ProductoRemoto;
import cl.sonidoVivo.comprasRealizadas.cliente.UsuarioCliente;
import cl.sonidoVivo.comprasRealizadas.dto.ItemSolicitud;
import cl.sonidoVivo.comprasRealizadas.dto.PedidoRespuesta;
import cl.sonidoVivo.comprasRealizadas.dto.PedidoSolicitud;
import cl.sonidoVivo.comprasRealizadas.model.DetallePedido;
import cl.sonidoVivo.comprasRealizadas.model.Pedido;
import cl.sonidoVivo.comprasRealizadas.repository.PedidoRepository;

@Service
public class PedidoService {
    private final PedidoRepository repository;
    private final UsuarioCliente usuarioCliente;
    private final CatalogoCliente catalogoCliente;

    public PedidoService(PedidoRepository repository,
                         UsuarioCliente usuarioCliente,
                         CatalogoCliente catalogoCliente){
        this.repository = repository;
        this.usuarioCliente = usuarioCliente;
        this.catalogoCliente = catalogoCliente;
    }

    // usuarioId sale del token JWT del comprador (lo entrega el controlador)
    public PedidoRespuesta crear(Long usuarioId, PedidoSolicitud solicitud){
        // 1) El comprador debe existir en el microservicio de usuarios
        usuarioCliente.verificarExiste(usuarioId);

        // 2) Si el mismo producto viene repetido, se suman las cantidades
        Map<String, Integer> cantidades = new LinkedHashMap<>();
        for (ItemSolicitud item : solicitud.items()) {
            cantidades.merge(item.codigo().trim(), item.cantidad(), Integer::sum);
        }

        // 3) Se arma el pedido con nombre y precio REALES del catálogo (no los del cliente)
        Pedido pedido = new Pedido();
        pedido.setUsuarioId(usuarioId);

        List<ItemStockRemoto> descuentos = new ArrayList<>();
        int total = 0;
        int unidades = 0;

        for (Map.Entry<String, Integer> linea : cantidades.entrySet()) {
            ProductoRemoto producto = catalogoCliente.buscarProducto(linea.getKey());
            int cantidad = linea.getValue();

            if (producto.stock() < cantidad) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Stock insuficiente para " + producto.nombre());
            }

            DetallePedido detalle = new DetallePedido();
            detalle.setProductoCodigo(producto.codigo());
            detalle.setNombre(producto.nombre());
            detalle.setPrecioUnitario(producto.precio());
            detalle.setCantidad(cantidad);
            pedido.getDetalles().add(detalle);

            descuentos.add(new ItemStockRemoto(producto.codigo(), cantidad));
            total += producto.precio() * cantidad;
            unidades += cantidad;
        }

        pedido.setTotal(total);
        pedido.setUnidades(unidades);
        pedido.setNumero(generarNumero());

        // 4) Se descuenta el stock en catálogo (si no alcanza, catalogo responde 409 y no se guarda nada)
        catalogoCliente.descontarStock(descuentos);

        // 5) Recién ahora se guarda el pedido
        return PedidoRespuesta.desde(repository.save(pedido));
    }

    @Transactional(readOnly = true)
    public List<PedidoRespuesta> listar(Long usuarioId){
        List<Pedido> pedidos = usuarioId == null
            ? repository.findAll(Sort.by(Sort.Direction.DESC, "fechaCreacion"))
            : repository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);

        return pedidos.stream().map(PedidoRespuesta::desde).toList();
    }

    @Transactional(readOnly = true)
    public PedidoRespuesta buscar(Long id){
        return repository.findById(id)
            .map(PedidoRespuesta::desde)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe el pedido " + id));
    }

    private String generarNumero(){
        String numero;
        do {
            numero = "SV-" + ThreadLocalRandom.current().nextInt(100000, 1000000);
        } while (repository.existsByNumero(numero));
        return numero;
    }
}
