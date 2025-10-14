package repositorio;

import modelo.Categoria;

import java.util.List;

public interface IRCategoria {
    boolean agregar(Categoria categoria);
    Categoria buscarPorNombre(String nombre);
    boolean actualizar(Categoria categoria);
    List<Categoria> listarTodas();
    boolean eliminar(int idCategoria);
}
