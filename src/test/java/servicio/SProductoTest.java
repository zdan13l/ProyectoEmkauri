package servicio;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import repositorio.*;
import modelo.*;
import java.sql.*;
import java.util.List;

// Clase de prueba para el servicio SProducto utilizando la implementación real del repositorio.
public class SProductoTest {
    // Servicios y repositorios reales.
    private RProducto repoReal;
    private SProducto servicio;

    // Configuración inicial antes de todos los tests.
    @BeforeAll
    static void iniciarServidorBD() throws Exception {
        // Activar modo pruebas
        ConexionDB.setModoPruebas(true);

        // Iniciar servidor H2 TCP
        ConexionDB.startTcpAndWebServer();

        // Crear la BD en memoria con DDL y DATA
        try (Connection conexion = ConexionDB.getConnection()) {
            ConexionDB.initSchema(conexion);
            ConexionDB.loadTestData(conexion);
        }
    }

    // Preparar la base de datos antes de cada test.
    @BeforeEach
    void prepararCadaTest() throws Exception {
        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement()) {
            // Desactivar temporalmente la integridad referencial para limpiar sin orden estricto.
            st.execute("SET REFERENTIAL_INTEGRITY FALSE");

            // Limpiar todas las tablas relevantes.
            st.execute("DELETE FROM ProgresoMateriales");
            st.execute("DELETE FROM Materiales");
            st.execute("DELETE FROM ComprasProductos");
            st.execute("DELETE FROM Compras");
            st.execute("DELETE FROM Pagos");
            st.execute("DELETE FROM Calificaciones");
            st.execute("DELETE FROM Productos");
            st.execute("DELETE FROM Categorias");
            st.execute("DELETE FROM Usuarios");
            st.execute("DELETE FROM DatosPersonales");
            st.execute("DELETE FROM Roles");

            // Insertar datos mínimos necesarios.
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (1,'Cliente'),(2,'Emprendedor')");
            st.execute("INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono) VALUES (1,'Test','User','123')");
            st.execute("INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol) " +
                            "VALUES (1,'test@mail','123',1,2)");
            st.execute("INSERT INTO Categorias(idCategoria, nombre, descripcion) VALUES (1,'General','Pruebas')");

            // Reactivar la integridad referencial.
            st.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
        // Inicializar repositorio y servicio reales.
        repoReal = new RProducto(ConexionDB.getConnection());
        servicio = new SProducto(repoReal);
    }

    // TEST : crearProducto()
    @Test
    void testCrearProducto() {
        Curso producto = new Curso();
        producto.setTitulo("Producto Test");
        producto.setDescripcion("Descripción");
        producto.setEstado("ACTIVO");
        producto.setPrecio(50.0);

        Usuario emprendedor = new Usuario();
        emprendedor.setIdUsuario(1);
        producto.setEmprendedor(emprendedor);

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1);
        producto.setCategoria(categoria);

        producto.setDuracionCurso(10);
        producto.setNivelDificultad("Básico");
        producto.setCertificacion("Sí");
        boolean creado = servicio.crearProducto(producto);

