package modelo;

import java.util.List;

// Modelo que representa un curso, que es un tipo de producto.
public class Curso extends Producto {
    private List<Material> materiales;
    private int duracionHoras;
    private String nivelDificultad;
    private String certificacion;

    public Curso() {
        super();
        this.materiales = null;
        this.duracionHoras = 0;
        this.nivelDificultad = "";
        this.certificacion = "";
    }

    public Curso(int idProducto, String titulo, String descripcion, double precio, Usuario emprendedor, List<Material> materiales, int duracionHoras, String nivelDificultad, String certificacion) {
        super(idProducto, titulo, descripcion, precio, emprendedor);
        this.materiales = materiales;
        this.duracionHoras = duracionHoras;
        this.nivelDificultad = nivelDificultad;
        this.certificacion = certificacion;
    }

    public List<Material> getMateriales() { return materiales; }
    public void setMateriales(List<Material> materiales) { this.materiales = materiales; }
    public int getDuracionHoras() { return duracionHoras; }
    public void setDuracionHoras(int duracionHoras) { this.duracionHoras = duracionHoras; }
    public String getNivelDificultad() { return nivelDificultad; }
    public void setNivelDificultad(String nivelDificultad) { this.nivelDificultad = nivelDificultad; }
    public String getCertificacion() { return certificacion; }
    public void setCertificacion(String certificacion) { this.certificacion = certificacion; }

    @Override
    public String toString() { return "Curso"; }
}
