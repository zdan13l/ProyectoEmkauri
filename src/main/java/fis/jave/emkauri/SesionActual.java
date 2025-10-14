package fis.jave.emkauri;

import modelo.Producto;
import modelo.Usuario;
import java.util.ArrayList;
import java.util.List;

// Clase para manejar la sesión actual del usuario en la aplicación.
public class SesionActual {
    private static Usuario usuarioActual;
    private static final List<Producto> carrito = new ArrayList<>();

    // Establece el usuario actual en sesión.
    public static void setUsuarioActual(Usuario usuario) { usuarioActual = usuario; }
    public static Usuario getUsuarioActual() { return usuarioActual; }
    public static List<Producto> getCarrito() { return carrito; }
    public static void agregarProductoAlCarrito(Producto producto) { carrito.add(producto); }
    public static void vaciarCarrito() { carrito.clear(); }
    public static void cerrarSesion() { usuarioActual = null; }
}
