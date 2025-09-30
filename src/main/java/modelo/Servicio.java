package modelo;

public class Servicio {
    private int idServicio;
    private double precio;
    private String nombre;
    private String descripcion;
    private String estado;
    private Categoria categoria;
    private Emprendedor emprendedor;

    public Servicio(int idServicio, double precio, String nombre, String descripcion, String estado, Categoria categoria, Emprendedor emprendedor) {
        this.idServicio = idServicio;
        this.precio = precio;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.categoria = categoria;
        this.emprendedor = emprendedor;
    }

    public int getIdServicio() {
        return idServicio;
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

    @Override
    public String toString() {
        return "Servicio";
    }
}
