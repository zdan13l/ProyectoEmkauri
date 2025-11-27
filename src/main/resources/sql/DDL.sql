-- SQL DDL para la plataforma Emkauri.

-- Autor: PowerRangers
-- Versión: 2.0
-- Base de datos: H2

-- 1. TABLAS DE SOPORTE PARA USUARIOS.
CREATE TABLE Roles (
    idRol INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) UNIQUE NOT NULL -- "Emprendedor", "Cliente", "Reclutador".
);

CREATE TABLE DatosPersonales (
    idDatos INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(25)
);

-- 2. TABLA PRINCIPAL DE USUARIOS.
CREATE TABLE Usuarios (
    idUsuario INT PRIMARY KEY AUTO_INCREMENT,
    correo VARCHAR(255) UNIQUE NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    idDatos INT UNIQUE NOT NULL,
    idRol INT NOT NULL,

    FOREIGN KEY (idDatos) REFERENCES DatosPersonales(idDatos),
    FOREIGN KEY (idRol) REFERENCES Roles(idRol)
);


-- 3. TABLAS RELACIONADAS A PRODUCTOS
CREATE TABLE Categorias (
    idCategoria INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT
);

CREATE TABLE Productos (
    idProducto INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    precio NUMERIC(10, 2) NOT NULL CHECK (precio >= 0),
    idEmprendedor INT NOT NULL,
    idCategoria INT NOT NULL,

    -- Columna discriminadora para la herencia
    tipoProducto VARCHAR(10) NOT NULL CHECK (tipoProducto IN ('CURSO', 'SERVICIO')),

    -- Atributos específicos de Curso.
    duracionCurso INT,
    nivelDificultad VARCHAR(50),
    certificacion VARCHAR(50),

    -- Atributos específicos de Servicio.
    duracionServicio INT,
    ubicacion VARCHAR(50),
    modalidad VARCHAR(50),

    FOREIGN KEY (idEmprendedor) REFERENCES Usuarios(idUsuario),
    FOREIGN KEY (idCategoria) REFERENCES Categorias(idCategoria)
);

CREATE TABLE Materiales (
    idMaterial INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(255) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    url TEXT NOT NULL,
    idCurso INT NOT NULL,

    FOREIGN KEY (idCurso) REFERENCES Productos(idProducto)
);

-- 4. TABLAS DE LÓGICA DE NEGOCIO
CREATE TABLE Calificaciones (
    idCalificacion INT PRIMARY KEY AUTO_INCREMENT,
    puntaje INT NOT NULL CHECK (puntaje BETWEEN 1 AND 5),
    comentario TEXT,
    fecha DATE NOT NULL DEFAULT CURRENT_DATE,
    idCliente INT NOT NULL,
    idProducto INT NOT NULL,

    FOREIGN KEY (idCliente) REFERENCES Usuarios(idUsuario),
    FOREIGN KEY (idProducto) REFERENCES Productos(idProducto),
    UNIQUE (idCliente, idProducto)
);

CREATE TABLE Pagos (
    idPago INT PRIMARY KEY AUTO_INCREMENT,
    monto NUMERIC(10, 2) NOT NULL,
    metodo VARCHAR(50) NOT NULL,
    fecha DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE Compras (
    idCompra INT PRIMARY KEY AUTO_INCREMENT,
    idCliente INT NOT NULL,
    montoFinal NUMERIC(10, 2) NOT NULL,
    fechaCompra DATE NOT NULL DEFAULT CURRENT_DATE,
    idPago INT,

    FOREIGN KEY (idCliente) REFERENCES Usuarios(idUsuario),
    FOREIGN KEY (idPago) REFERENCES Pagos(idPago)
);

-- Tabla intermedia entre Compras y Productos
CREATE TABLE ComprasProductos (
    idCompra INT NOT NULL,
    idProducto INT NOT NULL,
    precioCompra NUMERIC(10, 2) NOT NULL,

    PRIMARY KEY (idCompra, idProducto),
    FOREIGN KEY (idCompra) REFERENCES Compras(idCompra),
    FOREIGN KEY (idProducto) REFERENCES Productos(idProducto)
);

CREATE TABLE Solicitudes (
    idSolicitud INT PRIMARY KEY AUTO_INCREMENT,
    idSolicitante INT NOT NULL,
    idReclutador INT,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'APROBADO', 'RECHAZADO', 'ACTIVO', 'INACTIVO')),
    mensaje TEXT,

    -- El producto o el usuario asociado a la solicitud.
    idProductoAsociado INT,
    idEmprendedorAsociado INT,

    FOREIGN KEY (idSolicitante) REFERENCES Usuarios(idUsuario),
    FOREIGN KEY (idReclutador) REFERENCES Usuarios(idUsuario),
    FOREIGN KEY (idProductoAsociado) REFERENCES Productos(idProducto),
    FOREIGN KEY (idEmprendedorAsociado) REFERENCES Usuarios(idUsuario)
);

CREATE TABLE ProgresoMateriales (
    idProgreso INT PRIMARY KEY AUTO_INCREMENT,
    idCliente INT NOT NULL,
    idMaterial INT NOT NULL,
    visto BOOLEAN NOT NULL DEFAULT FALSE,

    FOREIGN KEY (idCliente) REFERENCES Usuarios(idUsuario),
    FOREIGN KEY (idMaterial) REFERENCES Materiales(idMaterial),

    UNIQUE (idCliente, idMaterial)
);