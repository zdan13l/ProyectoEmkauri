package servicio;

import modelo.Categoria;
import repositorio.IRCategoria;

import java.util.List;

// Servicio que implementa la lógica de negocio para las categorías.
public class SCategoria implements ISCategoria {
    private final IRCategoria repoC;

    // Constructor con inyección de dependencia del repositorio.
    public SCategoria(IRCategoria repoC) {
        this.repoC = repoC;
    }

    // Crea una nueva categoría.
    public boolean crearCategoria(Categoria categoria) { return repoC.agregar(categoria); }

    // Obtiene una categoría por su ID.
    public Categoria buscarPorNombre(String nombre) { return repoC.buscarPorNombre(nombre); }

    // Actualiza una categoría existente.
    public boolean actualizarCategoria(Categoria categoria) { return repoC.actualizar(categoria); }

    // Lista todas las categorías.
    public List<Categoria> listarCategorias() {
        return repoC.listarTodas();
    }

    // Elimina una categoría por su ID.
    public boolean eliminarCategoria(int id) { return repoC.eliminar(id); }
}
