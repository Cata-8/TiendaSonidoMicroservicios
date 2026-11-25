CREATE TABLE pedido (
    id BIGINT NOT NULL AUTO_INCREMENT,
    numero VARCHAR(20) NOT NULL,
    usuario_id BIGINT NULL,
    total INT NOT NULL,
    unidades INT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pedido_numero (numero),
    KEY idx_pedido_usuario (usuario_id)
) DEFAULT CHARSET=utf8mb4;
