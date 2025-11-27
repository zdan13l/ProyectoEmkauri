-- SQL DATACOMPLETO para insertar datos completos.

-- Autor: PowerRangers
-- Versión: 3.0
-- Base de datos: H2

SET REFERENTIAL_INTEGRITY FALSE;

-- ------------------------------------------------------------
-- 1) ROLES
-- ------------------------------------------------------------
INSERT INTO Roles (nombre) VALUES
('Emprendedor'),
('Cliente'),
('Reclutador'),
('Administrador');

-- ------------------------------------------------------------
-- 2) DATOS PERSONALES
-- ------------------------------------------------------------
INSERT INTO DatosPersonales (nombre, apellido, telefono) VALUES
('Daniel', 'Ortiz', '3001112233'),    -- idDatos = 1
('Andres', 'Loreto', '3002223344'),   -- 2
('Maria', 'Cruz', '3003334455'),      -- 3
('Andres', 'Ortiz', '3004445566'),    -- 4
('Santiago', 'Hernandez', '3005556677'), -- 5
('Andres', 'Pinzon', '3006667788'),   -- 6
('Laura', 'Pérez', '3007778899'),      -- 7
('Camilo', 'García', '3008889900'),    -- 8
('Valentina', 'Ríos', '3009991122'),   -- 9
('Sebastián', 'Moreno', '3101112233'), -- 10
('Lucía', 'Ramírez', '3102223344'),    -- 11
('Felipe', 'Torres', '3103334455'),    -- 12
('Natalia', 'Bermúdez', '3104445566'), -- 13
('Carolina', 'Lozano', '3105556677'),  -- 14
('Julián', 'Martínez', '3106667788');  -- 15

-- ------------------------------------------------------------
-- 3) USUARIOS
-- ------------------------------------------------------------
INSERT INTO Usuarios (correo, contrasena, idDatos, idRol) VALUES
('zdan1el@emkauri.co', '1234', 1, 2),    -- idUsuario = 1 (Cliente)
('loret01@emkauri.co', 'asdf', 2, 2),    -- 2 (Cliente)
('mafc12@emkauri.co', 'qwert', 3, 1),    -- 3 (Emprendedor)
('dresss@emkauri.co', '6789', 4, 2),     -- 4 (Cliente)
('santiagoh@emkauri.co', 'jkl', 5, 3),   -- 5 (Reclutador)
('apinzon@emkauri.co', 'password', 6, 1),-- 6 (Emprendedor)
('laura.perez@emkauri.co', 'laura123', 7, 2),     -- 7 (Cliente)
('camilo.garcia@emkauri.co', 'abc123', 8, 1),     -- 8 (Emprendedor)
('valentina.rios@emkauri.co', 'vale123', 9, 2),   -- 9 (Cliente)
('seb.moreno@emkauri.co', 'pass1', 10, 1),        -- 10 (Emprendedor)
('lucia.ramirez@emkauri.co', 'pass2', 11, 3),     -- 11 (Reclutador)
('felipe.torres@emkauri.co', 'pass3', 12, 1),     -- 12 (Emprendedor)
('nata.bermudez@emkauri.co', 'pass4', 13, 2),     -- 13 (Cliente)
('carolina.lozano@emkauri.co', 'pass5', 14, 2),   -- 14 (Cliente)
('julian.m@emkauri.co', 'pass6', 15, 3);          -- 15 (Reclutador)

-- ------------------------------------------------------------
-- 4) CATEGORÍAS
-- ------------------------------------------------------------
INSERT INTO Categorias (nombre, descripcion) VALUES
('Programación', 'Cursos y servicios relacionados con desarrollo de software'),
('Diseño', 'Cursos de diseño gráfico y UX/UI'),
('Marketing', 'Estrategias de marketing digital'),
('Educación', 'Formación académica general'),
('Arte', 'Artes plásticas, música y creatividad'),
('Finanzas', 'Educación financiera y contabilidad'),
('Salud', 'Bienestar físico y mental'),
('Tecnología', 'Innovación y herramientas tecnológicas'),
('Idiomas', 'Aprendizaje de nuevas lenguas'),
('Desarrollo personal', 'Habilidades blandas y crecimiento personal');

