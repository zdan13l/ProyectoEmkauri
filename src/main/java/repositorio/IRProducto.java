package repositorio;

import modelo.Producto;
import java.sql.ResultSet;
import java.util.List;

// Interfaz para operaciones CRUD en la entidad Producto.
public interface IRProducto {
    boolean agregar(Producto producto);
    boolean actualizar(Producto producto);
    boolean eliminar(int idProducto);
    Producto buscarPorId(int idProducto);
    List<Producto> buscarPorTitulo(String titulo);
    List<Producto> listarTodos();
    List<Producto> listarComprados(int idCliente);
    List<Producto> listarPorEmprendedor(int idEmprendedor);
    Producto mapearProducto(ResultSet rs);
}
