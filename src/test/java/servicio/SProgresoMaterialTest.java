package servicio;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import repositorio.*;
import modelo.*;
import java.sql.*;
import java.util.List;

// Clase de prueba para el servicio SProgresoMaterial utilizando la implementación real del repositorio.
public class SProgresoMaterialTest {
    // Servicios y repositorios reales.
    private RProgresoMaterial repoReal;
    private SProgresoMaterial servicio;

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
            st.execute("DELETE FROM Productos");
            st.execute("DELETE FROM Categorias");
            st.execute("DELETE FROM Usuarios");
            st.execute("DELETE FROM DatosPersonales");
            st.execute("DELETE FROM Roles");

            // Insertar datos mínimos necesarios.
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (1, 'Cliente')");
            st.execute("INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono) " +
                            "VALUES (1, 'Test', 'Usuario', '000')");
            st.execute("INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol) " +
                            "VALUES (1, 'test@mail.local', 'pass', 1, 1)");
            st.execute("INSERT INTO Categorias(idCategoria, nombre, descripcion) " +
                            "VALUES (1, 'General', 'Categoria de prueba')");

            // Crear un producto de tipo CURSO.
            st.execute("INSERT INTO Productos(idProducto, titulo, descripcion, precio, idEmprendedor, idCategoria, tipoProducto)" +
                            "VALUES (1, 'Curso de Prueba', 'Desc', 0.0, 1, 1, 'CURSO')");

            // Crear materiales del curso.
            st.execute("INSERT INTO Materiales(idMaterial, titulo, tipo, url, idCurso) " +
                            "VALUES (1, 'Video 1', 'VIDEO', 'http://m.com', 1)");
            st.execute("INSERT INTO Materiales(idMaterial, titulo, tipo, url, idCurso) " +
                            "VALUES (2, 'PDF 1', 'PDF', 'http://m.com/pdf', 1)");

            // Reactivar la integridad referencial.
            st.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
        // Inicializar repositorio y servicio reales.
        repoReal = new RProgresoMaterial(ConexionDB.getConnection());
        servicio = new SProgresoMaterial(repoReal);
    }

    // TEST : crear()
    @Test
    void testCrearProgresoExitoso() {
        Usuario u = new Usuario();
        u.setIdUsuario(1);

        Material m = new Material();
        m.setIdMaterial(1);

        ProgresoMaterial p = servicio.crear(u, m);
        assertNotNull(p);
        assertTrue(p.getIdProgreso() > 0);

        ProgresoMaterial desdeBD = servicio.obtener(1, 1);
        assertNotNull(desdeBD);
        assertFalse(desdeBD.isVisto());
    }

    // TEST : obtener()
    @Test
    void testObtenerProgresoNoExistente() {
        assertNull(servicio.obtener(1, 999));
    }

    // TEST : actualizar()
    @Test
    void testActualizarProgreso() {
        Usuario u = new Usuario(); u.setIdUsuario(1);
        Material m = new Material(); m.setIdMaterial(1);

        ProgresoMaterial p = servicio.crear(u, m);
        assertFalse(p.isVisto());
        p.setVisto(true);

        boolean ok = servicio.actualizar(p);
        assertTrue(ok);

        ProgresoMaterial desdeBD = servicio.obtener(1, 1);
        assertTrue(desdeBD.isVisto());
    }

    // TEST : marcarVisto() y marcarNoVisto()
    @Test
    void testMarcarVistoYNoVisto() {
        Usuario u = new Usuario(); u.setIdUsuario(1);
        Material m = new Material(); m.setIdMaterial(2);
        servicio.crear(u, m);

        assertTrue(servicio.marcarVisto(1, 2));
        assertTrue(servicio.obtener(1, 2).isVisto());

        assertTrue(servicio.marcarNoVisto(1, 2));
        assertFalse(servicio.obtener(1, 2).isVisto());
    }

    // TEST : listarPorCliente()
    @Test
    void testListarPorCliente() {
        Usuario u = new Usuario();
        u.setIdUsuario(1);
        Material m1 = new Material(); m1.setIdMaterial(1);
        Material m2 = new Material(); m2.setIdMaterial(2);
        servicio.crear(u, m1);
        servicio.crear(u, m2);

        List<ProgresoMaterial> lista = servicio.listarPorCliente(1);
        assertEquals(2, lista.size());
    }

    // ID de cliente inválido.
    @Test
    void testListarPorClienteIdInvalido() {
        List<ProgresoMaterial> lista = servicio.listarPorCliente(-1);
        assertTrue(lista.isEmpty());
    }

    // TEST : contarVistos()
    @Test
    void testContarVistos() {
        Usuario u = new Usuario(); u.setIdUsuario(1);
        Material m1 = new Material(); m1.setIdMaterial(1);
        Material m2 = new Material(); m2.setIdMaterial(2);
        servicio.crear(u, m1);
        servicio.crear(u, m2);
        servicio.marcarVisto(1, 1);

        int vistos = servicio.contarVistos(1, 1);
        assertEquals(1, vistos);
    }
}
