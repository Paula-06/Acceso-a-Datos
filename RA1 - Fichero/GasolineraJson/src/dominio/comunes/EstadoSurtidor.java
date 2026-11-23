package dominio.comunes;

/** Estado de servicio de un surtidor; averiado no implica dejar de pertenecer. */
public enum EstadoSurtidor {
    OPERATIVO("Operativo"),
    AVERIADO("Averiado"),
    MANTENIMIENTO("En mantenimiento");

    private final String texto;

    EstadoSurtidor(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return texto;
    }
}
