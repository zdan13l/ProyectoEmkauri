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
    public boolean crearCategoria(Categoria categoria) {
        // Implementación para crear una categoría
        return repoC.agregar(categoria);
    }

    @Override
    public Categoria buscarPorNombre(String nombre) {
        return repoC.buscarPorNombre(nombre);
    }

    @Override
    public boolean actualizarCategoria(Categoria categoria) {
        return repoC.actualizar(categoria);
    }

    @Override
    public List<Categoria> listarCategorias() {
        return repoC.listarTodas();
    }

    @Override
    public boolean eliminarCategoria(int id) {
        return repoC.eliminar(id);
    }


}
