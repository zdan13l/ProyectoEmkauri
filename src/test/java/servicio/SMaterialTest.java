package servicio;

import modelo.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repositorio.IRMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FakeRepoMaterial implements IRMaterial {

    private int secuenciaId = 1;

    // Guarda materiales por ID
    private final HashMap<Integer, Material> materiales = new HashMap<>();

    // Relación curso → lista de materiales
    private final HashMap<Integer, List<Material>> materialesPorCurso = new HashMap<>();


    @Override
    public void insertar(Material material, int idCurso) {
        material.setIdMaterial(secuenciaId++);
        materiales.put(material.getIdMaterial(), material);

        materialesPorCurso.putIfAbsent(idCurso, new ArrayList<>());
        materialesPorCurso.get(idCurso).add(material);
    }

    @Override
    public List<Material> listarPorCurso(int idCurso) {
        return materialesPorCurso.getOrDefault(idCurso, new ArrayList<>());
    }

    @Override
    public Material buscarPorId(int idMaterial) {
        return materiales.get(idMaterial);
    }

    @Override
    public void actualizar(Material material) {
        materiales.put(material.getIdMaterial(), material);
    }

    @Override
    public void eliminar(int idMaterial) {
        materiales.remove(idMaterial);

        // También quitarlo de cualquier curso donde estuviera
        materialesPorCurso.values()
                .forEach(lista -> lista.removeIf(m -> m.getIdMaterial() == idMaterial));
    }
}



// -------------------------------------------------------------
//  TESTS DEL SERVICIO SMaterial
// -------------------------------------------------------------
public class SMaterialTest {

    private FakeRepoMaterial repo;
    private SMaterial servicio;

    @BeforeEach
    void setUp() {
        repo = new FakeRepoMaterial();
        servicio = new SMaterial(repo);
    }

    // ---------------- TEST: INSERTAR ----------------
    @Test
    void testInsertarMaterial() throws Exception {
        Material m = new Material();
        m.setTitulo("Guía PDF");
        m.setTipo("Documento");
        m.setUrl("https://ejemplo.com/guia.pdf");

        servicio.insertar(m, 5);

        List<Material> lista = servicio.listarPorCurso(5);

        assertEquals(1, lista.size());
        assertEquals("Guía PDF", lista.get(0).getTitulo());
        assertEquals("Documento", lista.get(0).getTipo());
        assertEquals("https://ejemplo.com/guia.pdf", lista.get(0).getUrl());
        assertTrue(lista.get(0).getIdMaterial() > 0);
    }

    // ---------------- TEST: LISTAR ----------------
    @Test
    void testListarPorCursoVacio() throws Exception {
        List<Material> lista = servicio.listarPorCurso(123);
        assertNotNull(lista);
        assertEquals(0, lista.size());
    }

    // ---------------- TEST: BUSCAR POR ID ----------------
    @Test
    void testBuscarPorId() throws Exception {
        Material m = new Material();
        m.setTitulo("Presentación");
        m.setTipo("Slides");
        m.setUrl("https://ejemplo.com/presentacion.pptx");

        servicio.insertar(m, 2);

        Material encontrado = servicio.buscarPorId(m.getIdMaterial());

        assertNotNull(encontrado);
        assertEquals("Presentación", encontrado.getTitulo());
        assertEquals("Slides", encontrado.getTipo());
        assertEquals("https://ejemplo.com/presentacion.pptx", encontrado.getUrl());
    }

    // ---------------- TEST: MODIFICAR ----------------
    @Test
    void testModificarMaterial() throws Exception {
        Material m = new Material();
        m.setTitulo("Archivo");
        m.setTipo("PDF");
        m.setUrl("https://original.com");

        servicio.insertar(m, 3);

        // Cambiar datos
        m.setTitulo("Archivo Modificado");
        m.setUrl("https://modificado.com");

        servicio.modificar(m);

        Material actualizado = servicio.buscarPorId(m.getIdMaterial());

        assertEquals("Archivo Modificado", actualizado.getTitulo());
        assertEquals("PDF", actualizado.getTipo());
        assertEquals("https://modificado.com", actualizado.getUrl());
    }

    // ---------------- TEST: ELIMINAR ----------------
    @Test
    void testEliminarMaterial() throws Exception {
        Material m = new Material();
        m.setTitulo("Video");
        m.setTipo("MP4");
        m.setUrl("https://video.com");

        servicio.insertar(m, 10);

        int id = m.getIdMaterial();

        servicio.eliminar(id);

        assertNull(servicio.buscarPorId(id));
        assertEquals(0, servicio.listarPorCurso(10).size());
    }
}
