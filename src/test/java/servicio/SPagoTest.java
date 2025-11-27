package servicio;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import repositorio.*;
import modelo.*;
import java.sql.*;
import java.util.List;
import java.util.Date;

// Clase de prueba para el servicio SPago utilizando la implementación real del repositorio.
public class SPagoTest {
    // Servicios y repositorios reales.
    private RPago repoReal;
    private SPago servicio;

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
            st.execute("DELETE FROM Calificaciones");
            st.execute("DELETE FROM ProgresoMateriales");
            st.execute("DELETE FROM Materiales");
            st.execute("DELETE FROM ComprasProductos");
            st.execute("DELETE FROM Compras");
            st.execute("DELETE FROM Pagos");
            st.execute("DELETE FROM Productos");
            st.execute("DELETE FROM Categorias");
            st.execute("DELETE FROM Usuarios");
            st.execute("DELETE FROM DatosPersonales");
            st.execute("DELETE FROM Roles");

            // Datos mínimos obligatorios para permitir Compras y Pagos si se usan.
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (1, 'Cliente')");
            st.execute("INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono) VALUES (1,'Test','User','000')");
            st.execute("INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol) " +
                            "VALUES (1,'test@mail.com','123',1,1)");
            st.execute("INSERT INTO Categorias(idCategoria, nombre, descripcion) VALUES (1, 'General', 'Categoria de prueba')");
            st.execute("INSERT INTO Productos(idProducto, titulo, descripcion, precio, idEmprendedor, idCategoria, tipoProducto) " +
                            "VALUES (1, 'Producto Test', 'Descripción', 0.0, 1, 1, 'CURSO')");

            // Reactivar la integridad referencial.
            st.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
        // Inicializar repositorio y servicio reales.
        repoReal = new RPago(ConexionDB.getConnection());
        servicio = new SPago(repoReal);
    }

    // TEST : crearPago()
    @Test
    void testCrearPago() {
        Pago pago = new Pago();
        pago.setMonto(50.0);
        pago.setMetodo("NEQUI");
        pago.setFecha(new Date());

        boolean creado = servicio.crearPago(pago);
        assertTrue(creado);
        assertTrue(pago.getIdPago() > 0);

        Pago guardado = servicio.obtenerPagoPorId(pago.getIdPago());
        assertNotNull(guardado);
        assertEquals(50.0, guardado.getMonto());
    }

    // TEST : obtenerPagoPorId()
    @Test
    void testObtenerPagoPorId() {
        Pago pago = new Pago();
        pago.setMonto(99.9);
        pago.setMetodo("PSE");
        pago.setFecha(new Date());
        servicio.crearPago(pago);

        Pago encontrado = servicio.obtenerPagoPorId(pago.getIdPago());
        assertNotNull(encontrado);
        assertEquals("PSE", encontrado.getMetodo());
        assertEquals(99.9, encontrado.getMonto());
    }

    // TEST : obtenerTodos()
    @Test
    void testObtenerTodos() {
        Pago p1 = new Pago();
        p1.setMonto(10.0); p1.setMetodo("Efectivo"); p1.setFecha(new Date());
        Pago p2 = new Pago();
        p2.setMonto(20.0); p2.setMetodo("Tarjeta"); p2.setFecha(new Date());
        servicio.crearPago(p1);
        servicio.crearPago(p2);

        List<Pago> lista = servicio.obtenerTodos();
        assertEquals(2, lista.size());
    }

    // TEST : actualizarPago()
    @Test
    void testActualizarPago() {
        Pago pago = new Pago();
        pago.setMonto(15.0);
        pago.setMetodo("Efectivo");
        pago.setFecha(new Date());
        servicio.crearPago(pago);

        // Actualización
        pago.setMonto(30.0);
        pago.setMetodo("Crédito");

        boolean actualizado = servicio.actualizarPago(pago);
        assertTrue(actualizado);

        Pago actualizadoBD = servicio.obtenerPagoPorId(pago.getIdPago());
        assertEquals(30.0, actualizadoBD.getMonto());
        assertEquals("Crédito", actualizadoBD.getMetodo());
    }

    // TEST : eliminarPago()
    @Test
    void testEliminarPago() {
        Pago pago = new Pago();
        pago.setMonto(12.0);
        pago.setMetodo("NEQUI");
        pago.setFecha(new Date());
        servicio.crearPago(pago);

        int id = pago.getIdPago();
        boolean eliminado = servicio.eliminarPago(id);
        assertTrue(eliminado);

        assertNull(servicio.obtenerPagoPorId(id));
        assertEquals(0, servicio.obtenerTodos().size());
    }
}
