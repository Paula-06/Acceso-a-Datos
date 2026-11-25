package dominio.personal;

import java.math.BigDecimal;

/** Cajero: límite autorizado para devoluciones. */
public final class Cajero extends Funcion {

    private final BigDecimal limiteDevoluciones;

    public Cajero(BigDecimal limiteDevoluciones) {
        super("CAJERO", "Cajero");
        if (limiteDevoluciones == null || limiteDevoluciones.signum() <= 0) {
            throw new IllegalArgumentException("El límite de devoluciones debe ser mayor que cero.");
        }
        this.limiteDevoluciones = limiteDevoluciones;
    }

    public BigDecimal getLimiteDevoluciones() {
        return limiteDevoluciones;
    }
}
