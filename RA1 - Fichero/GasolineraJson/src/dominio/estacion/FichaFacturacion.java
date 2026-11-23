package dominio.estacion;

/** Configuración de emisión de tickets de una estación (relación 1:1). */
public class FichaFacturacion {

    private Integer id;
    private final String codigoConfiguracion;  // lo usa soporte para identificar la ficha
    private final String serieTickets;
    private long siguienteNumero;
    private String textoPie;
    private Estacion estacion;

    public FichaFacturacion(Integer id, String codigoConfiguracion, String serieTickets,
                            long siguienteNumero, String textoPie) {
        if (codigoConfiguracion == null || codigoConfiguracion.isBlank()) {
            throw new IllegalArgumentException("El código de configuración es obligatorio.");
        }
        if (serieTickets == null || serieTickets.isBlank()) {
            throw new IllegalArgumentException("La serie de tickets es obligatoria.");
        }
        if (siguienteNumero <= 0) {
            throw new IllegalArgumentException("El siguiente número de ticket debe ser positivo.");
        }
        this.id = id;
        this.codigoConfiguracion = codigoConfiguracion;
        this.serieTickets = serieTickets;
        this.siguienteNumero = siguienteNumero;
        this.textoPie = textoPie;
    }

    void enlazar(Estacion estacion) {
        this.estacion = estacion;
    }

    /** Reserva y consume el siguiente número de la serie. */
    public String siguienteNumeroTicket() {
        return serieTickets + "-" + (siguienteNumero++);
    }

    public Integer getId() {
        return id;
    }

    public String getCodigoConfiguracion() {
        return codigoConfiguracion;
    }

    public String getSerieTickets() {
        return serieTickets;
    }

    public long getSiguienteNumero() {
        return siguienteNumero;
    }

    public String getTextoPie() {
        return textoPie;
    }

    public Estacion getEstacion() {
        return estacion;
    }
}
