package modelo;

public class Calificacion {
    private int idCalificacion;
    private int puntuacion;
    private String comentario;
    private String fecha;

    public Calificacion(int idCalificacion, String comentario, int puntuacion, String fecha) {
        this.idCalificacion = idCalificacion;
        this.comentario = comentario;
        this.puntuacion = puntuacion;
        this.fecha = fecha;
    }

    public int getIdCalificacion() {
        return idCalificacion;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public String getFecha() {
        return fecha;
    }

    @Override
    public String toString() {
        return "Calificacion";
    }
}