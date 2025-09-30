package modelo;

import java.util.List;

public class Emprendedor extends Usuario {
    private String estado;
    private List<Curso> cursos;
    private List<Servicio> servicios;

    public Emprendedor(int idUsuario, String nombre, String email, String contrasena, String estado, List<Curso> cursos, List<Servicio> servicios) {
        super(idUsuario, nombre, email, contrasena);
        this.estado = estado;
        this.cursos = cursos;
        this.servicios = servicios;
    }

    public String getEstado() {
        return estado;
    }

    public List<Curso> getCursos() {
        return cursos;
    }

    public List<Servicio> getServicios() {
        return servicios;
    }

    @Override
    public String toString() {
        return "Emprendedor";
    }
}