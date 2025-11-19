package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modelo.Compra;
import modelo.Pago;
import modelo.Producto;
import servicio.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

// Controlador para gestionar la pantalla de pago y comprobante.
public class PagoController {

    // Elementos de la interfaz gráfica.
    @FXML private TextField txtTitular;
    @FXML private TextField txtNumeroTarjeta;
    @FXML private TextField txtMes;
    @FXML private TextField txtAnio;
    @FXML private PasswordField txtCVV;
    @FXML private TextField txtCorreo;
    @FXML private Label lblTotalPago;
    @FXML private Label lblNombreCliente;
    @FXML private Label lblCorreoCliente;
    @FXML private Label lblTotalPagado;
    @FXML private Label lblFecha;
    @FXML private Label lblCodigoTransaccion;
    @FXML private Button btnVolverInicio;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Button btnConfirmarPago;

    // Servicios para la lógica y gestor de navegación.
    private final ISPago servicioPago;
    private final ISCompra servicioCompra;
    private final GestorPantallas gestorPantallas;

    // Pago actual procesado.
    private Pago pagoActual;

    // Constructor que recibe los servicios necesarios.
    public PagoController(ISPago servicioPago, ISCompra servicioCompra, GestorPantallas gestorPantallas) {
        this.servicioPago = servicioPago;
        this.servicioCompra = servicioCompra;
        this.gestorPantallas = gestorPantallas;
    }

    // Inicialización de la pantalla.
    @FXML
    public void initialize() {
        if (lblTotalPago != null) {
            lblTotalPago.setText(String.format("$ %.2f", obtenerTotalCarrito()));
        } else if (lblFecha != null) {
            inicializarComprobante();
        }
    }

    // Maneja la acción de cancelar el pago.
    @FXML
    public void handleCancelar(ActionEvent event) { gestorPantallas.irCarrito(); }

    // Maneja la acción de confirmar el pago.
    @FXML
    public void handleConfirmarPago(ActionEvent event) {
        if (txtTitular == null) { return; }
        if (!validarCamposPago()) { return; }

        double total = obtenerTotalCarrito();

        // Crear el pago.
        pagoActual = crearPago(total);
        if (pagoActual == null) { return; }

        // Registrar la compra asociada al pago.
        if (!registrarCompra(total)) { return; }

        // Navegar a la pantalla de comprobante.
        SesionActual.setPagoActual(pagoActual);
        gestorPantallas.irComprobante();
    }

    // Maneja la acción de guardar el comprobante en un archivo JSON.
    @FXML
    public void handleGuardar(ActionEvent event) {
        if (pagoActual == null) {
            gestorPantallas.mostrarAlerta("Error", "No hay un pago registrado para guardar.");
            return;
        }

        // Crear el contenido JSON del comprobante.
        String json = "{" + "\"codigo\":\"" + escape(pagoActual.getCodigo()) + "\"," + "\"monto\":" + pagoActual.getMonto()
                + "," + "\"fecha\":\"" + escape(pagoActual.getFecha().toString()) + "\"" + "}";

        try {
            Path carpeta = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "comprobantes");
            Files.createDirectories(carpeta);

            Path destino = carpeta.resolve("comprobante_" + pagoActual.getCodigo() + ".json");
            Files.writeString(destino, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            gestorPantallas.mostrarExito("Guardado exitoso", "Comprobante guardado correctamente en:\n" + destino.toAbsolutePath());
        } catch (IOException e) {
            gestorPantallas.mostrarError("Error", "No se pudo guardar el comprobante:\n" + e.getMessage());
        }
    }

    // Maneja la acción de volver al inicio.
    @FXML
    public void handleVolverInicio(ActionEvent event) {
        gestorPantallas.irCliente();
        SesionActual.vaciarCarrito();
    }

    // Calcula el total del carrito actual.
    private double obtenerTotalCarrito() {
        return SesionActual.getCarrito().stream()
                .mapToDouble(Producto::getPrecio)
                .sum();
    }

