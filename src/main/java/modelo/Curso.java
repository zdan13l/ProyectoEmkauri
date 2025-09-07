package modelo;

import java.util.List;

public class Curso {
    private Categoria categoria;
    private List<Calificacion> calificaciones;

    private int idCurso;
    private String titulo;
    private double precio;
    private String descripcion;
    private String estado;

    public Curso(int idCurso, String titulo, String descripcion, double precio, String estado) {
        this.idCurso = idCurso;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public String getTitulo() {
        return titulo;
    }

    public double getPrecio() {
        return precio;
    }

    @Override
    public String toString() {
        return "Curso";
    }
}
