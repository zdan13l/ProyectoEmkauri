package servicio;

import modelo.Pago;
import java.util.List;

/**
 * Contrato del servicio de negocio para CU-009 (Gestión Pago).
 */
public interface ISPago {
    Pago crear(Pago pago);
    Pago buscarPorId(int idPago);
    List<Pago> listar();
    boolean actualizar(Pago pago);
    boolean eliminar(int idPago);

    // Reglas de asociación 1:1 Compra <-> Pago
    void asociarPagoACompra(int idPago, int idCompra);
    void desasociarPagoDeCompra(int idCompra);
}
