package servicio;

import modelo.Curso;
import repositorio.RCurso;
import java.util.List;

public class SCurso {
    private final RCurso rCurso = new RCurso();
    public List<Curso> listarCursosActivos() { return rCurso.listarActivos(); }
    public Curso buscarPorId(int id) { return rCurso.buscarPorId(id); }
}
