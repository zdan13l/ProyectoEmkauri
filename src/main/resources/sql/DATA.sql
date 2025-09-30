-- Insertar Usuarios
INSERT INTO Usuario (nombre, email, contrasena) VALUES
('DanielO', 'zdan1el@emkauri.com', '1234'),
('AndresL', 'loret01@emkauri.com', 'asdf'),
('MafeC', 'mafc12@emkauri.com', 'qwerty'),
('AndresO', 'dresssOrtiz@emkauri.com', '6789'),
('SantiagoH', 'santiagoh@emkauri.com', 'jkl'),
('AndresP', 'aPinzon10@emkauri.com', 'password');

-- Insertar Emprendedores
INSERT INTO Emprendedor (idUsuario, estado) VALUES
(3, 'Activo'),
(6, 'Pendiente');

-- Insertar Clientes
INSERT INTO Cliente (idUsuario) VALUES
(1),
(2),
(4);

-- Insertar Reclutadores
INSERT INTO Reclutador (idUsuario) VALUES
(5);

-- Insertar Categorías
INSERT INTO Categoria (nombre, descripcion) VALUES
('Programación', 'Cursos y servicios relacionados con desarrollo de software'),
('Diseño', 'Cursos de diseño gráfico, UI/UX y más'),
('Marketing', 'Estrategias y herramientas de marketing digital');

-- Insertar Cursos
INSERT INTO Curso (nombre, descripcion, estado, precio, idCategoria, idEmprendedor) VALUES
('Java desde cero', 'Curso básico de Java', 'Activo', 120.000, 1, 3),
('Diseño UX', 'Principios de usabilidad y experiencia de usuario', 'Activo', 200.000, 2, 6);

-- Insertar Servicios
INSERT INTO Servicio (nombre, descripcion, estado, precio, idCategoria, idEmprendedor) VALUES
('Consultoría en bases de datos', 'Optimización de queries y modelado de datos', 'Activo', 300.000, 1, 3),
('Mentoría en marketing digital', 'Sesiones personalizadas de estrategia digital', 'Pendiente', 150.000, 3, 6);

-- Insertar Pagos
INSERT INTO Pago (monto, metodo, estado, fecha) VALUES
(120.00, 'Tarjeta', 'Completado', CURRENT_DATE),
(300.00, 'Efectivo', 'Pendiente', CURRENT_DATE);

-- Insertar Compras
INSERT INTO Compra (fecha, idCliente, idCurso, idPago) VALUES
(CURRENT_DATE, 1, 1, 1);

INSERT INTO Compra (fecha, idCliente, idServicio, idPago) VALUES
(CURRENT_DATE, 2, 1, 2);

-- Insertar Calificaciones
INSERT INTO Calificacion (nota, comentario, fecha, idCliente, idCurso) VALUES
(90, 'Muy buen curso, me ayudó mucho', CURRENT_DATE, 1, 1),
(75, 'Contenido interesante pero algo corto', CURRENT_DATE, 2, 1);
