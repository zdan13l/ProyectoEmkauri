package modelo;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Pago {
    private int idPago;
    private double monto;
    private String metodo; // TARJETA | PSE | EFECTIVO (sugerido)
    private String estado; // PENDIENTE | APROBADO | RECHAZADO (sugerido)
    private Date fecha;
    private List<Compra> compras; // opcional (no se usa en CRUD directo)

    // Constructor para crear (sin id)
    public Pago(double monto, String metodo, String estado, Date fecha) {
        this.monto = monto;
        this.metodo = metodo;
        this.estado = estado;
        this.fecha = fecha;
    }

    // Constructor para leer (con id)
    public Pago(int idPago, double monto, String metodo, String estado, Date fecha) {
        this.idPago = idPago;
        this.monto = monto;
        this.metodo = metodo;
        this.estado = estado;
        this.fecha = fecha;
    }

    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public List<Compra> getCompras() { return compras; }
    public void setCompras(List<Compra> compras) { this.compras = compras; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pago)) return false;
        Pago pago = (Pago) o;
        return idPago == pago.idPago;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPago);
    }

    @Override
    public String toString() {
        return "Pago{id=" + idPago + ", monto=" + monto + ", metodo='" + metodo + "', estado='" + estado + "', fecha=" + fecha + "}";
    }
}
