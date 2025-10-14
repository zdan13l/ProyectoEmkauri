package servicio;

public interface ISCategoria {
    boolean crearCategoria(String nombre);
    boolean actualizarCategoria(int id, String nuevoNombre);
    boolean eliminarCategoria(int id);
    String obtenerCategoriaPorId(int id);
}
