CREATE TABLE Usuario (
    idUsuario INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL
);

CREATE TABLE Cliente (
    idUsuario INT PRIMARY KEY,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);

CREATE TABLE Emprendedor (
    idUsuario INT PRIMARY KEY,
    estado VARCHAR(20) NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);

CREATE TABLE Reclutador (
    idUsuario INT PRIMARY KEY,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);

CREATE TABLE Categoria (
    idCategoria INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

CREATE TABLE Curso (
    idCurso INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    estado VARCHAR(20) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    idCategoria INT,
    idEmprendedor INT NOT NULL,
    FOREIGN KEY (idCategoria) REFERENCES Categoria(idCategoria),
    FOREIGN KEY (idEmprendedor) REFERENCES Emprendedor(idUsuario)
);

CREATE TABLE Servicio (
    idServicio INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    estado VARCHAR(20) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    idCategoria INT,
    idEmprendedor INT NOT NULL,
    FOREIGN KEY (idCategoria) REFERENCES Categoria(idCategoria),
    FOREIGN KEY (idEmprendedor) REFERENCES Emprendedor(idUsuario)
);

CREATE TABLE Pago (
    idPago INT PRIMARY KEY AUTO_INCREMENT,
    monto DECIMAL(10,2) NOT NULL,
    metodo VARCHAR(50) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha DATE NOT NULL
);

CREATE TABLE Compra (
    idCompra INT PRIMARY KEY AUTO_INCREMENT,
    fecha DATE NOT NULL,
    idCliente INT NOT NULL,
    idCurso INT,
    idServicio INT,
    idPago INT NOT NULL,
    FOREIGN KEY (idCliente) REFERENCES Cliente(idUsuario),
    FOREIGN KEY (idCurso) REFERENCES Curso(idCurso),
    FOREIGN KEY (idServicio) REFERENCES Servicio(idServicio),
    FOREIGN KEY (idPago) REFERENCES Pago(idPago)
);

CREATE TABLE Calificacion (
    idCalificacion INT PRIMARY KEY AUTO_INCREMENT,
    nota INT NOT NULL CHECK (nota BETWEEN 0 AND 100),
    comentario TEXT,
    fecha DATE NOT NULL,
    idCliente INT NOT NULL,
    idCurso INT NOT NULL,
    FOREIGN KEY (idCliente) REFERENCES Cliente(idUsuario),
    FOREIGN KEY (idCurso) REFERENCES Curso(idCurso)
);
