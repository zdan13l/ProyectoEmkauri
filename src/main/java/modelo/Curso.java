package modelo;

import java.util.List;

// Modelo que representa un curso, que es un tipo de producto.
public class Curso extends Producto {
    private List<Material> materiales;
    private int duracionCurso;
    private String nivelDificultad;
    private String certificacion;

    public Curso() {
        super();
        this.materiales = null;
        this.duracionCurso = 0;
        this.nivelDificultad = "";
        this.certificacion = "";
    }

    public Curso(int idProducto, String titulo, String descripcion, double precio, Usuario emprendedor, Categoria categoria, List<Material> materiales, int duracionHoras, String nivelDificultad, String certificacion) {
        super(idProducto, titulo, descripcion, precio, emprendedor, categoria);
        this.materiales = materiales;
        this.duracionCurso = duracionHoras;
        this.nivelDificultad = nivelDificultad;
        this.certificacion = certificacion;
    }

    public List<Material> getMateriales() { return materiales; }
    public void setMateriales(List<Material> materiales) { this.materiales = materiales; }
    public int getDuracionCurso() { return duracionCurso; }
    public void setDuracionCurso(int duracionCurso) { this.duracionCurso = duracionCurso; }
    public String getNivelDificultad() { return nivelDificultad; }
    public void setNivelDificultad(String nivelDificultad) { this.nivelDificultad = nivelDificultad; }
    public String getCertificacion() { return certificacion; }
    public void setCertificacion(String certificacion) { this.certificacion = certificacion; }

    @Override
    public String toString() { return "Curso"; }
}