-- ------------------------------------------------------------
-- 5) PRODUCTOS (CURSOS Y SERVICIOS)
-- ------------------------------------------------------------
-- Cursos
INSERT INTO Productos (
    titulo, descripcion, precio, idEmprendedor, idCategoria, tipoProducto,
    duracionCurso, nivelDificultad, certificacion
) VALUES
('Java desde cero', 'Curso básico de Java', 120000.00, 3, 1, 'CURSO', 40, 'Básico', 'Certificado de participación'),
('Diseño UX', 'Principios de usabilidad y experiencia de usuario', 200000.00, 6, 2, 'CURSO', 30, 'Intermedio', 'Certificación UX'),
('Marketing para redes sociales', 'Creación de contenido y estrategia', 180000.00, 8, 3, 'CURSO', 20, 'Básico', 'Certificado'),
('Python avanzado', 'Automatización, web scraping y APIs', 250000.00, 10, 1, 'CURSO', 60, 'Avanzado', 'Diploma digital'),
('Fundamentos de Ilustración', 'Técnicas de ilustración digital', 150000.00, 12, 5, 'CURSO', 25, 'Intermedio', 'Certificado');

-- Servicios
INSERT INTO Productos (
    titulo, descripcion, precio, idEmprendedor, idCategoria, tipoProducto,
    duracionServicio, ubicacion, modalidad
) VALUES
('Consultoría en bases de datos', 'Optimización de queries y modelado de datos', 300000.00, 3, 1, 'SERVICIO', 2, 'Online', 'Virtual'),
('Mentoría en marketing digital', 'Sesiones personalizadas de estrategia digital', 150000.00, 6, 3, 'SERVICIO', 3, 'Bogotá', 'Presencial'),
('Asesoría en finanzas personales', 'Organización financiera y presupuesto', 100000.00, 12, 6, 'SERVICIO', 2, 'Online', 'Virtual'),
('Clases de inglés', 'Clases particulares para mejorar fluidez', 90000.00, 10, 9, 'SERVICIO', 1, 'Online', 'Virtual'),
('Coaching de vida', 'Acompañamiento y establecimiento de metas', 120000.00, 8, 10, 'SERVICIO', 1, 'Bogotá', 'Híbrido');

-- ------------------------------------------------------------
-- 6) MATERIALES (apuntan a idCurso -> productos que sean 'CURSO')
-- ------------------------------------------------------------
INSERT INTO Materiales (titulo, tipo, url, idCurso) VALUES
('Introducción a Java', 'PDF', 'https://emkauri.com/materials/java_intro.pdf', 1),
('Guía UX profesional', 'PDF', 'https://emkauri.com/materials/ux_guide.pdf', 2),
('Marketing en Instagram - Video', 'Video', 'https://emkauri.com/materials/ig_marketing.mp4', 3),
('Automatización con Python', 'Video', 'https://emkauri.com/materials/python_auto.mp4', 4),
('Técnicas de ilustración digital', 'PDF', 'https://emkauri.com/materials/ilustracion.pdf', 5);

-- ------------------------------------------------------------
-- 7) PAGOS
-- ------------------------------------------------------------
INSERT INTO Pagos (monto, metodo, fecha) VALUES
(120000.00, 'Tarjeta', CURRENT_DATE),
(300000.00, 'Tarjeta', CURRENT_DATE),
(180000.00, 'Nequi', CURRENT_DATE),
(250000.00, 'PSE', CURRENT_DATE),
(90000.00, 'Efectivo', CURRENT_DATE),
(150000.00, 'Tarjeta', CURRENT_DATE);

