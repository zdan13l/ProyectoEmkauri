package servicio;

import modelo.Pago;
import repositorio.IRPago;
import java.util.List;

// Servicio de negocio para la gestión de pagos.
public class SPago implements ISPago {
    private final IRPago repoP;

    // Constructor con inyección de dependencia.
    public SPago(IRPago repoP) {
        this.repoP = repoP;
    }

    // Crea un nuevo pago.
    public boolean crearPago(Pago pago) {
        return repoP.crearPago(pago);
    }

    // Obtiene un pago por su ID.
    public Pago obtenerPagoPorId(int idPago) {
        return repoP.obtenerPagoPorId(idPago);
    }

    // Obtiene todos los pagos.
    public List<Pago> obtenerTodos() {
        return repoP.obtenerTodos();
    }

    // Actualiza un pago existente.
    public boolean actualizarPago(Pago pago) {
        return repoP.actualizarPago(pago);
    }

    // Elimina un pago por su ID.
    public boolean eliminarPago(int idPago) {
        return repoP.eliminarPago(idPago);
    }
}
