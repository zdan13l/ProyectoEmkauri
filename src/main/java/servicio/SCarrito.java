package servicio;

import modelo.ItemCarrito;
import java.util.*;

public class SCarrito {
    private static final SCarrito instance = new SCarrito();
    public static SCarrito getInstance() { return instance; }

    private final List<ItemCarrito> carrito = new ArrayList<>();

    private SCarrito() {}

    public void agregarItem(ItemCarrito item) {
        Optional<ItemCarrito> existente = carrito.stream()
                .filter(i -> i.getId() == item.getId() && i.getTipo().equals(item.getTipo()))
                .findFirst();
        if (existente.isPresent()) {
            existente.get().setCantidad(existente.get().getCantidad() + item.getCantidad());
        } else {
            carrito.add(item);
        }
    }

    public void eliminarItem(int id) {
        carrito.removeIf(i -> i.getId() == id);
    }

    public List<ItemCarrito> listar() {
        return new ArrayList<>(carrito);
    }

    public double calcularTotal() {
        return carrito.stream().mapToDouble(ItemCarrito::getSubtotal).sum();
    }

    public void vaciar() {
        carrito.clear();
    }
}
