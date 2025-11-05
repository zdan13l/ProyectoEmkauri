package servicio;

import modelo.Producto;
import java.util.List;

// Interfaz para la gestión de productos.
public interface ISProducto {
    boolean crearProducto(Producto producto);
    Producto obtenerProductoPorId(int id);
    List<Producto> buscarPorNombre(String nombre);
    List<Producto> listarProductos();
    List<Producto> listarComprados(int idCliente);
    List<Producto> listarPorEmprendedor(int idEmprendedor);
    boolean actualizarProducto(Producto producto);
    boolean eliminarProducto(int id);
}
