CREATE TABLE detalle_pedido (
    id BIGINT NOT NULL AUTO_INCREMENT,
    pedido_id BIGINT NOT NULL,
    producto_codigo VARCHAR(20) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    precio_unitario INT NOT NULL,
    cantidad INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_detalle_pedido FOREIGN KEY (pedido_id) REFERENCES pedido (id) ON DELETE CASCADE
) DEFAULT CHARSET=utf8mb4;
