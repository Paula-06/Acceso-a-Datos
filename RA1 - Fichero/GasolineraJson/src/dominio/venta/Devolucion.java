package dominio.venta;

import dominio.comunes.Dinero;
import dominio.personal.Empleado;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Devolución parcial de una línea ya vendida. El importe se calcula con el
 * precio cobrado en la venta, nunca con el precio vigente del catálogo.
 */
public class Devolucion {

    private Integer id;
    private final LineaVenta linea;
    private final BigDecimal cantidad;
    private final String motivo;
    private final LocalDateTime fechaHora;
    private final Empleado empleado;
    private final BigDecimal importeReintegrado;
    private final boolean aptaParaVenta;

    public Devolucion(Integer id, LineaVenta linea, BigDecimal cantidad, String motivo,
                      LocalDateTime fechaHora, Empleado empleado, boolean aptaParaVenta) {
        if (linea == null || motivo == null || motivo.isBlank()
                || fechaHora == null || empleado == null) {
            throw new IllegalArgumentException("Faltan datos obligatorios de la devolución.");
        }
        Dinero.exigirPositivo(cantidad, "La cantidad devuelta");
        linea.getProducto().validarCantidad(cantidad);
        if (cantidad.compareTo(linea.cantidadDisponible()) > 0) {
            throw new IllegalArgumentException("No quedan suficientes unidades por devolver en la línea.");
        }
        BigDecimal importe = Dinero.calcularImporte(cantidad, linea.getPrecioUnitario());
        if (importe.compareTo(linea.importeDisponible()) > 0) {
            throw new IllegalArgumentException("El reintegro superaría el importe pendiente de la línea.");
        }
        this.id = id;
        this.linea = linea;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.fechaHora = fechaHora;
        this.empleado = empleado;
        this.importeReintegrado = importe;
        this.aptaParaVenta = aptaParaVenta;
        linea.anyadirDevolucion(this);
    }

    public Integer getId() {
        return id;
    }

    public LineaVenta getLinea() {
        return linea;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public BigDecimal getImporteReintegrado() {
        return importeReintegrado;
    }

    public boolean isAptaParaVenta() {
        return aptaParaVenta;
    }

    @Override
    public String toString() {
        return "Devolución de " + cantidad + " " + linea.getProducto().modalidad().unidad()
                + " -> " + importeReintegrado + " € (" + motivo + ")";
    }
}
