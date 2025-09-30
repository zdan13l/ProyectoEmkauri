package modelo;

import java.util.Date;
import java.util.List;

public class Pago {
    private int idPago;
    private double monto;
    private String metodo;
    private String estado;
    private Date fecha;
    private List<Compra> compras;

    public Pago(int idPago, double monto, String metodo, String estado, Date fecha, List<Compra> compras) {
        this.idPago = idPago;
        this.monto = monto;
        this.metodo = metodo;
        this.estado = estado;
        this.fecha = fecha;
        this.compras = compras;
    }

    public int getIdPago() {
        return idPago;
    }

    public double getMonto() {
        return monto;
    }

    public String getMetodo() {
        return metodo;
    }

    public String getEstado() {
        return estado;
    }

    public Date getFecha() {
        return fecha;
    }

    public List<Compra> getCompras() {
        return compras;
    }

    @Override
    public String toString() {
        return "Pago";
    }
}
