package servicio;

import modelo.Pago;
import repositorio.IRPago;
import java.util.List;

public class SPago implements ISPago {
    private final IRPago repoP;

    public SPago(IRPago repoP) {
        this.repoP = repoP;
    }

    @Override
    public boolean crearPago(Pago pago) {
        return repoP.crearPago(pago);
    }

    @Override
    public Pago obtenerPagoPorId(int idPago) {
        return repoP.obtenerPagoPorId(idPago);
    }

    @Override
    public List<Pago> obtenerTodos() {
        return repoP.obtenerTodos();
    }

    @Override
    public boolean actualizarPago(Pago pago) {
        return repoP.actualizarPago(pago);
    }

    @Override
    public boolean eliminarPago(int idPago) {
        return repoP.eliminarPago(idPago);
    }
}
