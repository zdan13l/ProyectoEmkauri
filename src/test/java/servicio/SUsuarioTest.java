package servicio;

import modelo.Datos;
import modelo.Rol;
import modelo.Usuario;
import org.junit.jupiter.api.*;
import repositorio.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class SUsuarioTest {

    private RUsuario repoReal;
    private SUsuario servicio;

    // ================================
    //   INICIO DEL SERVIDOR H2
    // ================================
    @BeforeAll
    static void iniciarServidorBD() throws Exception {

        // Modo prueba = BD en memoria
        ConexionDB.setModoPruebas(true);

        // Encender servidor TCP + consola H2
        ConexionDB.startTcpAndWebServer();

        // Crear schema y datos base (si aplica)
        try (Connection conexion = ConexionDB.getConnection()) {
            ConexionDB.initSchema(conexion);
            ConexionDB.loadTestData(conexion); // opcional
        }
    }

    // ================================
    //   LIMPIEZA + DATOS BASE
    // ================================
    @BeforeEach
    void prepararCadaTest() throws Exception {

        try (Connection conn = ConexionDB.getConnection();
             Statement st = conn.createStatement()) {

            st.execute("SET REFERENTIAL_INTEGRITY FALSE");

            st.execute("DELETE FROM Solicitudes");
            st.execute("DELETE FROM Usuarios");
            st.execute("DELETE FROM DatosPersonales");
            st.execute("DELETE FROM Roles");

            // Roles básicos
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (1, 'Cliente')");
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (2, 'Emprendedor')");

            // Datos personales base
            st.execute("""
                INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono)
                VALUES (1, 'Test', 'User', '000')
            """);

            // Usuario base válido
            st.execute("""
                INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol)
                VALUES (1, 'test@mail.com', '123', 1, 1)
            """);

            st.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }

        repoReal = new RUsuario(ConexionDB.getConnection());
        servicio = new SUsuario(repoReal);
    }

    // ======================================================
    // TEST 1 - Autenticación válida
    // ======================================================
    @Test
    void testAutenticarUsuarioValido() {
        boolean resultado = servicio.autenticar("test@mail.com", "123");
        assertTrue(resultado);
    }

    // ======================================================
    // TEST 2 - Usuario emprendedor con solicitud PENDIENTE
    // ======================================================
    @Test
    void testAutenticarUsuarioPendiente() throws Exception {

        try (Connection conn = ConexionDB.getConnection();
             Statement st = conn.createStatement()) {

            st.execute("""
                INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono)
                VALUES (2, 'Luis', 'Rojas', '555')
            """);

            st.execute("""
                INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol)
                VALUES (2, 'pendiente@mail.com', '123', 2, 2)
            """);

            st.execute("""
                INSERT INTO Solicitudes(idSolicitud, estado, idProductoAsociado, idSolicitante)
                VALUES (1, 'PENDIENTE', NULL, 2)
            """);
        }

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> servicio.autenticar("pendiente@mail.com", "123"));

        assertEquals(
                "Tu solicitud de registro como emprendedor aún está pendiente de aprobación.",
                ex.getMessage()
        );
    }

    // ======================================================
    // TEST 3 - Obtener nombre
    // ======================================================
    @Test
    void testObtenerNombre() throws Exception {

        try (Connection conn = ConexionDB.getConnection();
             Statement st = conn.createStatement()) {

            st.execute("""
                INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono)
                VALUES (3, 'Daniel', 'Ortiz', '777')
            """);

            st.execute("""
                INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol)
                VALUES (3, 'correo@mail.com', 'xyz', 3, 1)
            """);
        }

        assertEquals("Daniel", servicio.obtenerNombre("correo@mail.com"));
    }

    // ======================================================
    // TEST 4 - Registrar emprendedor
    // ======================================================
    @Test
    void testRegistrarEmprendedor() {

        Usuario u = new Usuario();
        u.setDatosPersonales(new Datos("Juan", "Perez", "111"));
        u.setRol(new Rol(2, "Emprendedor"));

        boolean ok = servicio.registrarUsuario(u, "mensaje de prueba");
        assertTrue(ok);
    }

    // ======================================================
    // TEST 5 - Registrar cliente
    // ======================================================
    @Test
    void testRegistrarCliente() {

        Usuario u = new Usuario();
        u.setDatosPersonales(new Datos("Maria", "Lopez", "222"));
        u.setRol(new Rol(1, "Cliente"));

        boolean ok = servicio.registrarUsuario(u, "");
        assertTrue(ok);
    }
}
