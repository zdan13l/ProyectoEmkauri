package servicio;

import modelo.Compra;
import repositorio.IRCompra;
import java.util.List;

// Servicio que implementa la lógica de negocio para las compras.
public class SCompra implements ISCompra {
    private final IRCompra repoC;

    // Constructor con inyección de dependencia del repositorio.
    public SCompra(IRCompra repoC) { this.repoC = repoC; }

    // Crea una nueva compra.
    public boolean crearCompra(Compra compra) {
        return repoC.insertar(compra);
    }

    // Obtiene una compra por su ID.
    public Compra obtenerCompraPorId(int id) {
        return repoC.obtenerPorId(id);
    }

    // Lista todas las compras.
    public List<Compra> listarCompras() {
        return repoC.listar();
    }

    // Actualiza una compra existente.
    public boolean actualizarCompra(Compra compra) {
        return repoC.actualizar(compra);
    }

    // Elimina una compra por su ID.
    public boolean eliminarCompra(int id) {
        return repoC.eliminar(id);
    }
}
