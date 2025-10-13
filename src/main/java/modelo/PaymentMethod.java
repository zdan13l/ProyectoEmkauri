package modelo;

public enum PaymentMethod {
    VISA, MASTERCARD, AMEX, PSE, OTRO;

    public static PaymentMethod fromString(String s) {
        if (s == null) return OTRO;
        try { return PaymentMethod.valueOf(s.trim().toUpperCase()); }
        catch (Exception e) { return OTRO; }
    }
}
