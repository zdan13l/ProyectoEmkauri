package servicio;

import modelo.Compra;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repositorio.IRCompra;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SCompraTest {

    @Mock
    private IRCompra repoC;

    @InjectMocks
    private SCompra servicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearCompra() {
        Compra compra = new Compra();
        when(repoC.insertar(compra)).thenReturn(true);

        boolean resultado = servicio.crearCompra(compra);

        assertTrue(resultado);
        verify(repoC).insertar(compra);
    }

    @Test
    void testObtenerCompraPorId() {
        Compra compra = new Compra();
        when(repoC.obtenerPorId(10)).thenReturn(compra);

        Compra resultado = servicio.obtenerCompraPorId(10);

        assertNotNull(resultado);
        verify(repoC).obtenerPorId(10);
    }

    @Test
    void testListarCompras() {
        when(repoC.listar()).thenReturn(Arrays.asList(new Compra(), new Compra()));

        List<Compra> lista = servicio.listarCompras();

        assertEquals(2, lista.size());
        verify(repoC).listar();
    }

    @Test
    void testActualizarCompra() {
        Compra compra = new Compra();
        when(repoC.actualizar(compra)).thenReturn(true);

        boolean actualizado = servicio.actualizarCompra(compra);

        assertTrue(actualizado);
        verify(repoC).actualizar(compra);
    }

    @Test
    void testEliminarCompra() {
        when(repoC.eliminar(5)).thenReturn(true);

        boolean eliminado = servicio.eliminarCompra(5);

        assertTrue(eliminado);
        verify(repoC).eliminar(5);
    }
}
