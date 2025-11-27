package servicio;

import org.junit.jupiter.api.*;
import repositorio.ConexionDB;
import repositorio.RUsuario;
import modelo.Usuario;
import modelo.Datos;

import java.sql.Connection;
import java.sql.Statement;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SUsuarioTest {

    private RUsuario repoReal;
    private SUsuario servicio;

    // -------------------------------------------------------------------------
    // INICIO DEL SERVIDOR H2 Y CREACIÓN DEL ESQUEMA
    // -------------------------------------------------------------------------
    @BeforeAll
    static void iniciarServidorBD() throws Exception {
        ConexionDB.setModoPruebas(true);
        ConexionDB.startTcpAndWebServer();

        try (Connection conn = ConexionDB.getConnection()) {
            ConexionDB.initSchema(conn);     // DDL completo
            ConexionDB.loadTestData(conn);   // DATA si existe
        }
    }

    // -------------------------------------------------------------------------
    // REINICIO DE TABLAS ANTES DE CADA TEST
    // -------------------------------------------------------------------------
    @BeforeEach
    void prepararCadaTest() throws Exception {
        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement()) {

            st.execute("SET REFERENTIAL_INTEGRITY FALSE");

            st.execute("DELETE FROM Solicitudes");
            st.execute("DELETE FROM Usuarios");
            st.execute("DELETE FROM DatosPersonales");
            st.execute("DELETE FROM Roles");

            // Crear roles mínimos
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (1,'Emprendedor')");
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (2,'Cliente')");
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (3,'Reclutador')");

            // Crear reclutador obligatorio para solicitudes
            st.execute("INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono) VALUES (1,'Rec','Lutador','000')");
            st.execute("INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol) VALUES (1,'reclu@mail.com','123',1,3)");

            st.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }

        repoReal = new RUsuario(ConexionDB.getConnection());
        servicio = new SUsuario(repoReal);
    }

    // -------------------------------------------------------------------------
    // TEST 1: REGISTRO CLIENTE
    // -------------------------------------------------------------------------
    @Test
    @Order(1)
    void testRegistrarCliente() {
        Usuario cliente = new Usuario(
                0,
                "cliente@test.com",
                "123",
                new Datos("Juan","Perez","555"),
                null
        );

        boolean ok = repoReal.insertarCliente(cliente);

        Assertions.assertTrue(ok);
        System.out.println("OK → Cliente registrado correctamente.");
    }

    // -------------------------------------------------------------------------
    // TEST 2: REGISTRO EMPRENDEDOR + CREACIÓN SOLICITUD
    // -------------------------------------------------------------------------
    @Test
    @Order(2)
    void testRegistrarEmprendedor() {
        Usuario emp = new Usuario(
                0,
                "emp@test.com",
                "abc",
                new Datos("Ana","Lopez","555"),
                null
        );

        boolean ok = repoReal.insertarEmprendedor(emp, "Deseo ser aceptado");

        Assertions.assertTrue(ok);
        System.out.println("OK → Emprendedor registrado y solicitud creada.");
    }

    // -------------------------------------------------------------------------
    // TEST 3: AUTENTICAR EMPRENDEDOR PENDIENTE → DEBE LANZAR EXCEPCIÓN "PENDIENTE"
    // -------------------------------------------------------------------------
    @Test
    @Order(3)
    void testAutenticarUsuarioPendiente() throws Exception {

        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement()) {

            // Crear emprendedor pendiente
            st.execute("INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono) VALUES (10,'Pend','Test','111')");
            st.execute("INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol) VALUES (10,'pend@mail.com','123',10,1)");

            // Crear solicitud en PENDIENTE
            st.execute("INSERT INTO Solicitudes(idSolicitud,idSolicitante,idReclutador,estado,idEmprendedorAsociado) " +
                    "VALUES (1,10,1,'PENDIENTE',10)");
        }

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> repoReal.autenticar("pend@mail.com","123")
        );

        Assertions.assertEquals("PENDIENTE", ex.getMessage());
        System.out.println("OK → Autenticación de emprendedor PENDIENTE correctamente bloqueada.");
    }

}
