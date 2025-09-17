package modelo;

import java.util.List;

public class Curso {
    private int idCurso;
    private double precio;
    private String nombre;
    private String descripcion;
    private String estado;
    private Categoria categoria;
    private Emprendedor emprendedor;
    private List<Calificacion> calificaciones;

    public Curso(int idCurso, double precio, String nombre, String descripcion, String estado, Categoria categoria, Emprendedor emprendedor, List<Calificacion> calificaciones) {
        this.idCurso = idCurso;
        this.precio = precio;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.categoria = categoria;
        this.emprendedor = emprendedor;
        this.calificaciones = calificaciones;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public double getPrecio() {
        return precio;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Emprendedor getEmprendedor() {
        return emprendedor;
    }

    public List<Calificacion> getCalificaciones() {
        return calificaciones;
    }

    @Override
    public String toString() {
        return "Curso";
    }
}
