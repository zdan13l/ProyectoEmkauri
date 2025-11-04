package servicio;

import modelo.Solicitud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repositorio.IRSolicitud;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SSolicitudTest {

    @Mock
    private IRSolicitud repoS;

    @InjectMocks
    private SSolicitud servicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearSolicitud() {
        Solicitud solicitud = new Solicitud();

        // no devuelve nada, pero debe establecer estado y guardar
        servicio.crearSolicitud(solicitud);

        assertEquals("PENDIENTE", solicitud.getEstado());
        verify(repoS).guardar(solicitud);
    }

    @Test
    void testListarSolicitudesPendientes() {
        when(repoS.listarPendientesTipo("EMPRENDEDOR"))
                .thenReturn(Arrays.asList(new Solicitud(), new Solicitud()));

        List<Solicitud> lista = servicio.listarSolicitudesPendientes("EMPRENDEDOR");

        assertEquals(2, lista.size());
        verify(repoS).listarPendientesTipo("EMPRENDEDOR");
    }

    @Test
    void testAprobarSolicitud() {
        when(repoS.aprobar(1)).thenReturn(true);

        boolean resultado = servicio.aprobarSolicitud(1);

        assertTrue(resultado);
        verify(repoS).aprobar(1);
    }

    @Test
    void testRechazarSolicitud() {
        when(repoS.rechazar(1)).thenReturn(true);

        boolean resultado = servicio.rechazarSolicitud(1);

        assertTrue(resultado);
        verify(repoS).rechazar(1);
    }
}
