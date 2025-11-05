package servicio;

import modelo.Compra;
import java.util.List;

// Interfaz para la gestión de compras.
public interface ISCompra {
    boolean crearCompra(Compra compra);
    Compra obtenerCompraPorId(int id);
    List<Compra> listarCompras();
    boolean actualizarCompra(Compra compra);
    boolean eliminarCompra(int id);
}
