package servicio;

import modelo.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repositorio.IRCategoria;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SCategoriaTest {

    @Mock
    private IRCategoria repoC;

    @InjectMocks
    private SCategoria servicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearCategoria() {
        Categoria c = new Categoria();
        when(repoC.agregar(c)).thenReturn(true);

        boolean resultado = servicio.crearCategoria(c);

        assertTrue(resultado);
        verify(repoC).agregar(c);
    }

    @Test
    void testListarCategorias() {
        Categoria c1 = new Categoria();
        Categoria c2 = new Categoria();
        when(repoC.listarTodas()).thenReturn(Arrays.asList(c1, c2));

        List<Categoria> lista = servicio.listarCategorias();

        assertEquals(2, lista.size());
        verify(repoC).listarTodas();
    }

    @Test
    void testEliminarCategoria() {
        when(repoC.eliminar(1)).thenReturn(true);

        boolean eliminado = servicio.eliminarCategoria(1);

        assertTrue(eliminado);
        verify(repoC).eliminar(1);
    }
}
