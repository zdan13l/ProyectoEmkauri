package repositorio;

import modelo.Categoria;

public interface IRCategoria {
    boolean agregar(Categoria categoria);
    boolean actualizar(Categoria categoria);
    boolean eliminar(int idCategoria);
    Categoria buscarPorId(int idCategoria);
}
