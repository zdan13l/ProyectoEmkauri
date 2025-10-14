package servicio;

import modelo.Producto;
import repositorio.IRProducto;
import java.util.List;

public class SProducto implements ISProducto {

    private final IRProducto repoP;

    public SProducto(IRProducto repoP) {
        this.repoP = repoP;
    }

    @Override
    public boolean crearProducto(Producto producto) {
        return repoP.agregar(producto);
    }

    @Override
    public Producto obtenerProductoPorId(int id) {
        return repoP.buscarPorId(id);
    }

    @Override
    public List<Producto> listarProductos() {
        return repoP.listarTodos();
    }

    @Override
    public boolean actualizarProducto(Producto producto) {
        return repoP.actualizar(producto);
    }

    @Override
    public boolean eliminarProducto(int id) {
        return repoP.eliminar(id);
    }

    @Override
    public List<Producto> buscarPorNombre(String nombre) {
        return repoP.buscarPorTitulo(nombre);
    }
}
