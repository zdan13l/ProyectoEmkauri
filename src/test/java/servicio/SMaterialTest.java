package servicio;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import repositorio.*;
import modelo.*;
import java.sql.*;
import java.util.List;

// Clase de prueba para el servicio SMaterial utilizando la implementación real del repositorio.
public class SMaterialTest {
    // Servicios y repositorios reales.
    private RMaterial repoReal;
    private SMaterial servicio;

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
        try (Connection con = ConexionDB.getConnection(); Statement st = con.createStatement()) {
            // Desactivar temporalmente la integridad referencial para limpiar sin orden estricto.
            st.execute("SET REFERENTIAL_INTEGRITY FALSE");

            // Limpiar todas las tablas relevantes.
            st.execute("DELETE FROM ProgresoMateriales");
            st.execute("DELETE FROM Materiales");
            st.execute("DELETE FROM Productos");
            st.execute("DELETE FROM Categorias");
            st.execute("DELETE FROM Usuarios");
            st.execute("DELETE FROM DatosPersonales");
            st.execute("DELETE FROM Roles");

            // Insertar datos mínimos necesarios.
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (1, 'Cliente')");
            st.execute("INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono) " +
                            "VALUES (1,'Test','User','000')");
            st.execute("INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol) " +
                            "VALUES (1,'test@mail.com','pass',1,1)");
            st.execute("INSERT INTO Categorias(idCategoria, nombre, descripcion) " +
                            "VALUES (1,'General','Test categoria')");
            st.execute("INSERT INTO Productos(idProducto, titulo, descripcion, precio, idEmprendedor, idCategoria, tipoProducto)" +
                            "VALUES (1, 'Curso Test', 'Descripción', 0.0, 1, 1, 'CURSO')");

            // Reactivar la integridad referencial.
            st.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
        // Reinstanciar repositorio y servicio para cada test.
        repoReal = new RMaterial(ConexionDB.getConnection());
        servicio = new SMaterial(repoReal);
    }

    // TEST : insertar()
    @Test
    void testInsertarMaterial() {
        Material material = new Material();
        material.setTitulo("Guía PDF");
        material.setTipo("Documento");
        material.setUrl("https://ejemplo.com/guia.pdf");
        servicio.insertar(material, 1);

        List<Material> lista = servicio.listarPorCurso(1);
        assertEquals(1, lista.size());
        assertTrue(lista.get(0).getIdMaterial() > 0);
        assertEquals("Guía PDF", lista.get(0).getTitulo());
    }

    // TEST : listarPorCurso()
    @Test
    void testListarPorCursoVacio() {
        List<Material> lista = servicio.listarPorCurso(999);
        assertNotNull(lista);
        assertEquals(0, lista.size());
    }

    // TEST: buscarPorId()
    @Test
    void testBuscarPorId() {
        Material m = new Material();
        m.setTitulo("Video");
        m.setTipo("MP4");
        m.setUrl("https://video.com");
        servicio.insertar(m, 1);

        Material encontrado = servicio.buscarPorId(m.getIdMaterial());
        assertNotNull(encontrado);
        assertEquals("Video", encontrado.getTitulo());
        assertEquals("MP4", encontrado.getTipo());
        assertEquals("https://video.com", encontrado.getUrl());
    }

    // TEST: modificar()
    @Test
    void testModificarMaterial() {
        Material m = new Material();
        m.setTitulo("Archivo");
        m.setTipo("PDF");
        m.setUrl("https://original.com");
        servicio.insertar(m, 1);

        m.setTitulo("Archivo Modificado");
        m.setUrl("https://modificado.com");
        servicio.modificar(m);

        Material actualizado = servicio.buscarPorId(m.getIdMaterial());
        assertEquals("Archivo Modificado", actualizado.getTitulo());
        assertEquals("https://modificado.com", actualizado.getUrl());
    }

    // TEST: eliminar()
    @Test
    void testEliminarMaterial() {
        Material m = new Material();
        m.setTitulo("Temporal");
        m.setTipo("TXT");
        m.setUrl("https://tmp.com");
        servicio.insertar(m, 1);

        int id = m.getIdMaterial();
        servicio.eliminar(id);

        assertNull(servicio.buscarPorId(id));
        assertEquals(0, servicio.listarPorCurso(1).size());
    }
}
