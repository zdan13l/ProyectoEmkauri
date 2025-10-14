package servicio;

import modelo.Categoria;

import java.util.List;

public interface ISCategoria {
    boolean crearCategoria(String nombre);
    Categoria buscarPorNombre(String nombre);
    boolean actualizarCategoria(int id, String nuevoNombre);
    List<Categoria> listarCategorias();
    boolean eliminarCategoria(int id);
}
