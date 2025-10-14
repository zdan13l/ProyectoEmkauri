package servicio;

import modelo.Categoria;

import java.util.List;

public interface ISCategoria {
    boolean crearCategoria(Categoria categoria);
    Categoria buscarPorNombre(String nombre);
    boolean actualizarCategoria(Categoria categoria);
    List<Categoria> listarCategorias();
    boolean eliminarCategoria(int id);
}
