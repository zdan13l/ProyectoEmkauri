package negocio;

import modelo.Compra;
import modelo.Curso;
import modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

public class NegocioEmkauri {
    private List<Usuario> usuarios;
    private List<Curso> cursos;
    private List<Compra> compras;

    public NegocioEmkauri() {
        this.usuarios = new ArrayList<>();
        this.cursos = new ArrayList<>();
        this.compras = new ArrayList<>();
    }
}
