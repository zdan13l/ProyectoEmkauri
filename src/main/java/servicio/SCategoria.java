package servicio;

import repositorio.IRCategoria;
import repositorio.IRProducto;

public class SCategoria implements ISCategoria {
    private final IRCategoria repoC;

    public SCategoria(IRCategoria repoC) {
        this.repoC = repoC;
    }
    @Override
    public boolean crearCategoria(String nombre) {
        // Implementación para crear una categoría
        return false;
    }

    @Override
    public boolean actualizarCategoria(int id, String nuevoNombre) {
        // Implementación para actualizar una categoría
        return false;
    }

    @Override
    public boolean eliminarCategoria(int id) {
        // Implementación para eliminar una categoría
        return false;
    }

    @Override
    public String obtenerCategoriaPorId(int id) {
        // Implementación para obtener una categoría por ID
        return null;
    }
}
