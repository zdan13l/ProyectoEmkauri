package servicio;

import modelo.Curso;
import repositorio.RCurso;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SCurso {

    private final RCurso repo = new RCurso();

    public Curso crear(String nombre, String descripcion, double precio, String estado) {
        validar(nombre, precio);
        String estadoFinal = (estado == null || estado.isBlank()) ? "ACTIVO" : estado.toUpperCase();

        if (repo.existePorNombre(nombre)) {
            throw new IllegalArgumentException("Ya existe un curso con ese nombre.");
        }

        Curso nuevo = new Curso(
            0,                      // id se genera automáticamente
            precio,
            nombre,
            descripcion,
            estadoFinal,
            null,                   // Categoria (rellenar más adelante si se integra)
            null,                   // Emprendedor
            java.util.List.of()     // Calificaciones
        );
        return repo.guardar(nuevo);
    }

    public boolean editar(int idCurso, String nombre, String descripcion, Double precio, String estado) {
        Curso actual = repo.buscarPorId(idCurso)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

        String nombreE  = (nombre == null || nombre.isBlank()) ? actual.getNombre() : nombre;
        String descE    = (descripcion == null) ? actual.getDescripcion() : descripcion;
        double precioE  = (precio == null || precio < 0) ? actual.getPrecio() : precio;
        String estadoE  = (estado == null || estado.isBlank()) ? actual.getEstado() : estado.toUpperCase();

        validar(nombreE, precioE);

        Curso editado = new Curso(
            actual.getIdCurso(),
            precioE,
            nombreE,
            descE,
            estadoE,
            actual.getCategoria(),
            actual.getEmprendedor(),
            actual.getCalificaciones() 
        );
        return repo.actualizar(editado);
    }

    public boolean eliminar(int idCurso) {
        return repo.eliminar(idCurso);
    }

    public List<Curso> listar() {
        return repo.listar();
    }

    private void validar(String nombre, double precio) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del curso es obligatorio.");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
    }
}