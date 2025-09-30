package modelo;

import java.util.Date;

public class Calificacion {
    private int idCalificacion;
    private int nota;
    private String comentario;
    private Date fecha;
    private Cliente cliente;
    private Curso curso;

    public Calificacion(int idCalificacion, String comentario, int nota, Date fecha, Cliente cliente, Curso curso) {
        this.idCalificacion = idCalificacion;
        this.comentario = comentario;
        this.nota = nota;
        this.fecha = fecha;
        this.cliente = cliente;
        this.curso = curso;
    }

    public int getIdCalificacion() {
        return idCalificacion;
    }

    public int getNota() {
        return nota;
    }

    public String getComentario() {
        return comentario;
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

    @Override
    public String toString() {
        return "Calificacion";
    }
}