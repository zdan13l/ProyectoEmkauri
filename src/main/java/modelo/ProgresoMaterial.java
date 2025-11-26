package modelo;

import java.util.Date;

// Modelo que representa el progreso de un cliente en un material.
public class ProgresoMaterial {
    private int idProgreso;
    private Usuario idCliente;
    private Material idMaterial;
    private boolean visto;

    public ProgresoMaterial() {
        this.idProgreso = 0;
        this.idCliente = null;
        this.idMaterial = null;
        this.visto = false;
    }

    public ProgresoMaterial(int idProgreso, Usuario idCliente, Material idMaterial, boolean visto) {
        this.idProgreso = idProgreso;
        this.idCliente = idCliente;
        this.idMaterial = idMaterial;
        this.visto = visto;
    }

    // Getters y setters
    public int getIdProgreso() { return idProgreso; }
    public Usuario getIdCliente() { return idCliente; }
    public Material getIdMaterial() { return idMaterial; }
    public boolean isVisto() { return visto; }

    public void setIdProgreso(int idProgreso) { this.idProgreso = idProgreso; }
    public void setIdCliente(Usuario idCliente) { this.idCliente = idCliente; }
    public void setIdMaterial(Material idMaterial) { this.idMaterial = idMaterial; }
    public void setVisto(boolean visto) { this.visto = visto; }

    @Override
    public String toString() { return "ProgresoMaterial"; }
}
