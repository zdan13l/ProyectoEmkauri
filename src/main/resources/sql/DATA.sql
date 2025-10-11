-- SQL DDL para insertar datos.

-- Autor: PowerRangers
-- Versión: 2.0
-- Base de datos: H2

-- 1. ROLES BÁSICOS
INSERT INTO Roles (nombre) VALUES
('Emprendedor'),
('Cliente'),
('Reclutador');

-- 2. DATOS PERSONALES
INSERT INTO DatosPersonales (nombre, apellido, telefono) VALUES
('Daniel', 'Ortiz', '3001112233'),
('Andres', 'Loreto', '3002223344'),
('Maria', 'Cruz', '3003334455'),
('Andres', 'Ortiz', '3004445566'),
('Santiago', 'Hernandez', '3005556677'),
('Andres', 'Pinzon', '3006667788');

-- 3. USUARIOS
-- idRol, 1Emprendedor, 2Cliente, 3Reclutador.
INSERT INTO Usuarios (correo, contrasena, idDatos, idRol) VALUES
('zdan1el@emkauri.co', '1234', 1, 2),   -- Daniel (Cliente)
('loret01@emkauri.co', 'asdf', 2, 2),   -- AndresL (Cliente)
('mafc12@emkauri.co', 'qwert', 3, 1),  -- MafeC (Emprendedora)
('dresss@emkauri.co', '6789', 4, 2),    -- AndresO (Cliente)
('santiagoh@emkauri.co', 'jkl', 5, 3),  -- SantiagoH (Reclutador)
('apinzon@emkauri.co', 'password', 6, 1); -- AndresP (Emprendedor)

-- 4️. CATEGORÍAS
INSERT INTO Categorias (nombre, descripcion) VALUES
('Programación', 'Cursos y servicios relacionados con desarrollo de software'),
('Diseño', 'Cursos de diseño gráfico, UI/UX y más'),
('Marketing', 'Estrategias y herramientas de marketing digital');

-- 5️. PRODUCTOS (CURSOS Y SERVICIOS)
-- tipoProducto = 'CURSO' o 'SERVICIO'
INSERT INTO Productos (
    titulo, descripcion, precio, idEmprendedor, idCategoria, tipoProducto,
    duracionHoras, nivelDificultad, certificacion
) VALUES
('Java desde cero', 'Curso básico de Java', 120000, 3, 1, 'CURSO', 40, 'Básico', 'Certificado de participación'),
('Diseño UX', 'Principios de usabilidad y experiencia de usuario', 200000, 6, 2, 'CURSO', 30, 'Intermedio', 'Certificación UX');

INSERT INTO Productos (
    titulo, descripcion, precio, idEmprendedor, idCategoria, tipoProducto,
    duracionServicio, ubicacion, modalidad
) VALUES
('Consultoría en bases de datos', 'Optimización de queries y modelado de datos', 300000, 3, 1, 'SERVICIO', '2 horas', 'Online', 'Virtual'),
('Mentoría en marketing digital', 'Sesiones personalizadas de estrategia digital', 150000, 6, 3, 'SERVICIO', '3 horas', 'Bogotá', 'Presencial');

-- 6. MATERIALES (solo para cursos)
INSERT INTO Materiales (titulo, tipo, url, idCurso) VALUES
('Introducción a Java', 'PDF', 'https://emkauri.com/materials/java_intro.pdf', 1),
('Diseño centrado en el usuario', 'Video', 'https://emkauri.com/materials/ux_video.mp4', 2);

-- 7. PAGOS
INSERT INTO Pagos (monto, metodo, fecha) VALUES
(120000, 'Tarjeta', CURRENT_DATE),
(300000, 'Efectivo', CURRENT_DATE);

-- 8. COMPRAS
INSERT INTO Compras (idCliente, montoFinal, idPago) VALUES
(1, 120000, 1), -- Daniel compra curso Java
(2, 300000, 2); -- AndresL compra servicio BD

-- 9. COMPRAS-PRODUCTOS (asocia compra con productos)
INSERT INTO ComprasProductos (idCompra, idProducto, precioCompra) VALUES
(1, 1, 120000),
(2, 3, 300000);

-- 10. CALIFICACIONES
INSERT INTO Calificaciones (puntaje, comentario, idCliente, idProducto, fecha) VALUES
(5, 'Muy buen curso, me ayudó mucho', 1, 1, CURRENT_DATE),
(4, 'Contenido interesante pero algo corto', 2, 1, CURRENT_DATE);

-- 11. SOLICITUDES
INSERT INTO Solicitudes (idSolicitante, idReclutador, estado, mensaje, idEmprendedorAsociado) VALUES
(6, 5, 'PENDIENTE', 'Solicitud para aprobar como Emprendedor', 6);

INSERT INTO Solicitudes (idSolicitante, idReclutador, estado, mensaje, idProductoAsociado) VALUES
(3, 5, 'PENDIENTE', 'Revisión del curso Java desde cero', 1);