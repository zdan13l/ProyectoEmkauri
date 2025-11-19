package repositorio;

import modelo.Categoria;
import java.util.List;

// Interfaz para operaciones CRUD en la entidad Categoria.
public interface IRCategoria {
    boolean agregar(Categoria categoria);
    Categoria buscarNombre(String nombre);
    List<Categoria> buscarNombreParcial(String nombre);
    boolean actualizar(Categoria categoria);
    List<Categoria> listarTodas();
    boolean eliminar(int idCategoria);
}
