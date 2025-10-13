package modelo;

import java.math.BigDecimal;

public class PagoSolicitud {
    private final String titular;
    private final String numeroTarjeta;   // solo dígitos
    private final int mesExp;             // 1..12
    private final int anioExp;            // YYYY
    private final String cvv;             // 3-4 dígitos
    private final BigDecimal monto;       // usa BigDecimal como tu modelo Pago
    private final PaymentMethod metodo;

    public PagoSolicitud(String titular, String numeroTarjeta, int mesExp, int anioExp,
                         String cvv, BigDecimal monto, PaymentMethod metodo) {
        this.titular = titular;
        this.numeroTarjeta = numeroTarjeta;
        this.mesExp = mesExp;
        this.anioExp = anioExp;
        this.cvv = cvv;
        this.monto = monto;
        this.metodo = metodo;
    }
    public String getTitular() { return titular; }
    public String getNumeroTarjeta() { return numeroTarjeta; }
    public int getMesExp() { return mesExp; }
    public int getAnioExp() { return anioExp; }
    public String getCvv() { return cvv; }
    public BigDecimal getMonto() { return monto; }
    public PaymentMethod getMetodo() { return metodo; }
}
