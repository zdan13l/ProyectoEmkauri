package servicio;

import modelo.Categoria;
import repositorio.IRCategoria;

import java.util.List;

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
    public Categoria buscarPorNombre(String nombre) {
        return repoC.buscarPorNombre(nombre);
    }

    @Override
    public boolean actualizarCategoria(int id, String nuevoNombre) {
        // Implementación para actualizar una categoría
        return false;
    }

    @Override
    public List<Categoria> listarCategorias() {
        return repoC.listarTodas();
    }

    @Override
    public boolean eliminarCategoria(int id) {
        // Implementación para eliminar una categoría
        return false;
    }


}
