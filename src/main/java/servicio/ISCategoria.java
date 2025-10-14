package servicio;

import modelo.Categoria;
import java.util.List;

// Interfaz para la gestión de categorías.
public interface ISCategoria {
    boolean crearCategoria(Categoria categoria);
    Categoria buscarPorNombre(String nombre);
    boolean actualizarCategoria(Categoria categoria);
    List<Categoria> listarCategorias();
    boolean eliminarCategoria(int id);
}
