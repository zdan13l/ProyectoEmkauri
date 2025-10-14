package servicio;

import modelo.Producto;
import java.util.List;

public interface ISProducto {
    boolean crearProducto(Producto producto);
    Producto obtenerProductoPorId(int id);
    List<Producto> listarProductos();
    boolean actualizarProducto(Producto producto);
    boolean eliminarProducto(int id);
    List<Producto> buscarPorNombre(String nombre);
}
