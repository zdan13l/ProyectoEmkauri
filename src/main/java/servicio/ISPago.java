package servicio;

import modelo.Pago;

import java.util.List;

public interface ISPago {
    Pago crear(Pago pago);
    Pago buscarPorId(long idPago);
    List<Pago> listar();
    boolean actualizar(Pago pago);
    boolean eliminar(long idPago);

    void asociarPagoACompra(long idPago, int idCompra);
    void desasociarPagoDeCompra(int idCompra);
}
