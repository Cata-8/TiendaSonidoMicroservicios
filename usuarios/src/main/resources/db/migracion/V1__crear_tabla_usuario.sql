CREATE TABLE usuario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    run VARCHAR(12) NULL,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(100) NOT NULL DEFAULT '',
    correo VARCHAR(100) NOT NULL,
    fecha_nacimiento VARCHAR(10) NOT NULL DEFAULT '',
    rol VARCHAR(20) NOT NULL DEFAULT 'cliente',
    region VARCHAR(100) NOT NULL DEFAULT '',
    comuna VARCHAR(100) NOT NULL DEFAULT '',
    direccion VARCHAR(300) NOT NULL DEFAULT '',
    clave VARCHAR(100) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_correo (correo),
    UNIQUE KEY uk_usuario_run (run)
) DEFAULT CHARSET=utf8mb4;
