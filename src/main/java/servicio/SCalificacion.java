package servicio;

import modelo.Calificacion;
import modelo.Cliente;
import modelo.Curso;
import repositorio.RCalificacion;

import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

// Servicio para manejar lógica de Calificación.
public class SCalificacion {
    private final List<Calificacion> calificaciones = new ArrayList<>();
    private final RCalificacion repoC = new RCalificacion();

    // Registrar calificación en BD y en la lista
    public void registrarCalificacion(int idCalificacion, String comentario, int nota, Date fecha, Cliente cliente, Curso curso) {
        int idCliente = 0;
        int idCurso = 0;

        if (cliente != null) {
            idCliente = cliente.getIdUsuario();
        }

        if (curso != null) {
            idCurso = curso.getIdCurso();
        }

        boolean creada = repoC.crearCalificacion(idCalificacion, nota, comentario, fecha.toString(), idCliente, idCurso);
        if (creada) {
            calificaciones.add(new Calificacion(idCalificacion, comentario, nota, fecha, cliente, curso));
        }
    }

    // Buscar calificación en BD
    public Calificacion buscarCalificacion(int idCalificacion) {
        try (ResultSet rs = repoC.leerCalificacion(idCalificacion)) {
            if (rs != null && rs.next()) {
                return new Calificacion(
                        rs.getInt("idCalificacion"),
                        rs.getString("comentario"),
                        rs.getInt("nota"),
                        rs.getDate("fecha"),
                        null, // Cliente se resuelve en otra capa
                        null  // Curso se resuelve en otra capa
                );
            }
        } catch (Exception e) {
            System.err.println("Error en buscarCalificacion: " + e.getMessage());
        }
        return null;
    }

    // Actualizar calificación en BD y en la lista
    public void actualizarCalificacion(int idCalificacion, String comentario, int nota, Date fecha, Cliente cliente, Curso curso) {
        int idCliente = 0;
        int idCurso = 0;

        if (cliente != null) {
            idCliente = cliente.getIdUsuario();
        }

        if (curso != null) {
            idCurso = curso.getIdCurso();
        }

        boolean actualizada = repoC.actualizarCalificacion(idCalificacion, nota, comentario, fecha.toString(), idCliente, idCurso);
        if (actualizada) {
            for (int i = 0; i < calificaciones.size(); i++) {
                Calificacion c = calificaciones.get(i);
                if (c.getIdCalificacion() == idCalificacion) {
                    calificaciones.set(i, new Calificacion(idCalificacion, comentario, nota, fecha, cliente, curso));
                    break;
                }
            }
        }
    }

    // Eliminar calificación en BD y en la lista
    public void eliminarCalificacion(int idCalificacion) {
        boolean eliminada = repoC.eliminarCalificacion(idCalificacion);
        if (eliminada) {
            calificaciones.removeIf(c -> c.getIdCalificacion() == idCalificacion);
        }
    }

    // Obtener todas las calificaciones en memoria
    public List<Calificacion> obtenerCalificaciones() {
        return calificaciones;
    }
}
