package servicio;

import modelo.Material;

import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Servicio en memoria para CU-004 Materiales de Curso.
 * Permite subir (simulado), listar y eliminar materiales por curso.
 */
public class SMaterial {

    // Singleton simple para reutilizar en controladores
    private static final SMaterial INSTANCE = new SMaterial();
    public static SMaterial getInstance() { return INSTANCE; }

    // idCurso -> lista de materiales
    private final Map<Integer, List<Material>> materialesPorCurso = new HashMap<>();

    private static final Pattern URL_PATTERN = Pattern.compile("^(https?://).+");

    private SMaterial() { }

    public Material subirMaterial(int idCurso, String titulo, String url, String tipo) {
        validar(idCurso, titulo, url);
        Material m = new Material(idCurso, titulo.trim(), url.trim(), tipo);
        materialesPorCurso.computeIfAbsent(idCurso, k -> new ArrayList<>()).add(m);
        return m;
    }

    public List<Material> listarPorCurso(int idCurso) {
        return materialesPorCurso.getOrDefault(idCurso, Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(Material::getFechaSubida).reversed())
                .collect(Collectors.toUnmodifiableList());
    }

    public boolean eliminar(int idCurso, int idMaterial) {
        List<Material> lista = materialesPorCurso.get(idCurso);
        if (lista == null) return false;
        return lista.removeIf(m -> m.getIdMaterial() == idMaterial);
    }

    private void validar(int idCurso, String titulo, String url) {
        if (idCurso <= 0) throw new IllegalArgumentException("idCurso inválido.");
        if (titulo == null || titulo.isBlank()) throw new IllegalArgumentException("El título es obligatorio.");
        if (url == null || url.isBlank()) throw new IllegalArgumentException("La URL es obligatoria.");
        if (!URL_PATTERN.matcher(url.trim()).matches()) throw new IllegalArgumentException("La URL debe iniciar con http:// o https://");
        try { URI.create(url.trim()); } catch (Exception e) { throw new IllegalArgumentException("URL inválida."); }
    }
}