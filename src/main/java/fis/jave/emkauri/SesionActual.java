package fis.jave.emkauri;

import modelo.Usuario;

// Clase para manejar la sesión actual del usuario en la aplicación.
public class SesionActual {
    private static Usuario usuarioActual;

    // Establece el usuario actual en sesión.
    public static void setUsuarioActual(Usuario usuario) { usuarioActual = usuario; }
    public static Usuario getUsuarioActual() { return usuarioActual; }
    public static void cerrarSesion() { usuarioActual = null; }
}
