package servicio;

import modelo.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repositorio.IRProducto;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SProductoTest {

    @Mock
    private IRProducto repoP;

    @InjectMocks
    private SProducto servicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearProducto() {
        Producto producto = new Producto();
        when(repoP.agregar(producto)).thenReturn(true);

        boolean resultado = servicio.crearProducto(producto);

        assertTrue(resultado);
        verify(repoP).agregar(producto);
    }

    @Test
    void testObtenerProductoPorId() {
        Producto producto = new Producto();
        when(repoP.buscarPorId(10)).thenReturn(producto);

        Producto resultado = servicio.obtenerProductoPorId(10);

        assertNotNull(resultado);
        verify(repoP).buscarPorId(10);
    }

    @Test
    void testBuscarPorNombre() {
        when(repoP.buscarPorTitulo("Curso Java"))
                .thenReturn(Arrays.asList(new Producto(), new Producto()));

        List<Producto> lista = servicio.buscarPorNombre("Curso Java");

        assertEquals(2, lista.size());
        verify(repoP).buscarPorTitulo("Curso Java");
    }

    @Test
    void testListarProductos() {
        when(repoP.listarTodos()).thenReturn(Arrays.asList(new Producto(), new Producto(), new Producto()));

        List<Producto> lista = servicio.listarProductos();

        assertEquals(3, lista.size());
        verify(repoP).listarTodos();
    }

    @Test
    void testEliminarProducto() {
        when(repoP.eliminar(7)).thenReturn(true);

        boolean eliminado = servicio.eliminarProducto(7);

        assertTrue(eliminado);
        verify(repoP).eliminar(7);
    }
}
