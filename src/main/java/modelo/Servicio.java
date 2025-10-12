package modelo;

// Modelo que representa un servicio, que es un tipo de producto.
public class Servicio extends Producto {
    private int duracion;
    private String ubicacion;
    private String modalidad;

    public Servicio() {
        super();
        this.duracion = 0;
        this.ubicacion = "";
        this.modalidad = "";
    }

    public Servicio(int idProducto, String titulo, String descripcion, double precio, Usuario emprendedor, int duracion, String ubicacion, String modalidad) {
        super(idProducto, titulo, descripcion, precio, emprendedor);
        this.duracion = duracion;
        this.ubicacion = ubicacion;
        this.modalidad = modalidad;
    }

    public int getDuracion() { return duracion; }
    public void setDuracion(int duracion) { this.duracion = duracion; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    @Override
    public String toString() { return "Servicio"; }

}
