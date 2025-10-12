package modelo;

// Modelo que representa una solicitud realizada por un usuario a un reclutador.
public class Solicitud {
    private int idSolicitud;
    private Usuario solicitante;
    private Usuario reclutador;
    private String estado;
    private String mensaje;
    Producto productoAsociado;
    Usuario emprendedor;

    public Solicitud() {
        this.idSolicitud = 0;
        this.solicitante = new Usuario();
        this.reclutador = new Usuario();
        this.estado = "";
        this.mensaje = "";
        this.productoAsociado = new Producto();
        this.emprendedor = new Usuario();
    }

    public Solicitud(int idSolicitud, Usuario solicitante, Usuario reclutador, String estado, String mensaje, Producto productoAsociado, Usuario emprendedor) {
        this.idSolicitud = idSolicitud;
        this.solicitante = solicitante;
        this.reclutador = reclutador;
        this.estado = estado;
        this.mensaje = mensaje;
        this.productoAsociado = productoAsociado;
        this.emprendedor = emprendedor;
    }

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }
    public Usuario getSolicitante() { return solicitante; }
    public void setSolicitante(Usuario solicitante) { this.solicitante = solicitante; }
    public Usuario getReclutador() { return reclutador; }
    public void setReclutador(Usuario reclutador) { this.reclutador = reclutador; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public Producto getProductoAsociado() { return productoAsociado; }
    public void setProductoAsociado(Producto productoAsociado) { this.productoAsociado = productoAsociado; }
    public Usuario getEmprendedor() { return emprendedor; }
    public void setEmprendedor(Usuario emprendedor) { this.emprendedor = emprendedor; }

    @Override
    public String toString() { return "Solicitud"; }
}