    // Válida los campos del formulario de pago.
    private boolean validarCamposPago() {
        // Validar campos obligatorios.
        if (txtTitular.getText().isEmpty() || txtNumeroTarjeta.getText().isEmpty() || txtMes.getText().isEmpty() || txtAnio.getText().isEmpty()
                || txtCVV.getText().isEmpty() || txtCorreo.getText().isEmpty()) {
            gestorPantallas.mostrarAlerta("Campos incompletos", "Por favor completa todos los datos del pago.");
            return false;
        }

        // Validar nombre del titular.
        String regexTitular = "^[a-zA-ZÀ-ÿ\\s]+$";
        if (!txtTitular.getText().matches(regexTitular)) {
            gestorPantallas.mostrarAlerta("Formato inválido", "El nombre del titular debe contener solo letras y espacios.");
            return false;
        }

        // Validar número de tarjeta (16 dígitos).
        String regexTarjeta = "^\\d{16}$";
        if (!txtNumeroTarjeta.getText().matches(regexTarjeta)) {
            gestorPantallas.mostrarAlerta("Formato inválido", "El número de tarjeta debe contener 16 dígitos.");
            return false;
        }

        // Validar mes (01-12) y año (actual o futuro).
        String regexMes = "^(0[1-9]|1[0-2])$";
        if (!txtMes.getText().matches(regexMes)) {
            gestorPantallas.mostrarAlerta("Formato inválido", "El mes debe estar entre 01 y 12.");
            return false;
        }

        String regexAnio = "^(20\\d{2})$";
        int anioActual = LocalDateTime.now().getYear();
        if (!txtAnio.getText().matches(regexAnio) || Integer.parseInt(txtAnio.getText()) < anioActual) {
            gestorPantallas.mostrarAlerta("Formato inválido", "El año debe ser el actual o un año futuro.");
            return false;
        }

        // Validar CVV (3 dígitos).
        String regexCVV = "^\\d{3}$";
        if (!txtCVV.getText().matches(regexCVV)) {
            gestorPantallas.mostrarAlerta("Formato inválido", "El CVV debe contener 3 dígitos.");
            return false;
        }

        // Validar formato del correo electrónico.
        String regexCorreo = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
        if (!txtCorreo.getText().matches(regexCorreo)) {
            gestorPantallas.mostrarAlerta("Formato inválido", "El correo electrónico no tiene un formato válido.");
            return false;
        }

        // Todos los campos son válidos.
        return true;
    }

    // Crea un objeto Pago con los datos proporcionados.
    private Pago crearPago(double total) {
        Pago pago = new Pago();
        pago.setCodigo(UUID.randomUUID().toString().substring(0, 8));
        pago.setMonto(total);
        pago.setMetodo("Tarjeta");
        pago.setFecha(new Date());

        if (!servicioPago.crearPago(pago)) {
            gestorPantallas.mostrarError("Error", "No se pudo procesar el pago.");
            return null;
        }
        return pago;
    }

    //
    private boolean registrarCompra(double total) {
        Compra compra = new Compra();
        compra.setCliente(SesionActual.getUsuarioActual());
        compra.setProductos(new ArrayList<>(SesionActual.getCarrito()));
        compra.setMontoFinal(total);
        compra.setPago(pagoActual);

        // Guardar la compra usando el servicio.
        if (!servicioCompra.crearCompra(compra)) {
            gestorPantallas.mostrarError("Error", "No se pudo registrar la compra.");
            return false;
        }
        return true;
    }

    // Inicializa los datos del comprobante.
    private void inicializarComprobante() {
        pagoActual = SesionActual.getPagoActual();

        // Verificar que haya un pago registrado.
        if (pagoActual == null) {
            gestorPantallas.mostrarError("Error", "No hay un pago registrado para mostrar el comprobante.");
            return;
        }

        // Mostrar datos del cliente.
        if (SesionActual.getUsuarioActual() != null) {
            lblNombreCliente.setText("Nombre: " + SesionActual.getUsuarioActual().getDatosPersonales().getNombre());
            lblCorreoCliente.setText("Correo: " + SesionActual.getUsuarioActual().getCorreo());
        }

        // Mostrar detalles del pago.
        double total = pagoActual.getMonto();
        lblTotalPagado.setText("Total Pagado: $" + String.format("%.2f", total));
        lblFecha.setText("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(pagoActual.getFecha()));
        lblCodigoTransaccion.setText("Código de transacción: #" + pagoActual.getCodigo());
    }

    // Escapa caracteres especiales en una cadena para JSON.
    private String escape(String s) {
        if (s == null) {
            return "";
        } else {
            return s.replace("\\", "\\\\").replace("\"", "\\\"");
        }
    }
}
