package servicio;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import repositorio.*;
import modelo.*;
import java.sql.*;

// Clase de prueba para el servicio SUsuario utilizando la implementación real del repositorio.
public class SUsuarioTest {
    // Servicios y repositorios reales.
    private RUsuario repoReal;
    private SUsuario servicio;

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
            st.execute("DELETE FROM Solicitudes");
            st.execute("DELETE FROM Usuarios");
            st.execute("DELETE FROM DatosPersonales");
            st.execute("DELETE FROM Roles");

            // Reiniciar contadores de IDs.
            st.execute("ALTER TABLE Roles ALTER COLUMN idRol RESTART WITH 1");
            st.execute("ALTER TABLE Usuarios ALTER COLUMN idUsuario RESTART WITH 1");
            st.execute("ALTER TABLE DatosPersonales ALTER COLUMN idDatos RESTART WITH 1");
            st.execute("ALTER TABLE Solicitudes ALTER COLUMN idSolicitud RESTART WITH 1");

            // Insertar datos mínimos necesarios.
            st.execute("INSERT INTO Roles(nombre) VALUES ('Emprendedor')");
            st.execute("INSERT INTO Roles(nombre) VALUES ('Cliente')");
            st.execute("INSERT INTO Roles(nombre) VALUES ('Reclutador')");
            st.execute("INSERT INTO DatosPersonales(nombre, apellido, telefono) VALUES ('Rec', 'Lutador', '000')");
            st.execute("INSERT INTO Usuarios(correo, contrasena, idDatos, idRol) VALUES ('reclu@mail.com', '123', 1, 3)");

            // Reactivar la integridad referencial.
            st.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
        // Crear instancias reales de repositorio y servicio.
        repoReal = new RUsuario(ConexionDB.getConnection());
        servicio = new SUsuario(repoReal);
    }

    // TEST : registrarCliente()
    @Test
    void testRegistrarCliente() {
        Usuario cliente = new Usuario();
        cliente.setCorreo("cliente@test.com");
        cliente.setContrasena("123");
        cliente.setDatosPersonales(new Datos("Juan", "Perez", "555"));
        cliente.setRol(new Rol(2, "Cliente"));

        Usuario desdeBd = repoReal.buscarPorCorreo("cliente@test.com");
        assertNotNull(desdeBd, "El cliente debe existir después del registro.");
        assertEquals("cliente@test.com", desdeBd.getCorreo());
    }

    // TEST : registrarEmprendedor()
    @Test
    void testRegistrarEmprendedor() {
        Usuario empr = new Usuario();
        empr.setCorreo("emp@test.com");
        empr.setContrasena("abc");
        empr.setDatosPersonales(new Datos("Ana", "Lopez", "555"));
        empr.setRol(new Rol(1, "Emprendedor"));

        Usuario desdeBd = repoReal.buscarPorCorreo("emp@test.com");
        assertNotNull(desdeBd);
        assertEquals("emp@test.com", desdeBd.getCorreo());
    }

    // TEST : autenticarUsuarioPendiente()
    @Test
    void testAutenticarUsuarioPendiente() {
        Usuario empr = new Usuario();
        empr.setCorreo("pendiente@test.com");
        empr.setContrasena("pwd");
        empr.setDatosPersonales(new Datos("Pend", "Test", "777"));
        empr.setRol(new Rol(1, "Emprendedor"));

        boolean regOk = servicio.registrarUsuario(empr, "pendiente solicitud");
        assertTrue(regOk, "El registro emprendedor debe ser correcto.");

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> servicio.autenticar("pendiente@test.com", "pwd")
        );
        assertEquals(
                "Tu solicitud de registro como emprendedor aún está pendiente de aprobación.",
                ex.getMessage()
        );
    }
}
