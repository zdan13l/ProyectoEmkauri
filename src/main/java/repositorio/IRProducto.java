package repositorio;

import modelo.Producto;
import java.sql.ResultSet;
import java.util.List;

public interface IRProducto {
    boolean agregar(Producto producto);
    boolean actualizar(Producto producto);
    boolean eliminar(int idProducto);
    Producto buscarPorId(int idProducto);
    List<Producto> listarTodos();
    List<Producto> buscarPorTitulo(String titulo);
    Producto mapearProducto(ResultSet rs);
}
