package servicio;

import modelo.Pago;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repositorio.IRPago;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SPagoTest {

    @Mock
    private IRPago repoP;

    @InjectMocks
    private SPago servicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearPago() {
        Pago pago = new Pago();
        when(repoP.crearPago(pago)).thenReturn(true);

        boolean resultado = servicio.crearPago(pago);

        assertTrue(resultado);
        verify(repoP).crearPago(pago);
    }

    @Test
    void testObtenerPagoPorId() {
        Pago pago = new Pago();
        when(repoP.obtenerPagoPorId(1)).thenReturn(pago);

        Pago resultado = servicio.obtenerPagoPorId(1);

        assertNotNull(resultado);
        verify(repoP).obtenerPagoPorId(1);
    }

    @Test
    void testObtenerTodos() {
        when(repoP.obtenerTodos()).thenReturn(Arrays.asList(new Pago(), new Pago(), new Pago()));

        List<Pago> lista = servicio.obtenerTodos();

        assertEquals(3, lista.size());
        verify(repoP).obtenerTodos();
    }

    @Test
    void testActualizarPago() {
        Pago pago = new Pago();
        when(repoP.actualizarPago(pago)).thenReturn(true);

        boolean actualizado = servicio.actualizarPago(pago);

        assertTrue(actualizado);
        verify(repoP).actualizarPago(pago);
    }

    @Test
    void testEliminarPago() {
        when(repoP.eliminarPago(2)).thenReturn(true);

        boolean eliminado = servicio.eliminarPago(2);

        assertTrue(eliminado);
        verify(repoP).eliminarPago(2);
    }
}
