package modelo;

// Modelo que representa un servicio, que es un tipo de producto.
public class Servicio extends Producto {
    private int duracionServicio;
    private String ubicacion;
    private String modalidad;

    public Servicio() {
        super();
        this.duracionServicio = 0;
        this.ubicacion = "";
        this.modalidad = "";
    }

    public Servicio(int idProducto, String titulo, String descripcion, String estado, double precio, Usuario emprendedor, Categoria categoria, int duracionServicio, String ubicacion, String modalidad) {
        super(idProducto, titulo, descripcion, estado, precio, emprendedor, categoria);
        this.duracionServicio = duracionServicio;
        this.ubicacion = ubicacion;
        this.modalidad = modalidad;
    }

    public int getDuracionServicio() { return duracionServicio; }
    public void setDuracionServicio(int duracion) { this.duracionServicio = duracion; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    @Override
    public String toString() { return "Servicio"; }
}
