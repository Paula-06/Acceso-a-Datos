package dominio.comunes;

/** Ciclo de una incidencia de trabajo. */
public enum EstadoIncidencia {
    ABIERTA("Abierta"),
    EN_CURSO("En curso"),
    RESUELTA("Resuelta"),
    CERRADA("Cerrada");

    private final String texto;

    EstadoIncidencia(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return texto;
    }
}
