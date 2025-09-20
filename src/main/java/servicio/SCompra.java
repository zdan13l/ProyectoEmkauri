package servicio;

import modelo.Compra;
import modelo.Cliente;
import modelo.Curso;
import modelo.Servicio;
import modelo.Pago;
import repositorio.RCompra;

import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

// Servicio para manejar lógica de Compra.
public class SCompra {
    private final List<Compra> compras = new ArrayList<>();
    private final RCompra repoC = new RCompra();

    // Registrar compra en BD y en la lista
    public void registrarCompra(int idCompra, Date fecha, Cliente cliente, Curso curso, Servicio servicio, Pago pago) {
        int idCliente = 0;
        int idCurso = 0;
        int idServicio = 0;
        int idPago = 0;

        if (cliente != null) {
            idCliente = cliente.getIdUsuario();
        }
        if (curso != null) {
            idCurso = curso.getIdCurso();
        }
        if (servicio != null) {
            idServicio = servicio.getIdServicio();
        }
        if (pago != null) {
            idPago = pago.getIdPago();
        }

        boolean creada = repoC.crearCompra(idCompra, idCliente, idCurso, idServicio, idPago, fecha.toString());
        if (creada) {
            compras.add(new Compra(idCompra, fecha, cliente, curso, servicio, pago));
        }
    }

    // Buscar compra en BD
    public Compra buscarCompra(int idCompra) {
        try (ResultSet rs = repoC.leerCompra(idCompra)) {
            if (rs != null && rs.next()) {
                return new Compra(
                        rs.getInt("idCompra"),
                        rs.getDate("fecha"),
                        null, // Cliente se resuelve en otra capa
                        null, // Curso
                        null, // Servicio
                        null  // Pago
                );
            }
        } catch (Exception e) {
            System.err.println("Error en buscarCompra: " + e.getMessage());
        }
        return null;
    }

    // Actualizar compra en BD y en la lista
    public void actualizarCompra(int idCompra, Date fecha, Cliente cliente, Curso curso, Servicio servicio, Pago pago) {
        int idCliente = 0;
        int idCurso = 0;
        int idServicio = 0;
        int idPago = 0;

        if (cliente != null) {
            idCliente = cliente.getIdUsuario();
        }
        if (curso != null) {
            idCurso = curso.getIdCurso();
        }
        if (servicio != null) {
            idServicio = servicio.getIdServicio();
        }
        if (pago != null) {
            idPago = pago.getIdPago();
        }

        boolean actualizada = repoC.actualizarCompra(idCompra, idCliente, idCurso, idServicio, idPago, fecha.toString());
        if (actualizada) {
            for (int i = 0; i < compras.size(); i++) {
                Compra c = compras.get(i);
                if (c.getIdCompra() == idCompra) {
                    compras.set(i, new Compra(idCompra, fecha, cliente, curso, servicio, pago));
                    break;
                }
            }
        }
    }

    // Eliminar compra en BD y en la lista
    public void eliminarCompra(int idCompra) {
        boolean eliminada = repoC.eliminarCompra(idCompra);
        if (eliminada) {
            compras.removeIf(c -> c.getIdCompra() == idCompra);
        }
    }

    // Obtener todas las compras en memoria
    public List<Compra> obtenerCompras() {
        return compras;
    }
}
