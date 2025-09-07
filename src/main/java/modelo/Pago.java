package modelo;

public class Pago {
    private int idPago;
    private double monto;
    private String fecha;
    private String metodo;
    private String estado;

    public Pago(int idPago, double monto, String fecha, String metodo, String estado) {
        this.idPago = idPago;
        this.monto = monto;
        this.fecha = fecha;
        this.metodo = metodo;
        this.estado = estado;
    }

    public int getIdPago() {
        return idPago;
    }

    public double getMonto() {
        return monto;
    }

    public String getFecha() {
        return fecha;
    }

    public String getMetodo() {
        return metodo;
    }

    public String getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return "Pago";
    }
}
