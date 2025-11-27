package servicio;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import repositorio.*;
import modelo.*;
import java.sql.*;
import java.util.List;

// Clase de prueba para el servicio SCompra utilizando la implementación real del repositorio.
public class SCompraTest {
    // Servicios y repositorios reales.
    private RCompra repoReal;
    private SCompra servicio;

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
            st.execute("DELETE FROM ComprasProductos");
            st.execute("DELETE FROM Compras");
            st.execute("DELETE FROM Pagos");
            st.execute("DELETE FROM Productos");
            st.execute("DELETE FROM Categorias");
            st.execute("DELETE FROM Usuarios");
            st.execute("DELETE FROM DatosPersonales");
            st.execute("DELETE FROM Roles");

            // Insertar datos mínimos necesarios.
            st.execute("INSERT INTO Roles(idRol, nombre) VALUES (1, 'Cliente')");
            st.execute("INSERT INTO DatosPersonales(idDatos, nombre, apellido, telefono)" +
                            " VALUES (1, 'Test', 'Usuario', '000')");
            st.execute("INSERT INTO Usuarios(idUsuario, correo, contrasena, idDatos, idRol)" +
                            " VALUES (1, 'test@mail.com', 'pass', 1, 1)");
            st.execute("INSERT INTO Categorias(idCategoria, nombre, descripcion)" +
                            " VALUES (1, 'General', 'Test')");
            st.execute("INSERT INTO Productos(idProducto, titulo, descripcion, precio," +
                            " idEmprendedor, idCategoria, tipoProducto) " +
                            "VALUES (1, 'Curso', 'Curso Test', 50.0, 1, 1, 'CURSO')");
            st.execute("INSERT INTO Pagos(idPago, metodo, estado, detalle) " +
                            "VALUES (1, 'NEQUI', 'OK', 'prueba')");

            // Reactivar la integridad referencial.
            st.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
        // Inicializar repositorio y servicio reales.
        repoReal = new RCompra(ConexionDB.getConnection());
        servicio = new SCompra(repoReal);
    }

    // TEST : crearCompra()
    @Test
    void testCrearCompraExitosa() {
        Compra compra = new Compra();
        Usuario cliente = new Usuario();
        cliente.setIdUsuario(1);
        compra.setCliente(cliente);

        Pago pago = new Pago();
        pago.setIdPago(1);
        compra.setPago(pago);

        compra.setMontoFinal(50.0);

        Producto producto = new Producto();
        producto.setIdProducto(1);
        producto.setPrecio(50.0);

        compra.setProductos(List.of(producto));

        boolean resultado = servicio.crearCompra(compra);
        assertTrue(resultado);

        // Validar que exista en BD.
        Compra c = servicio.obtenerCompraPorId(1);
        assertNotNull(c);
        assertEquals(1, c.getCliente().getIdUsuario());
        assertEquals(50.0, c.getMontoFinal());
        assertEquals(1, c.getProductos().size());
    }

    // TEST : obtenerCompraPorId()
    @Test
    void testObtenerCompraPorId() throws Exception {
        try (Connection con = ConexionDB.getConnection(); Statement st = con.createStatement()) {
            st.execute("INSERT INTO Compras(idCompra, idCliente, montoFinal, idPago)" +
                            " VALUES (10, 1, 99.9, 1)");
            st.execute("INSERT INTO ComprasProductos(idCompra, idProducto, precioCompra)" +
                            " VALUES (10, 1, 99.9)");
        }
        Compra compra = servicio.obtenerCompraPorId(10);

        assertNotNull(compra);
        assertEquals(99.9, compra.getMontoFinal());
        assertEquals(1, compra.getProductos().size());
    }

    // TEST : listarCompras()
    @Test
    void testListarCompras() throws Exception {
        try (Connection con = ConexionDB.getConnection(); Statement st = con.createStatement()) {
            st.execute("INSERT INTO Compras(idCompra, idCliente, montoFinal, idPago)" +
                            " VALUES (20, 1, 30.0, 1)");
            st.execute("INSERT INTO Compras(idCompra, idCliente, montoFinal, idPago)" +
                            " VALUES (21, 1, 60.0, 1)");
        }
        List<Compra> lista = servicio.listarCompras();
        assertEquals(2, lista.size());
    }

    // TEST: actualizarCompra()
    @Test
    void testActualizarCompra() throws Exception {
        try (Connection con = ConexionDB.getConnection(); Statement st = con.createStatement()) {
            st.execute("INSERT INTO Compras(idCompra, idCliente, montoFinal, idPago)" +
                            " VALUES (30, 1, 40.0, 1)");
        }
        Compra compra = new Compra();
        compra.setIdCompra(30);
        compra.setMontoFinal(55.0);

        Pago pago = new Pago();
        pago.setIdPago(1);
        compra.setPago(pago);

        boolean actualizado = servicio.actualizarCompra(compra);
        assertTrue(actualizado);

        Compra c = servicio.obtenerCompraPorId(30);
        assertEquals(55.0, c.getMontoFinal());
    }

    // TEST: actualizarCompra()
    @Test
    void testEliminarCompra() throws Exception {
        try (Connection con = ConexionDB.getConnection(); Statement st = con.createStatement()) {
            st.execute("INSERT INTO Compras(idCompra, idCliente, montoFinal, idPago)" +
                            " VALUES (40, 1, 20.0, 1)");
        }
        boolean eliminado = servicio.eliminarCompra(40);
        assertTrue(eliminado);
        assertNull(servicio.obtenerCompraPorId(40));
    }
}
