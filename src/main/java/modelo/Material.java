package modelo;

// Modelo que representa un material asociado a un producto o servicio.
public class Material {
    private int idMaterial;
    private String titulo;
    private String tipo;
    private String url;

    public Material(int idMaterial, String titulo, String tipo, String url) {
        this.idMaterial = idMaterial;
        this.titulo = titulo;
        this.tipo = tipo;
        this.url = url;
    }

    public int getIdMaterial() { return idMaterial; }
    public void setIdMaterial(int idMaterial) { this.idMaterial = idMaterial; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    @Override
    public String toString() { return "Material"; }
}
