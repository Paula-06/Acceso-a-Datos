package dominio.producto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representación en Java de una entrada del histórico que MySQL genera con un
 * trigger. No se crea desde la aplicación: existe para futuras consultas JDBC.
 */
public class HistorialPrecio {

    private Integer id;
    private final Producto producto;
    private final BigDecimal precioAnterior;
    private final BigDecimal precioNuevo;
    private final LocalDateTime fechaCambio;

    public HistorialPrecio(Integer id, Producto producto, BigDecimal precioAnterior,
                           BigDecimal precioNuevo, LocalDateTime fechaCambio) {
        if (producto == null || precioAnterior == null || precioNuevo == null || fechaCambio == null) {
            throw new IllegalArgumentException("Faltan datos del cambio de precio.");
        }
        this.id = id;
        this.producto = producto;
        this.precioAnterior = precioAnterior;
        this.precioNuevo = precioNuevo;
        this.fechaCambio = fechaCambio;
    }

    public Integer getId() {
        return id;
    }

    public Producto getProducto() {
        return producto;
    }

    public BigDecimal getPrecioAnterior() {
        return precioAnterior;
    }

    public BigDecimal getPrecioNuevo() {
        return precioNuevo;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    @Override
    public String toString() {
        return producto.getReferencia() + ": " + precioAnterior + " -> " + precioNuevo + " (" + fechaCambio + ")";
    }
}
