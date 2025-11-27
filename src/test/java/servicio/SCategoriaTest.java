package servicio;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import modelo.Categoria;
import repositorio.*;
import java.sql.*;
import java.util.List;

// Clase de prueba para el servicio SCategoria utilizando la implementación real del repositorio.
public class SCategoriaTest {
    // Servicios y repositorios reales.
    private RCategoria repoReal;
    private SCategoria servicio;

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
            st.execute("DELETE FROM Calificaciones");
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

            // Datos mínimos requeridos para Categorías
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (1, 'Cliente')");
            st.execute("INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono) VALUES (1, 'Test', 'User', '000')");
            st.execute("INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol) " +
                            "VALUES (1, 'test@mail.local', 'pass', 1, 1)");

            // Reactivar la integridad referencial.
            st.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
        // Inicializar el repositorio y servicio reales.
        repoReal = new RCategoria(ConexionDB.getConnection());
        servicio = new SCategoria(repoReal);
    }

    // TEST : crearCategoria()
    @Test
    void testCrearCategoria() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Tecnología");
        categoria.setDescripcion("Cursos y servicios tecnológicos");

        boolean resultado = servicio.crearCategoria(categoria);
        assertTrue(resultado);

        List<Categoria> lista = servicio.listarCategorias();
        assertEquals(1, lista.size());
        assertEquals("Tecnología", lista.get(0).getNombre());
    }

    // TEST: listarCategorias()
    @Test
    void testListarCategorias() throws Exception {
        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement()) {
            st.execute("INSERT INTO Categorias(idCategoria, nombre, descripcion) VALUES (1, 'Arte', 'Categoria arte')");
            st.execute("INSERT INTO Categorias(idCategoria, nombre, descripcion) VALUES (2, 'Música', 'Categoria música')");
        }

        List<Categoria> lista = servicio.listarCategorias();
        assertEquals(2, lista.size());
    }

    // TEST: eliminarCategoria()
    @Test
    void testEliminarCategoria() throws Exception {
        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement()) {
            st.execute("INSERT INTO Categorias(idCategoria, nombre, descripcion) VALUES (1, 'Deportes', 'Categoría deportes')");
        }

        boolean eliminado = servicio.eliminarCategoria(1);
        assertTrue(eliminado);

        assertTrue(servicio.listarCategorias().isEmpty());
    }

    // TEST: buscarPorNombre()
    @Test
    void testBuscarPorNombre() throws Exception {
        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement()) {
            st.execute("INSERT INTO Categorias(idCategoria, nombre, descripcion) VALUES (1, 'Cocina', 'Todo de cocina')");
        }

        Categoria encontrada = servicio.buscarPorNombre("Cocina");
        assertNotNull(encontrada);
        assertEquals("Cocina", encontrada.getNombre());
    }

    // TEST: buscarPorNombreParcial()
    @Test
    void testBuscarPorNombreParcial() throws Exception {
        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement()) {
            st.execute("INSERT INTO Categorias(idCategoria, nombre, descripcion) VALUES (1, 'Programación', 'Dev')");
            st.execute("INSERT INTO Categorias(idCategoria, nombre, descripcion) VALUES (2, 'Programación Web', 'Web Dev')");
        }

        List<Categoria> lista = servicio.buscarPorNombreParcial("Program");
        assertEquals(2, lista.size());
    }

    // TEST: actualizarCategoria()
    @Test
    void testActualizarCategoria() throws Exception {
        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement()) {
            st.execute("INSERT INTO Categorias(idCategoria, nombre, descripcion) VALUES (1, 'Viejo', 'Desc vieja')");
        }

        Categoria categoria = new Categoria(1, "Nuevo nombre", "Nueva descripción");

        boolean actualizado = servicio.actualizarCategoria(categoria);
        assertTrue(actualizado);

        Categoria result = servicio.buscarPorNombre("Nuevo nombre");
        assertNotNull(result);
        assertEquals("Nueva descripción", result.getDescripcion());
    }
}
