    package servicio;

    import modelo.Calificacion;
    import modelo.Usuario;
    import modelo.Producto;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.MockitoAnnotations;
    import repositorio.IRCalificacion;

    import java.util.Arrays;
    import java.util.List;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.Mockito.*;

    /**
     * Pruebas unitarias para el servicio SCalificacion.
     */
    public class SCalificacionTest {

        @Mock
        private IRCalificacion repoCal;

        @InjectMocks
        private SCalificacion servicio;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
        }

        // -----------------------------------------------------------
        // TEST: crearCalificacion()
        // -----------------------------------------------------------
        @Test
        void testCrearCalificacionExitosa() {
            Calificacion cal = new Calificacion();
            cal.setCliente(new Usuario());
            cal.setProducto(new Producto());
            cal.setPuntaje(5);
            cal.setComentario("Excelente servicio");

            when(repoCal.guardar(cal)).thenReturn(true);

            boolean resultado = servicio.crearCalificacion(cal);

            assertTrue(resultado);
            verify(repoCal).guardar(cal);
        }

        @Test
        void testCrearCalificacionInvalidaSinCliente() {
            Calificacion cal = new Calificacion();
            cal.setProducto(new Producto());
            cal.setPuntaje(4);

            boolean resultado = servicio.crearCalificacion(cal);

            assertFalse(resultado);
            verify(repoCal, never()).guardar(any());
        }

        @Test
        void testCrearCalificacionInvalidaPuntajeFueraDeRango() {
            Calificacion cal = new Calificacion();
            cal.setCliente(new Usuario());
            cal.setProducto(new Producto());
            cal.setPuntaje(10); // fuera de rango

            boolean resultado = servicio.crearCalificacion(cal);

            assertFalse(resultado);
            verify(repoCal, never()).guardar(any());
        }

        // -----------------------------------------------------------
        // TEST: listarPorProducto()
        // -----------------------------------------------------------
        @Test
        void testListarPorProducto() {
            Calificacion c1 = new Calificacion();
            Calificacion c2 = new Calificacion();
            when(repoCal.obtenerPorProducto(1)).thenReturn(Arrays.asList(c1, c2));

            List<Calificacion> lista = servicio.listarPorProducto(1);

            assertEquals(2, lista.size());
            verify(repoCal).obtenerPorProducto(1);
        }

        @Test
        void testListarPorProductoIdInvalido() {
            List<Calificacion> lista = servicio.listarPorProducto(0);

            assertTrue(lista.isEmpty());
            verify(repoCal, never()).obtenerPorProducto(anyInt());
        }

        // -----------------------------------------------------------
        // TEST: listarPorCliente()
        // -----------------------------------------------------------
        @Test
        void testListarPorCliente() {
            Calificacion c1 = new Calificacion();
            when(repoCal.obtenerPorCliente(5)).thenReturn(List.of(c1));

            List<Calificacion> lista = servicio.listarPorCliente(5);

            assertEquals(1, lista.size());
            verify(repoCal).obtenerPorCliente(5);
        }

        @Test
        void testListarPorClienteIdInvalido() {
            List<Calificacion> lista = servicio.listarPorCliente(-1);

            assertTrue(lista.isEmpty());
            verify(repoCal, never()).obtenerPorCliente(anyInt());
        }
    }
