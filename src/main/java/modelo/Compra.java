package modelo;

public class Compra {
    private int idCompra;
    private String fecha;
    private double total;

    public Compra(int idCompra, String fecha, double total) {
        this.idCompra = idCompra;
        this.fecha = fecha;
        this.total = total;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public String getFecha() {
        return fecha;
    }

    public double getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "Compra";
    }
}
