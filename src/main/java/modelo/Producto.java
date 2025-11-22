package modelo;

import java.util.*;

// Modelo que representa un producto en el sistema.
public class Producto {
    private int idProducto;
    private String titulo;
    private String descripcion;
    private String estado;
    private double precio;
    Usuario emprendedor;
    Categoria categoria;
    List<Calificacion> calificaciones;

    public Producto() {
        this.idProducto = 0;
        this.titulo = "";
        this.descripcion = "";
        this.estado = "";
        this.precio = 0.0;
        this.emprendedor = new Usuario();
        this.categoria = new Categoria();
        this.calificaciones = new ArrayList<>();
    }

    public Producto(int idProducto, String titulo, String descripcion, String estado, double precio, Usuario emprendedor, Categoria categoria) {
        this.idProducto = idProducto;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.precio = precio;
        this.emprendedor = emprendedor;
        this.categoria = categoria;
        this.calificaciones = new ArrayList<>();
    }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public Usuario getEmprendedor() { return emprendedor; }
    public void setEmprendedor(Usuario emprendedor) { this.emprendedor = emprendedor; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public List<Calificacion> getCalificaciones() { return calificaciones; }
    public void setCalificaciones(List<Calificacion> calificaciones) { this.calificaciones = calificaciones; }
    public void agregarCalificacion(Calificacion calificacion) { this.calificaciones.add(calificacion); }

    @Override
    public String toString() { return "Producto"; }
}
