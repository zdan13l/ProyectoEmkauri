package servicio;

import modelo.Datos;
import modelo.Rol;
import modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repositorio.IRUsuario;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SUsuarioTest {

    @Mock
    private IRUsuario repoU;

    @InjectMocks
    private SUsuario servicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAutenticarUsuarioValido() {
        Usuario user = new Usuario();
        when(repoU.autenticar("test@mail.com", "123")).thenReturn(user);

        boolean resultado = servicio.autenticar("test@mail.com", "123");

        assertTrue(resultado);
        verify(repoU).autenticar("test@mail.com", "123");
    }

    @Test
    void testAutenticarUsuarioPendiente() {
        when(repoU.autenticar("pendiente@mail.com", "123"))
                .thenThrow(new RuntimeException("PENDIENTE"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                servicio.autenticar("pendiente@mail.com", "123"));

        assertEquals("Tu solicitud de registro como emprendedor aún está pendiente de aprobación.", ex.getMessage());
    }

    @Test
    void testObtenerNombre() {
        Datos datos = new Datos();
        datos.setNombre("Daniel");
        Usuario user = new Usuario();
        user.setDatosPersonales(datos);

        when(repoU.buscarPorCorreo("correo@mail.com")).thenReturn(user);

        assertEquals("Daniel", servicio.obtenerNombre("correo@mail.com"));
    }

    @Test
    void testRegistrarEmprendedor() {
        Usuario user = new Usuario();
        Rol rol = new Rol();
        rol.setNombre("Emprendedor");
        user.setRol(rol);

        when(repoU.insertarEmprendedor(user, "mensaje")).thenReturn(true);

        assertTrue(servicio.registrarUsuario(user, "mensaje"));
    }

    @Test
    void testRegistrarCliente() {
        Usuario user = new Usuario();
        Rol rol = new Rol();
        rol.setNombre("Cliente");
        user.setRol(rol);

        when(repoU.insertarCliente(user)).thenReturn(true);

        assertTrue(servicio.registrarUsuario(user, ""));
    }
}
