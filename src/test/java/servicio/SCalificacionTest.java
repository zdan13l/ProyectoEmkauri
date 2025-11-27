package servicio;

import modelo.*;
import java.sql.*;
import java.util.List;
import org.junit.jupiter.api.*;
import repositorio.RCalificacion;
import static org.junit.jupiter.api.Assertions.*;

// Clase de prueba para el servicio SCalificacion utilizando la implementación real del repositorio.
public class SCalificacionTest {
    // Servicios y repositorios reales.
    private RCalificacion repoReal;
    private SCalificacion servicio;

    @BeforeEach
    void setUp() throws Exception {
        repoReal = new RCalificacion();
        servicio = new SCalificacion(repoReal);

        // Limpiar y preparar la base de datos de pruebas.
        try (Connection con = repositorio.ConexionDB.getConnection(); Statement st = con.createStatement()) {
            st.execute("DELETE FROM Calificaciones");
            st.execute("DELETE FROM Productos");
            st.execute("DELETE FROM Usuarios");

            // Insertar usuario de prueba.
            st.execute("INSERT INTO Usuarios(idUsuario, nombre, correo) VALUES (1, 'Test', 'test@test.com')");

            // Insertar producto de prueba.
            st.execute("INSERT INTO Productos(idProducto, titulo, precio) VALUES (1, 'Prod Test', 10000)");
        }
    }

    // TEST: crearCalificacion()
    @Test
    void testCrearCalificacionExitosa() {
        Calificacion calificacion = new Calificacion();

        Usuario cliente = new Usuario();
        cliente.setIdUsuario(1);
        calificacion.setCliente(cliente);

        Producto prod = new Producto();
        prod.setIdProducto(1);
        calificacion.setProducto(prod);
        calificacion.setPuntaje(5);
        calificacion.setComentario("Excelente servicio");

        boolean resultado = servicio.crearCalificacion(calificacion);
        assertTrue(resultado);

        // Verificar que la calificación se guardó correctamente.
        List<Calificacion> lista = servicio.listarPorProducto(1);
        assertEquals(1, lista.size());
        assertEquals(5, lista.get(0).getPuntaje());
    }

    // Calificación sin cliente.
    @Test
    void testCrearCalificacionInvalidaSinCliente() {
        Calificacion calificacion = new Calificacion();
        calificacion.setProducto(new Producto());
        calificacion.setPuntaje(4);

        boolean resultado = servicio.crearCalificacion(calificacion);
        assertFalse(resultado);
        assertTrue(servicio.listarPorProducto(1).isEmpty());
    }

    // Puntaje fuera de rango (menor que 1 o mayor que 5).
    @Test
    void testCrearCalificacionInvalidaPuntajeFueraDeRango() {
        Calificacion calificacion = new Calificacion();
        calificacion.setCliente(new Usuario());
        calificacion.setProducto(new Producto());
        calificacion.setPuntaje(10); // Inválido

        boolean resultado = servicio.crearCalificacion(calificacion);
        assertFalse(resultado);
    }

    // TEST: listarPorProducto()
    @Test
    void testListarPorProducto() throws Exception {
        // Insertar manualmente calificación real.
        try (Connection con = repositorio.ConexionDB.getConnection();
             Statement st = con.createStatement()) {
            st.execute("INSERT INTO Calificaciones(idCalificacion, idUsuario, idProducto, puntaje, comentario) " +
                            "VALUES (10, 1, 1, 4, 'Muy bien')");
        }

        List<Calificacion> lista = servicio.listarPorProducto(1);
        assertEquals(1, lista.size());
        assertEquals(4, lista.get(0).getPuntaje());
    }

    // ID de producto inválido.
    @Test
    void testListarPorProductoIdInvalido() {
        List<Calificacion> lista = servicio.listarPorProducto(0);
        assertTrue(lista.isEmpty());
    }

    // TEST: listarPorCliente()
    @Test
    void testListarPorCliente() throws Exception {

        try (Connection con = repositorio.ConexionDB.getConnection(); Statement st = con.createStatement()) {
            st.execute(
                    "INSERT INTO Calificaciones(idCalificacion, idUsuario, idProducto, puntaje, comentario) " +
                        "VALUES (15, 1, 1, 3, 'ok')");
        }

        List<Calificacion> lista = servicio.listarPorCliente(1);
        assertEquals(1, lista.size());
        assertEquals(3, lista.get(0).getPuntaje());
    }

    // ID de cliente inválido.
    @Test
    void testListarPorClienteIdInvalido() {
        List<Calificacion> lista = servicio.listarPorCliente(-1);
        assertTrue(lista.isEmpty());
    }
}
