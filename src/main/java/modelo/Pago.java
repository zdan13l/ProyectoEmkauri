package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Pago {
    private Long id;                 // autoincrement en H2
    private Long idCompra;           // UNIQUE en Compras (1 compra -> 1 pago)
    private BigDecimal monto;
    private String metodo;           // "EFECTIVO", "TARJETA", "TRANSFERENCIA", etc.
    private LocalDate fecha;         // fecha de registro
    private String estado;           // "PENDIENTE", "CONFIRMADO", "ANULADO"

    public Pago() {}

    public Pago(Long id, Long idCompra, BigDecimal monto, String metodo, LocalDate fecha, String estado) {
        this.id = id;
        this.idCompra = idCompra;
        this.monto = monto;
        this.metodo = metodo;
        this.fecha = fecha;
        this.estado = estado;
    }

    // Getters/Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdCompra() { return idCompra; }
    public void setIdCompra(Long idCompra) { this.idCompra = idCompra; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public void validar() {
        if (monto == null || monto.signum() <= 0) throw new IllegalArgumentException("Monto debe ser > 0");
        if (metodo == null || metodo.isBlank()) throw new IllegalArgumentException("Método requerido");
        if (fecha == null) throw new IllegalArgumentException("Fecha requerida");
        if (estado == null || estado.isBlank()) throw new IllegalArgumentException("Estado requerido");
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pago)) return false;
        Pago pago = (Pago) o;
        return Objects.equals(id, pago.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
