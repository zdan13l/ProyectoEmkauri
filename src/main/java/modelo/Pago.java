package modelo;

import java.util.Date;

// Modelo que representa un pago asociado a una compra.
public class Pago {
    private int idPago;
    private double monto;
    private String metodo;
    private Date fecha;

    public Pago(int idPago, double monto, String metodo, Date fecha) {
        this.idPago = idPago;
        this.monto = monto;
        this.metodo = metodo;
        this.fecha = fecha;
    }

    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    @Override
    public String toString() { return "Pago"; }
}
