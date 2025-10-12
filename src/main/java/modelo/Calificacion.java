package modelo;

import java.util.Date;

// Modelo que representa una calificación dada por un cliente a un producto.
public class Calificacion {
    private int idCalificacion;
    private int puntaje;
    private String comentario;
    private Usuario cliente;
    private Producto producto;
    private Date fecha;

    public Calificacion() {
        this.idCalificacion = 0;
        this.puntaje = 0;
        this.comentario = "";
        this.cliente = new Usuario();
        this.producto = new Producto();
        this.fecha = new Date();
    }

    public Calificacion(int idCalificacion, int puntaje, String comentario, Usuario cliente, Producto producto, Date fecha) {
        this.idCalificacion = idCalificacion;
        this.puntaje = puntaje;
        this.comentario = comentario;
        this.cliente = cliente;
        this.producto = producto;
        this.fecha = fecha;
    }

    public int getIdCalificacion() { return idCalificacion; }
    public void setIdCalificacion(int idCalificacion) { this.idCalificacion = idCalificacion; }
    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) { this.cliente = cliente; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    @Override
    public String toString() { return "Calificacion"; }
}