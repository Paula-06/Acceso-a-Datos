package dominio.comunes;

/** Medios de pago admitidos en los cobros de tienda. */
public enum MedioPago {
    EFECTIVO("Efectivo"),
    TARJETA("Tarjeta"),
    TRANSFERENCIA("Transferencia");

    private final String texto;

    MedioPago(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return texto;
    }
}
