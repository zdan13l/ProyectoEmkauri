package modelo;

// Modelo que representa un rol dentro el sistema.
public class Rol {
    private int idRol;
    private String nombre;

    public Rol() {
        this.idRol = 0;
        this.nombre = "";
    }

    public Rol(int idRol, String nombre) {
        this.idRol = idRol;
        this.nombre = nombre;
    }

    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() { return "Rol"; }
}
