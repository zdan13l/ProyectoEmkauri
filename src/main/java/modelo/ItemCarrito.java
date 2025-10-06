package modelo;

public class ItemCarrito {
    private int id;
    private String nombre;
    private String tipo; // Curso o Servicio
    private double precio;
    private int cantidad;

    public ItemCarrito(int id, String nombre, String tipo, double precio, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public double getPrecio() { return precio; }
    public int getCantidad() { return cantidad; }

    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getSubtotal() { return precio * cantidad; }

    @Override
    public String toString() {
        return nombre + " (" + tipo + ") x" + cantidad + " - $" + getSubtotal();
    }
}
