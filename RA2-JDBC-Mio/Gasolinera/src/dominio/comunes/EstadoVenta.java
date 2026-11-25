package dominio.comunes;

/** Estado del cobro de una venta: admite cobros parciales hasta confirmarse. */
public enum EstadoVenta {
    PENDIENTE("Pendiente de cobro"),
    PAGADA("Pagada");

    private final String texto;

    EstadoVenta(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return texto;
    }
}
