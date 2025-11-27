package servicio;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import repositorio.*;
import modelo.*;

import java.sql.*;

// Pruebas reales del servicio SUsuario usando repositorio y BD real.
public class SUsuarioTest {

    private RUsuario repoReal;
    private SUsuario servicio;

    // ---------------------------------------------------------------------
    // INICIAR SERVIDOR H2 + CARGAR DDL Y DATA
    // ---------------------------------------------------------------------
    @BeforeAll
    static void iniciarServidorBD() throws Exception {

        ConexionDB.setModoPruebas(true);
        ConexionDB.startTcpAndWebServer();

        try (Connection conn = ConexionDB.getConnection()) {
            ConexionDB.initSchema(conn);
            ConexionDB.loadTestData(conn);
        }
    }

    // ---------------------------------------------------------------------
    // LIMPIAR BD Y REINSERTAR DATOS MÍNIMOS
    // ---------------------------------------------------------------------
    @BeforeEach
    void prepararCadaTest() throws Exception {

        try (Connection conn = ConexionDB.getConnection();
             Statement st = conn.createStatement()) {

            st.execute("SET REFERENTIAL_INTEGRITY FALSE");

            st.execute("DELETE FROM Calificaciones");
            st.execute("DELETE FROM ProgresoMateriales");
            st.execute("DELETE FROM Materiales");
            st.execute("DELETE FROM ComprasProductos");
            st.execute("DELETE FROM Compras");
            st.execute("DELETE FROM Pagos");
            st.execute("DELETE FROM Productos");
            st.execute("DELETE FROM Categorias");
            st.execute("DELETE FROM Solicitudes");
            st.execute("DELETE FROM Usuarios");
            st.execute("DELETE FROM DatosPersonales");
            st.execute("DELETE FROM Roles");

            // -------------------------------------------------------------
            // INSERTAR DATOS MÍNIMOS
            // -------------------------------------------------------------
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES " +
                    "(1,'Emprendedor'), (2,'Cliente'), (3,'Reclutador')");

            // Datos personales básicos
            st.execute("INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono) " +
                    "VALUES (1,'Reclu','Prueba','111')");

            // Usuario reclutador REQUERIDO PARA aceptar emprendedores
            st.execute("INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol) " +
                    "VALUES (1,'reclu@mail.com','123',1,3)");

            st.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }

        repoReal = new RUsuario(ConexionDB.getConnection());
        servicio = new SUsuario(repoReal);
    }

    // ---------------------------------------------------------------------
    // TEST: autenticar usuario válido
    // ---------------------------------------------------------------------
    @Test
    void testAutenticarUsuarioValido() throws Exception {

        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement()) {

            st.execute("INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono) " +
                    "VALUES (2,'Cliente','Test','000')");

            st.execute("INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol) " +
                    "VALUES (2,'cliente@mail.com','abc',2,2)");
        }

        boolean ok = servicio.autenticar("cliente@mail.com", "abc");
        assertTrue(ok);
    }

    // ---------------------------------------------------------------------
    // TEST: autenticar usuario emprendedor pendiente
    // ---------------------------------------------------------------------
    @Test
    void testAutenticarUsuarioPendiente() throws Exception {

        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement()) {

            st.execute("INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono) " +
                    "VALUES (3,'Pend','User','000')");

            st.execute("INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol, estado) " +
                    "VALUES (3,'pend@mail.com','123',3,1,'PENDIENTE')");
        }

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> servicio.autenticar("pend@mail.com", "123")
        );

        assertEquals("Tu solicitud de registro como emprendedor aún está pendiente de aprobación.", ex.getMessage());
    }

    // ---------------------------------------------------------------------
    // TEST: obtenerNombre()
    // ---------------------------------------------------------------------
    @Test
    void testObtenerNombre() throws Exception {

        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement()) {

            st.execute("INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono) " +
                    "VALUES (4,'Daniel','Ortiz','555')");

            st.execute("INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol) " +
                    "VALUES (4,'correo@mail.com','123',4,2)");
        }

        String nombre = servicio.obtenerNombre("correo@mail.com");
        assertEquals("Daniel", nombre);
    }

    // ---------------------------------------------------------------------
    // TEST: registrarUsuario emprendedor
    // ---------------------------------------------------------------------
    @Test
    void testRegistrarEmprendedor() {

        Usuario u = new Usuario();

        Rol r = new Rol();
        r.setNombre("Emprendedor");
        u.setRol(r);

        Datos d = new Datos();
        d.setNombre("Nuevo");
        d.setApellido("Emp");
        d.setTelefono("000");
        u.setDatosPersonales(d);

        u.setCorreo("nuevo@emp.com");
        u.setContrasena("123");

        boolean ok = servicio.registrarUsuario(u, "mensaje de prueba");
        assertTrue(ok);
    }

    // ---------------------------------------------------------------------
    // TEST: registrarUsuario cliente
    // ---------------------------------------------------------------------
    @Test
    void testRegistrarCliente() {

        Usuario u = new Usuario();

        Rol r = new Rol();
        r.setNombre("Cliente");
        u.setRol(r);

        Datos d = new Datos();
        d.setNombre("Nuevo");
        d.setApellido("Cliente");
        d.setTelefono("000");
        u.setDatosPersonales(d);

        u.setCorreo("cliente@ok.com");
        u.setContrasena("123");

        boolean ok = servicio.registrarUsuario(u, "");
        assertTrue(ok);
    }
}
