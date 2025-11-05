package modelo;

import java.util.Date;
import java.util.List;

// Modelo que representa una compra realizada por un cliente.
public class Compra {
    private int idCompra;
    private Usuario cliente;
    private List<Producto> productos;
    private double montoFinal;
    private Date fechaCompra;
    private Pago pago;

    public Compra() {
        this.idCompra = 0;
        this.cliente = new Usuario();
        this.productos = null;
        this.montoFinal = 0.0;
        this.fechaCompra = new Date();
        this.pago = new Pago();
    }

    public Compra(int idCompra, Usuario cliente, List<Producto> productos, double montoFinal, Date fechaCompra, Pago pago) {
        this.idCompra = idCompra;
        this.cliente = cliente;
        this.productos = productos;
        this.montoFinal = montoFinal;
        this.fechaCompra = fechaCompra;
        this.pago = pago;
    }

    public int getIdCompra() { return idCompra; }
    public void setIdCompra(int idCompra) { this.idCompra = idCompra; }
    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) { this.cliente = cliente; }
    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }
    public double getMontoFinal() { return montoFinal; }
    public void setMontoFinal(double montoFinal) { this.montoFinal = montoFinal; }
    public Date getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(Date fechaCompra) { this.fechaCompra = fechaCompra; }
    public Pago getPago() { return pago; }
    public void setPago(Pago pago) { this.pago = pago; }

    @Override
    public String toString() { return "Compra"; }
}