        assertTrue(creado);
        assertTrue(producto.getIdProducto() > 0);
    }

    // TEST : obtenerProductoPorId()
    @Test
    void testObtenerProductoPorId() throws Exception {
        try (Connection con = ConexionDB.getConnection(); Statement st = con.createStatement()) {

            st.execute("INSERT INTO Productos(idProducto, titulo, descripcion, precio, estado, " +
                            "idEmprendedor, idCategoria, tipoProducto, duracionCurso, nivelDificultad, certificacion) " +
                            "VALUES (10,'Prod A','Desc',100,'ACTIVO',1,1,'CURSO',5,'Bajo','Cert')");

            st.execute("INSERT INTO Solicitudes(idSolicitud, estado, idProductoAsociado) " +
                            "VALUES (10,'APROBADO',10)");
        }

        Producto producto = servicio.obtenerProductoPorId(10);
        assertNotNull(producto);
        assertEquals("Prod A", producto.getTitulo());
    }


    // TEST : buscarPorNombre()
    @Test
    void testBuscarPorNombre() throws Exception {
        try (Connection con = ConexionDB.getConnection(); Statement st = con.createStatement()) {
            st.execute("INSERT INTO Productos(idProducto,titulo,descripcion,precio,estado," +
                            "idEmprendedor,idCategoria,tipoProducto,duracionCurso,nivelDificultad,certificacion) " +
                            "VALUES (20,'Curso Java Básico','...',0,'ACTIVO',1,1,'CURSO',5,'Bajo','Cert')");

            st.execute("INSERT INTO Productos(idProducto,titulo,descripcion,precio,estado," +
                            "idEmprendedor,idCategoria,tipoProducto,duracionCurso,nivelDificultad,certificacion) " +
                            "VALUES (21,'Curso Java Avanzado','...',0,'ACTIVO',1,1,'CURSO',5,'Medio','Cert')");

            st.execute("INSERT INTO Solicitudes(idSolicitud, estado, idProductoAsociado) VALUES (20,'APROBADO',20)");
            st.execute("INSERT INTO Solicitudes(idSolicitud, estado, idProductoAsociado) VALUES (21,'APROBADO',21)");
        }

        List<Producto> lista = servicio.buscarPorNombre("Java");
        assertEquals(2, lista.size());
    }


    // TEST : listarProductos()
    @Test
    void testListarProductos() throws Exception {
        try (Connection con = ConexionDB.getConnection(); Statement st = con.createStatement()) {
            st.execute("INSERT INTO Productos(idProducto,titulo,descripcion,precio,estado,idEmprendedor,idCategoria,tipoProducto, duracionCurso) " +
                            "VALUES (1,'A','a',0,'ACTIVO',1,1,'CURSO',5)");

            st.execute("INSERT INTO Productos(idProducto,titulo,descripcion,precio,estado,idEmprendedor,idCategoria,tipoProducto, duracionServicio, ubicacion, modalidad) " +
                            "VALUES (2,'B','b',0,'ACTIVO',1,1,'SERVICIO',10,'Online','Remoto')");

            st.execute("INSERT INTO Productos(idProducto,titulo,descripcion,precio,estado,idEmprendedor,idCategoria,tipoProducto, duracionCurso) " +
                            "VALUES (3,'C','c',0,'ACTIVO',1,1,'CURSO',8)");

            st.execute("INSERT INTO Solicitudes(idSolicitud, estado, idProductoAsociado) VALUES (1,'APROBADO',1)");
            st.execute("INSERT INTO Solicitudes(idSolicitud, estado, idProductoAsociado) VALUES (2,'APROBADO',2)");
            st.execute("INSERT INTO Solicitudes(idSolicitud, estado, idProductoAsociado) VALUES (3,'APROBADO',3)");
        }
        List<Producto> lista = servicio.listarProductos();
        assertEquals(3, lista.size());
    }


    // TEST : eliminarProducto()
    @Test
    void testEliminarProducto() throws Exception {
        try (Connection con = ConexionDB.getConnection(); Statement st = con.createStatement()) {
            st.execute("INSERT INTO Productos(idProducto,titulo,descripcion,precio,estado,idEmprendedor,idCategoria,tipoProducto, duracionServicio, ubicacion, modalidad) " +
                            "VALUES (5,'Eliminar','x',10,'ACTIVO',1,1,'SERVICIO',10,'Online','Remoto')");

            st.execute("INSERT INTO Solicitudes(idSolicitud, estado, idProductoAsociado) VALUES (5,'APROBADO',5)");
        }
        boolean eliminado = servicio.eliminarProducto(5);
        assertTrue(eliminado);
        assertNull(servicio.obtenerProductoPorId(5));
    }
}
