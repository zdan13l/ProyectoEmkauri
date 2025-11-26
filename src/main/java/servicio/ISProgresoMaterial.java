package servicio;

import modelo.Material;
import modelo.ProgresoMaterial;
import modelo.Usuario;

import java.util.List;

// Interfaz para el servicio de gestión del progreso de materiales por parte de los clientes.
public interface ISProgresoMaterial {
    ProgresoMaterial obtener(int idCliente, int idMaterial);
    ProgresoMaterial crear(Usuario cliente, Material material);
    boolean actualizar(ProgresoMaterial progreso);
    boolean marcarVisto(int idCliente, int idMaterial);
    boolean marcarNoVisto(int idCliente, int idMaterial);
    int contarVistos(int idCliente, int idProducto);
    List<ProgresoMaterial> listarPorCliente(int idCliente);
}
