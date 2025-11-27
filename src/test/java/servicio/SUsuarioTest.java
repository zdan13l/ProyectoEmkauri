package servicio;

import org.junit.jupiter.api.*;
import repositorio.ConexionDB;
import repositorio.RUsuario;
import modelo.Usuario;
import modelo.Datos;
import modelo.Rol;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SUsuarioTest {

    private RUsuario repoReal;
    private SUsuario servicio;

    @BeforeAll
    static void iniciarServidorBD() throws Exception {
        ConexionDB.setModoPruebas(true);
        ConexionDB.startTcpAndWebServer();

        try (Connection conn = ConexionDB.getConnection()) {
            ConexionDB.initSchema(conn);
            ConexionDB.loadTestData(conn);
        }
    }

    @BeforeEach
    void prepararCadaTest() throws Exception {
        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement()) {

            st.execute("SET REFERENTIAL_INTEGRITY FALSE");

            st.execute("DELETE FROM Solicitudes");
            st.execute("DELETE FROM Usuarios");
            st.execute("DELETE FROM DatosPersonales");
            st.execute("DELETE FROM Roles");

            // MUY IMPORTANTE: reiniciar IDs
            st.execute("ALTER TABLE Roles ALTER COLUMN idRol RESTART WITH 1");
            st.execute("ALTER TABLE Usuarios ALTER COLUMN idUsuario RESTART WITH 1");
            st.execute("ALTER TABLE DatosPersonales ALTER COLUMN idDatos RESTART WITH 1");
            st.execute("ALTER TABLE Solicitudes ALTER COLUMN idSolicitud RESTART WITH 1");

            // Roles en el orden que repo espera
            st.execute("INSERT INTO Roles(nombre) VALUES ('Emprendedor')"); // idRol = 1
            st.execute("INSERT INTO Roles(nombre) VALUES ('Cliente')");      // idRol = 2
            st.execute("INSERT INTO Roles(nombre) VALUES ('Reclutador')");   // idRol = 3

            // Crear un reclutador necesario para solicitudes de emprendedor
            st.execute("INSERT INTO DatosPersonales(nombre, apellido, telefono) VALUES ('Rec', 'Lutador', '000')");
            st.execute("INSERT INTO Usuarios(correo, contrasena, idDatos, idRol) VALUES ('reclu@mail.com', '123', 1, 3)");

            st.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }

        repoReal = new RUsuario(ConexionDB.getConnection());
        servicio = new SUsuario(repoReal);
    }

    // ===========================================================
    // TEST 1: Registro de Cliente
    // ===========================================================
    @Test
    @Order(1)
    void testRegistrarCliente() {

        Usuario cliente = new Usuario();
        cliente.setCorreo("cliente@test.com");
        cliente.setContrasena("123");
        cliente.setDatosPersonales(new Datos("Juan", "Perez", "555"));
        cliente.setRol(new Rol(2, "Cliente")); // idRol=2

        boolean okServicio = servicio.registrarUsuario(cliente, "");

        if (!okServicio) {
            boolean okRepo = repoReal.insertarCliente(cliente);
            if (!okRepo) {
                Usuario busc = repoReal.buscarPorCorreo("cliente@test.com");
                fail("Registro de cliente falló (servicio=false, repo=false). Usuario en BD? " + (busc != null));
            } else {
                fail("Repo funcionó pero el servicio devolvió false.");
            }
        }

        Usuario desdeBd = repoReal.buscarPorCorreo("cliente@test.com");
        assertNotNull(desdeBd, "El cliente debe existir después del registro.");
        assertEquals("cliente@test.com", desdeBd.getCorreo());
    }

    // ===========================================================
    // TEST 2: Registro de Emprendedor (crea Solicitud)
    // ===========================================================
    @Test
    @Order(2)
    void testRegistrarEmprendedor() {

        Usuario empr = new Usuario();
        empr.setCorreo("emp@test.com");
        empr.setContrasena("abc");
        empr.setDatosPersonales(new Datos("Ana", "Lopez", "555"));
        empr.setRol(new Rol(1, "Emprendedor")); // idRol=1

        boolean okServicio = servicio.registrarUsuario(empr, "Quiero publicar cursos");

        if (!okServicio) {
            boolean okRepo = repoReal.insertarEmprendedor(empr, "Quiero publicar cursos");
            if (!okRepo) {
                Usuario busc = repoReal.buscarPorCorreo("emp@test.com");
                fail("Registro emprendedor falló (servicio=false, repo=false). Usuario en BD? " + (busc != null));
            } else {
                fail("Repo funcionó pero el servicio devolvió false.");
            }
        }

        Usuario desdeBd = repoReal.buscarPorCorreo("emp@test.com");
        assertNotNull(desdeBd);
        assertEquals("emp@test.com", desdeBd.getCorreo());
    }

    // ===========================================================
    // TEST 3: Autenticación de emprendedor con solicitud pendiente
    // ===========================================================
    @Test
    @Order(3)
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
