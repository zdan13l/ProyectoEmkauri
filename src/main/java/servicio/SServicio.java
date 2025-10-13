package servicio;

import modelo.Servicio;
import repositorio.IRServicio;

import java.util.List;

public class SServicio implements ISServicio {

    private final IRServicio repo;

    public SServicio(IRServicio repo) { this.repo = repo; }

    @Override public List<Servicio> listar() { return repo.listar(); }
    @Override public Servicio buscarPorId(int id) { return repo.buscarPorId(id); }
    @Override public int crear(Servicio s) { return repo.crear(s); }
    @Override public boolean actualizar(Servicio s) { return repo.actualizar(s); }
    @Override public boolean eliminar(int id) { return repo.eliminar(id); }
}
