package servicio;

import modelo.Material;
import org.junit.jupiter.api.*;
import repositorio.ConexionDB;
import repositorio.RMaterial;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SMaterialTest {

    private RMaterial repoReal;
    private SMaterial servicio;

    @BeforeAll
    static void iniciarServidorBD() throws Exception {
        ConexionDB.setModoPruebas(true);
        ConexionDB.startTcpAndWebServer();

        try (Connection conexion = ConexionDB.getConnection()) {
            ConexionDB.initSchema(conexion);
            ConexionDB.loadTestData(conexion);
        }
    }

    @BeforeEach
    void prepararCadaTest() throws Exception {

        try (Connection con = ConexionDB.getConnection();
             Statement st = con.createStatement()) {

            st.execute("SET REFERENTIAL_INTEGRITY FALSE");

            st.execute("DELETE FROM ProgresoMateriales");
            st.execute("DELETE FROM Materiales");
            st.execute("DELETE FROM Cursos");
            st.execute("DELETE FROM Productos");
            st.execute("DELETE FROM Categorias");
            st.execute("DELETE FROM Usuarios");
            st.execute("DELETE FROM DatosPersonales");
            st.execute("DELETE FROM Roles");

            // DATOS BASE
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (1, 'Cliente')");
            st.execute("INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono) " +
                    "VALUES (1,'Test','User','000')");

            st.execute("INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol) " +
                    "VALUES (1,'test@mail.com','pass',1,1)");

            st.execute("INSERT INTO Categorias(idCategoria, nombre, descripcion) " +
                    "VALUES (1,'General','Test categoria')");

            // Producto tipo CURSO
            st.execute("""
                INSERT INTO Productos(idProducto, titulo, descripcion, precio, idEmprendedor, idCategoria, tipoProducto)
                VALUES (1, 'Curso Test', 'Descripción', 0.0, 1, 1, 'CURSO')
            """);

            // Curso asociado a Producto
            st.execute("""
                INSERT INTO Cursos(idCurso, idProducto, descripcion)
                VALUES (1, 1, 'Curso base para pruebas')
            """);

            st.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }

        repoReal = new RMaterial(ConexionDB.getConnection());
        servicio = new SMaterial(repoReal);
    }

    // -------------------------------------------------------------
    // TEST: INSERTAR MATERIAL
    // -------------------------------------------------------------
    @Test
    void testInsertarMaterial() {
        Material m = new Material();
        m.setTitulo("Guía PDF");
        m.setTipo("Documento");
        m.setUrl("https://ejemplo.com/guia.pdf");

        servicio.insertar(m, 1);

        List<Material> lista = servicio.listarPorCurso(1);

        assertEquals(1, lista.size());
        assertTrue(lista.get(0).getIdMaterial() > 0);
        assertEquals("Guía PDF", lista.get(0).getTitulo());
    }

    // -------------------------------------------------------------
    // TEST: LISTAR VACÍO
    // -------------------------------------------------------------
    @Test
    void testListarPorCursoVacio() {
        List<Material> lista = servicio.listarPorCurso(999);
        assertNotNull(lista);
        assertEquals(0, lista.size());
    }

    // -------------------------------------------------------------
    // TEST: BUSCAR POR ID
    // -------------------------------------------------------------
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

    // -------------------------------------------------------------
    // TEST: MODIFICAR
    // -------------------------------------------------------------
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

    // -------------------------------------------------------------
    // TEST: ELIMINAR
    // -------------------------------------------------------------
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
