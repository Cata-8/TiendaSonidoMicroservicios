CREATE TABLE producto (
    codigo VARCHAR(20) NOT NULL,
    categoria VARCHAR(80) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    marca VARCHAR(80) NOT NULL,
    modelo VARCHAR(80) NOT NULL,
    stock INT NOT NULL,
    precio INT NOT NULL,
    descripcion TEXT NOT NULL,
    imagen VARCHAR(255) NOT NULL,
    PRIMARY KEY (codigo)
) DEFAULT CHARSET=utf8mb4;
