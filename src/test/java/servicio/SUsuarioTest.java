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

            // Roles con IDs controlados (coincidir con lo que el repo espera)
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (1,'Emprendedor')");
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (2,'Cliente')");
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (3,'Reclutador')");

            // Crear un reclutador (necesario para insertarEmprendedor)
            st.execute("INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono) VALUES (1,'Rec','Lutador','000')");
            st.execute("INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol) VALUES (1,'reclu@mail.com','123',1,3)");

            st.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }

        repoReal = new RUsuario(ConexionDB.getConnection());
        servicio = new SUsuario(repoReal);
    }

    // TEST 1: registrar cliente (directamente con repo y también usando servicio)
    @Test
    @Order(1)
    void testRegistrarCliente() {
        Usuario cliente = new Usuario();
        cliente.setCorreo("cliente@test.com");
        cliente.setContrasena("123");
        cliente.setDatosPersonales(new Datos("Juan", "Perez", "555"));
        cliente.setRol(new Rol(2, "Cliente"));

        // Llamada por el servicio (usa repo internamente)
        boolean okServicio = servicio.registrarUsuario(cliente, "");
        // Si el servicio devuelve false, aún así verificamos repo directamente para diagnóstico
        if (!okServicio) {
            // intentar insertar por repo directamente para ver si falla igual
            boolean okRepo = repoReal.insertarCliente(cliente);
            if (!okRepo) {
                // buscar en BD si quedó algo para diagnosticar
                Usuario busc = repoReal.buscarPorCorreo("cliente@test.com");
                fail("Registro de cliente falló (servicio=false, repo=false). Usuario en BD? " + (busc != null));
            } else {
                // repo funcionó pero servicio no: problema en SUsuario (p. ej. validación previa)
                fail("Registro por repo funcionó pero servicio.registrarUsuario devolvió false.");
            }
        }

        // si pasó por el servicio, verificamos existencia
        Usuario desdeBd = repoReal.buscarPorCorreo("cliente@test.com");
        assertNotNull(desdeBd, "Después de registrar (servicio), el usuario debe existir en la BD");
        assertEquals("cliente@test.com", desdeBd.getCorreo());
    }

    // TEST 2: registrar emprendedor (crea usuario + solicitud pendiente)
    @Test
    @Order(2)
    void testRegistrarEmprendedor() {
        Usuario empr = new Usuario();
        empr.setCorreo("emp@test.com");
        empr.setContrasena("abc");
        empr.setDatosPersonales(new Datos("Ana", "Lopez", "555"));
        empr.setRol(new Rol(1, "Emprendedor"));

        boolean okServicio = servicio.registrarUsuario(empr, "Quiero publicar cursos");
        if (!okServicio) {
            // intento diagnóstico con el repo directo
            boolean okRepo = repoReal.insertarEmprendedor(empr, "Quiero publicar cursos");
            if (!okRepo) {
                Usuario busc = repoReal.buscarPorCorreo("emp@test.com");
                fail("Registro emprendedor falló (servicio=false, repo=false). Usuario en BD? " + (busc != null));
            } else {
                fail("repo.insertarEmprendedor funcionó pero servicio.registrarUsuario devolvió false.");
            }
        }

        Usuario desdeBd = repoReal.buscarPorCorreo("emp@test.com");
        assertNotNull(desdeBd, "Después de registrar emprendedor, el usuario debe existir en BD");
        assertEquals("emp@test.com", desdeBd.getCorreo());
    }

    // TEST 3: autenticar emprendedor pendiente -> servicio debe lanzar mensaje amigable
    @Test
    @Order(3)
    void testAutenticarUsuarioPendiente() throws Exception {
        // Registramos emprendedor para crear la solicitud pendiente via servicio
        Usuario empr = new Usuario();
        empr.setCorreo("pendiente@test.com");
        empr.setContrasena("pwd");
        empr.setDatosPersonales(new Datos("Pend", "Test", "777"));
        empr.setRol(new Rol(1, "Emprendedor"));

        boolean regOk = servicio.registrarUsuario(empr, "pendiente solicitud");
        assertTrue(regOk, "registro emprendedor debe ser OK (crear usuario + solicitud)");

        // Autenticación debe lanzar la excepción traducida por SUsuario
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> servicio.autenticar("pendiente@test.com", "pwd"));

        assertEquals("Tu solicitud de registro como emprendedor aún está pendiente de aprobación.", ex.getMessage());
    }
}
