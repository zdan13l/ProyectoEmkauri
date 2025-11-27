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

// Sigue la estructura de tu SCalificacionTest: usa la BD H2 real.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SUsuarioTest {

    private RUsuario repoReal;
    private SUsuario servicio;

    @BeforeAll
    static void iniciarServidorBD() throws Exception {
        // modo pruebas y levantar H2
        ConexionDB.setModoPruebas(true);
        ConexionDB.startTcpAndWebServer();

        try (Connection conn = ConexionDB.getConnection()) {
            // Crea esquema (DDL) y carga datos base si los tienes
            ConexionDB.initSchema(conn);
            ConexionDB.loadTestData(conn);
        }
    }

    @BeforeEach
    void prepararCadaTest() throws Exception {
        try (Connection conn = ConexionDB.getConnection();
             Statement st = conn.createStatement()) {

            // Desactivar integridad para limpiar tablas en cualquier orden
            st.execute("SET REFERENTIAL_INTEGRITY FALSE");

            // Limpiar tablas que afectan usuario/solicitudes
            st.execute("DELETE FROM Solicitudes");
            st.execute("DELETE FROM Usuarios");
            st.execute("DELETE FROM DatosPersonales");
            st.execute("DELETE FROM Roles");

            // Insertar roles con IDs controlados:
            // 1 = Emprendedor  (IMPORTANTE: el repo inserta emprendedor con idRol = 1)
            // 2 = Cliente
            // 3 = Reclutador
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (1, 'Emprendedor')");
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (2, 'Cliente')");
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (3, 'Reclutador')");

            // Crear un reclutador (necesario para crear solicitudes de emprendedor)
            st.execute("INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono) VALUES (1, 'Rec', 'Lutador', '000')");
            st.execute("INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol) VALUES (1, 'reclu@mail.com', '123', 1, 3)");

            // Reactivar integridad
            st.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }

        // inicializar repo y servicio reales
        repoReal = new RUsuario(ConexionDB.getConnection());
        servicio = new SUsuario(repoReal);
    }

    // ---------------------------------------------------------------------
    // TEST 1: registrar cliente por el servicio (usa insertarCliente internamente)
    // ---------------------------------------------------------------------
    @Test
    @Order(1)
    void testRegistrarCliente() {
        Usuario cliente = new Usuario();
        cliente.setCorreo("cliente@test.com");
        cliente.setContrasena("123");
        cliente.setDatosPersonales(new Datos("Juan", "Perez", "555"));
        cliente.setRol(new Rol(2, "Cliente")); // importante: rol presente

        boolean ok = servicio.registrarUsuario(cliente, "");
        assertTrue(ok, "El registro de cliente debe retornar true");

        // adicional: verificar que buscarPorCorreo devuelve no nulo
        Usuario desdeBd = repoReal.buscarPorCorreo("cliente@test.com");
        assertNotNull(desdeBd);
        assertEquals("cliente@test.com", desdeBd.getCorreo());
    }

    // ---------------------------------------------------------------------
    // TEST 2: registrar emprendedor (crea usuario + solicitud pendiente)
    // ---------------------------------------------------------------------
    @Test
    @Order(2)
    void testRegistrarEmprendedor() {
        Usuario empr = new Usuario();
        empr.setCorreo("emp@test.com");
        empr.setContrasena("abc");
        empr.setDatosPersonales(new Datos("Ana", "Lopez", "555"));
        empr.setRol(new Rol(1, "Emprendedor")); // IMPORTANT: nombre coincide con Role en BD

        boolean ok = servicio.registrarUsuario(empr, "Quiero publicar cursos");
        assertTrue(ok, "El registro de emprendedor debe retornar true (y crear solicitud)");

        // verificar que la solicitud fue creada (podemos buscar al usuario)
        Usuario desdeBd = repoReal.buscarPorCorreo("emp@test.com");
        assertNotNull(desdeBd);
        assertEquals("emp@test.com", desdeBd.getCorreo());
    }

    // ---------------------------------------------------------------------
    // TEST 3: intentar autenticar emprendedor pendiente -> servicio debe lanzar mensaje amigable
    // ---------------------------------------------------------------------
    @Test
    @Order(3)
    void testAutenticarUsuarioPendiente() {
        // registramos emprendedor a través del servicio (esto crea la solicitud PENDIENTE)
        Usuario empr = new Usuario();
        empr.setCorreo("pendiente@test.com");
        empr.setContrasena("pwd");
        empr.setDatosPersonales(new Datos("Pend", "Test", "777"));
        empr.setRol(new Rol(1, "Emprendedor"));

        boolean regOk = servicio.registrarUsuario(empr, "pendiente solicitud");
        assertTrue(regOk, "registro emprendedor debe ser OK");

        // ahora intentar autenticación -> debe lanzar RuntimeException con mensaje amigable
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> servicio.autenticar("pendiente@test.com", "pwd"));

        String esperado = "Tu solicitud de registro como emprendedor aún está pendiente de aprobación.";
        assertEquals(esperado, ex.getMessage());
    }
}
