package dominio.comunes;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Criterio común de redondeo monetario para toda la aplicación:
 * dos decimales y HALF_UP. Evita que cada operación decidía su propio redondeo.
 * Se usa tanto en precios como en importes de líneas, cobros y devoluciones.
 */
public final class Dinero {

    public static final int ESCALA = 2;

    private Dinero() {
    }

    public static BigDecimal redondear(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("El importe no puede ser nulo.");
        }
        return valor.setScale(ESCALA, RoundingMode.HALF_UP);
    }

    /** Calcula el importe de una línea o devolución con el criterio común. */
    public static BigDecimal calcularImporte(BigDecimal cantidad, BigDecimal precioUnitario) {
        exigirPositivo(cantidad, "La cantidad");
        exigirPositivo(precioUnitario, "El precio unitario");
        return redondear(cantidad.multiply(precioUnitario));
    }

    public static void exigirPositivo(BigDecimal valor, String nombre) {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException(nombre + " debe ser mayor que cero.");
        }
    }
}
