package modelo;

public class Servicio {
    private Categoria categoria;

    private int idServicio;
    private String nombre;
    private String descripcion;
    private double precio;
    private String estado;

    public Servicio(int idServicio, String nombre, String estado, String descripcion, double precio) {
        this.idServicio = idServicio;
        this.nombre = nombre;
        this.estado = estado;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public int getIdServicio() {
        return idServicio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return "Servicio";
    }
}
