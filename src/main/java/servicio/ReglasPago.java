package servicio;

import modelo.PagoSolicitud;
import java.util.Calendar;

public class ReglasPago {
    public static boolean cvvValido(String cvv) {
        return cvv != null && cvv.matches("\\d{3,4}");
    }
    public static boolean fechaValida(int mes, int anio) {
        if (mes < 1 || mes > 12 || anio < 2020) return false;
        Calendar hoy = Calendar.getInstance();
        int y = hoy.get(Calendar.YEAR);
        int m = hoy.get(Calendar.MONTH) + 1;
        return (anio > y) || (anio == y && mes >= m);
    }
    public static boolean solicitudBasicaValida(PagoSolicitud req) {
        return req != null
                && req.getMonto() != null
                && req.getMonto().signum() > 0
                && req.getMetodo() != null
                && req.getTitular() != null && req.getTitular().trim().length() >= 3;
    }
}