-- ------------------------------------------------------------
-- 8) COMPRAS
-- ------------------------------------------------------------
INSERT INTO Compras (idCliente, montoFinal, fechaCompra, idPago) VALUES
(1, 120000.00, CURRENT_DATE, 1),  -- Daniel compra Java
(2, 300000.00, CURRENT_DATE, 2),  -- AndresL compra consultoría
(7, 180000.00, CURRENT_DATE, 3),  -- Laura compra Marketing
(9, 250000.00, CURRENT_DATE, 4),  -- Valentina compra Python
(13, 90000.00, CURRENT_DATE, 5),  -- Natalia compra Clases inglés
(14, 150000.00, CURRENT_DATE, 6); -- Carolina compra Mentoría marketing

-- ------------------------------------------------------------
-- 9) COMPRAS - PRODUCTOS (relaciona compras con productos existentes)
-- ------------------------------------------------------------
INSERT INTO ComprasProductos (idCompra, idProducto, precioCompra) VALUES
(1, 1, 120000.00),  -- compra 1 -> producto 1 (Java)
(2, 6, 300000.00),  -- compra 2 -> producto 6 (Consultoría BD)
(3, 3, 180000.00),  -- compra 3 -> producto 3 (Marketing redes)
(4, 4, 250000.00),  -- compra 4 -> producto 4 (Python avanzado)
(5, 9, 90000.00),   -- compra 5 -> producto 9 (Clases de inglés)
(6, 7, 150000.00);  -- compra 6 -> producto 7 (Mentoría marketing)

-- ------------------------------------------------------------
-- 10) CALIFICACIONES
-- ------------------------------------------------------------
INSERT INTO Calificaciones (puntaje, comentario, idCliente, idProducto, fecha) VALUES
(5, 'Muy buen curso, me ayudó mucho', 1, 1, CURRENT_DATE),
(4, 'Contenido interesante pero algo corto', 2, 1, CURRENT_DATE),
(5, 'Excelente mentoría', 14, 7, CURRENT_DATE),
(5, 'Muy claro y práctico', 7, 3, CURRENT_DATE),
(3, 'Esperaba más ejercicios', 9, 4, CURRENT_DATE);

-- ------------------------------------------------------------
-- 11) SOLICITUDES
-- ------------------------------------------------------------
-- Inserciones que solo refieren a un emprendedor asociado (no producto).
INSERT INTO Solicitudes (idSolicitante, idReclutador, estado, mensaje, idEmprendedorAsociado) VALUES
(3, 5, 'APROBADO', 'Solicitud aprobada para emprender', 3),
(6, 5, 'PENDIENTE', 'Solicitud de revisión', 6),
(8, 11, 'APROBADO', 'Solicitud de registro como emprendedor', 8);

-- Inserciones que también refieren a un producto asociado (idProductoAsociado, idEmprendedorAsociado).
INSERT INTO Solicitudes (idSolicitante, idReclutador, estado, mensaje, idProductoAsociado, idEmprendedorAsociado) VALUES
(3, 5, 'APROBADO', 'Solicitud sobre Curso Java desde cero', 1, 3),
(6, 5, 'PENDIENTE', 'Solicitud sobre Diseño UX', 2, 6),
(9, 11, 'RECHAZADO', 'Solicitud asociada a Asesoría en finanzas', 8, 12),
(7, 5, 'APROBADO', 'Solicitud asociada a Coaching de vida', 10, 8);

-- ------------------------------------------------------------
-- 12) PROGRESO MATERIALES
-- ------------------------------------------------------------
INSERT INTO ProgresoMateriales (idCliente, idMaterial, visto) VALUES
(1, 1, TRUE),
(1, 2, TRUE),
(7, 3, FALSE),
(9, 4, TRUE),
(13, 5, FALSE);

SET REFERENTIAL_INTEGRITY TRUE;
