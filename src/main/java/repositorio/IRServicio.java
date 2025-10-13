package repositorio;

import modelo.Servicio;
import java.util.List;

public interface IRServicio {
    List<Servicio> listar();
    Servicio buscarPorId(int id);
    int crear(Servicio s);
    boolean actualizar(Servicio s);
    boolean eliminar(int id);
}
