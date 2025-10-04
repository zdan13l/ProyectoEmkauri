package servicio;

import modelo.Servicio;
import repositorio.RServicio;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class SServicio {

    private final RServicio repo;

    public SServicio() {
        this.repo = new RServicio();
    }

    // Para inyección (tests)
    public SServicio(RServicio repo) {
        this.repo = Objects.requireNonNull(repo);
    }

    // Crear servicio con validaciones básicas
    public Servicio crear(Servicio s) throws SQLException {
        validar(s, false);
        return repo.crear(s);
    }

    public Servicio obtener(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("id inválido");
        return repo.buscarPorId(id);
    }

    public List<Servicio> listar() throws SQLException {
        return repo.listar();
    }

    public boolean actualizar(Servicio s) throws SQLException {
        if (s == null || s.getIdServicio() <= 0)
            throw new IllegalArgumentException("id inválido");
        validar(s, true);
        return repo.actualizar(s);
    }

    public boolean eliminar(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("id inválido");
        return repo.eliminar(id);
    }

    // ---------- VALIDACIONES ----------
    private void validar(Servicio s, boolean esUpdate) {
        if (s == null) throw new IllegalArgumentException("Servicio requerido");

        String nombre = s.getNombre();
        String desc = s.getDescripcion();
        String estado = s.getEstado();
        double precio = s.getPrecio();

        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre del servicio es obligatorio.");
        if (nombre.length() > 100)
            throw new IllegalArgumentException("El nombre no puede tener más de 100 caracteres.");
        if (desc != null && desc.length() > 500)
            throw new IllegalArgumentException("La descripción no puede tener más de 500 caracteres.");
        if (precio < 0)
            throw new IllegalArgumentException("El precio no puede ser negativo.");

        if (estado == null || estado.isBlank()) estado = "PENDIENTE";
        switch (estado.toUpperCase()) {
            case "ACTIVO":
            case "INACTIVO":
            case "PENDIENTE":
                break;
            default:
                throw new IllegalArgumentException("Estado inválido. Usa ACTIVO, INACTIVO o PENDIENTE.");
        }
    }
}
