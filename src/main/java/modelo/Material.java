package modelo;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class Material {
    private static final AtomicInteger SEQ = new AtomicInteger(1);

    private final int idMaterial;     // autogenerado en memoria
    private final int idCurso; 
    private String titulo;
    private String url;
    private String tipo;              // LINK/PDF/VIDEO/OTRO
    private LocalDateTime fechaSubida;

    public Material(int idCurso, String titulo, String url, String tipo) {
        this.idMaterial = SEQ.getAndIncrement();
        this.idCurso = idCurso;
        this.titulo = titulo;
        this.url = url;
        this.tipo = (tipo == null || tipo.isBlank()) ? "LINK" : tipo.trim().toUpperCase();
        this.fechaSubida = LocalDateTime.now();
    }

    public int getIdMaterial() { return idMaterial; }
    public int getIdCurso() { return idCurso; }
    public String getTitulo() { return titulo; }
    public String getUrl() { return url; }
    public String getTipo() { return tipo; }
    public LocalDateTime getFechaSubida() { return fechaSubida; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setUrl(String url) { this.url = url; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setFechaSubida(LocalDateTime fechaSubida) { this.fechaSubida = fechaSubida; }

    @Override
    public String toString() {
        return "[" + tipo + "] " + titulo + " (" + url + ")";
    }
}