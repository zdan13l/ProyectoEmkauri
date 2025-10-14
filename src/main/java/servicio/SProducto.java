package servicio;

import modelo.Producto;
import repositorio.IRProducto;
import java.util.List;

// Servicio de negocio para la gestión de productos.
public class SProducto implements ISProducto {

    private final IRProducto repoP;

    // Constructor con inyección de dependencia.
    public SProducto(IRProducto repoP) {
        this.repoP = repoP;
    }

    // Crea un nuevo producto.
    public boolean crearProducto(Producto producto) {
        return repoP.agregar(producto);
    }

    // Obtiene un producto por su ID.
    public Producto obtenerProductoPorId(int id) {
        return repoP.buscarPorId(id);
    }

    // Busca productos por nombre (título).
    public List<Producto> buscarPorNombre(String nombre) {
        return repoP.buscarPorTitulo(nombre);
    }

    // Lista todos los productos disponibles.
    public List<Producto> listarProductos() { return repoP.listarTodos(); }

    // Lista los productos comprados por un cliente específico.
    public List<Producto> listarComprados(int idCliente) { return repoP.listarComprados(idCliente); }

    // Lista los productos asociados a un emprendedor específico.
    public List<Producto> listarPorEmprendedor(int idEmprendedor) { return repoP.listarPorEmprendedor(idEmprendedor); }

    // Actualiza la información de un producto existente.
    public boolean actualizarProducto(Producto producto) {
        return repoP.actualizar(producto);
    }

    // Elimina un producto por su ID.
    public boolean eliminarProducto(int id) {
        return repoP.eliminar(id);
    }
}
