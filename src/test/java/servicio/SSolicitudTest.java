package servicio;

import modelo.*;
import org.junit.jupiter.api.*;
import repositorio.*;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SSolicitudTest {

    private RSolicitud repoReal;
    private SSolicitud servicio;

    // ================================
    //      INICIO DEL SERVIDOR H2
    // ================================
    @BeforeAll
    static void iniciarServidorBD() throws Exception {
        ConexionDB.setModoPruebas(true);
        ConexionDB.startTcpAndWebServer();

        try (Connection conexion = ConexionDB.getConnection()) {
            ConexionDB.initSchema(conexion);
            ConexionDB.loadTestData(conexion);
        }
    }

    // ==============================================
    //     LIMPIAR TABLAS + INSERTAR DATOS BASE
    // ==============================================
    @BeforeEach
    void prepararCadaTest() throws Exception {
        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement()) {

            st.execute("SET REFERENTIAL_INTEGRITY FALSE");

            st.execute("DELETE FROM Solicitudes");
            st.execute("DELETE FROM Productos");
            st.execute("DELETE FROM Categorias");
            st.execute("DELETE FROM Usuarios");
            st.execute("DELETE FROM DatosPersonales");
            st.execute("DELETE FROM Roles");

            // Insertar roles mínimos
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (1,'Cliente')");
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (2,'Reclutador')");
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (3,'Emprendedor')");

            // Datos personales
            st.execute("""
                INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono)
                VALUES (1,'Test','User','000'),
                       (2,'Reclu','Default','111'),
                       (3,'Empre','Uno','222')
            """);

            // Usuarios
            st.execute("""
                INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol)
                VALUES (1,'cliente@test','x',1,1),
                       (2,'reclutador@test','x',2,2),
                       (3,'emprendedor@test','x',3,3)
            """);

            // Categoría y producto asociado
            st.execute("INSERT INTO Categorias(idCategoria, nombre, descripcion) VALUES (1,'Cat','Desc')");
            st.execute("""
                INSERT INTO Productos(idProducto,titulo,descripcion,precio,idEmprendedor,idCategoria,tipoProducto)
                VALUES (1,'Prod Test','Desc',10.0,3,1,'CURSO')
            """);

            st.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }

        repoReal = new RSolicitud(ConexionDB.getConnection());
        servicio = new SSolicitud(repoReal);
    }

    // =====================================
    //          TEST CREAR SOLICITUD
    // =====================================
    @Test
    void testCrearSolicitud() throws Exception {

        Solicitud s = new Solicitud();
        s.setEstado("X"); // será sobrescrito
        s.setMensaje("Hola");

        Usuario solicitante = new Usuario();
        solicitante.setIdUsuario(1);
        s.setSolicitante(solicitante);

        Usuario emprendedor = new Usuario();
        emprendedor.setIdUsuario(3);
        s.setEmprendedor(emprendedor);

        Producto p = new Producto();
        p.setIdProducto(1);
        s.setProductoAsociado(p);

        servicio.crearSolicitud(s);

        assertEquals("PENDIENTE", s.getEstado());

        try (Connection con = ConexionDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Solicitudes")) {

            rs.next();
            assertEquals(1, rs.getInt(1));
        }
    }

    // =====================================
    //     TEST LISTAR PENDIENTES
    // =====================================
    @Test
    void testListarSolicitudesPendientes() throws Exception {

        try (Connection con = ConexionDB.getConnection(); Statement st = con.createStatement()) {
            st.execute("""
                INSERT INTO Solicitudes(idSolicitud,idSolicitante,idReclutador,estado,idEmprendedorAsociado)
                VALUES (1,1,2,'PENDIENTE',3),
                       (2,1,2,'PENDIENTE',3)
            """);
        }

        List<Solicitud> lista = servicio.listarSolicitudesPendientes("EMPRENDEDOR");

        assertEquals(2, lista.size());
    }

    // =====================================
    //          APROBAR SOLICITUD
    // =====================================
    @Test
    void testAprobarSolicitud() throws Exception {

        try (Connection con = ConexionDB.getConnection(); Statement st = con.createStatement()) {
            st.execute("""
                INSERT INTO Solicitudes(idSolicitud,idSolicitante,idReclutador,estado,idEmprendedorAsociado)
                VALUES (5,1,2,'PENDIENTE',3)
            """);
        }

        boolean ok = servicio.aprobarSolicitud(5);
        assertTrue(ok);

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT estado FROM Solicitudes WHERE idSolicitud=5")) {

            ResultSet rs = ps.executeQuery();
            rs.next();
            assertEquals("APROBADO", rs.getString("estado"));
        }
    }

    // =====================================
    //          RECHAZAR SOLICITUD
    // =====================================
    @Test
    void testRechazarSolicitud() throws Exception {

        try (Connection con = ConexionDB.getConnection(); Statement st = con.createStatement()) {
            st.execute("""
                INSERT INTO Solicitudes(idSolicitud,idSolicitante,idReclutador,estado,idEmprendedorAsociado)
                VALUES (7,1,2,'PENDIENTE',3)
            """);
        }

        boolean ok = servicio.rechazarSolicitud(7);
        assertTrue(ok);

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT estado FROM Solicitudes WHERE idSolicitud=7")) {

            ResultSet rs = ps.executeQuery();
            rs.next();
            assertEquals("RECHAZADO", rs.getString("estado"));
        }
    }
}
