package modelo;

import java.util.Date;

public class Compra {
    private int idCompra;
    private Date fecha;
    private Cliente cliente;
    private Curso curso;
    private Servicio servicio;
    private Pago pago;

    public Compra(int idCompra, Date fecha, Cliente cliente, Curso curso, Servicio servicio, Pago pago) {
        this.idCompra = idCompra;
        this.fecha = fecha;
        this.cliente = cliente;
        this.curso = curso;
        this.servicio = servicio;
        this.pago = pago;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public Date getFecha() {
        return fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Curso getCurso() {
        return curso;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public Pago getPago() {
        return pago;
    }

    @Override
    public String toString() {
        return "Compra";
    }
}
