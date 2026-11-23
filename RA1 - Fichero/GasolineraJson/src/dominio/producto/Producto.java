package dominio.producto;

import dominio.comunes.Dinero;
import dominio.comunes.EstadoProducto;
import dominio.comunes.ModalidadProducto;

import java.math.BigDecimal;

/**
 * Producto del catálogo. La modalidad es estable y define el comportamiento de
 * validación y cálculo; las subclases solo aportan sus datos específicos.
 */
public abstract class Producto {

    private Integer id;                  // null hasta persistir
    private final String referencia;     // referencia comercial única
    private String descripcion;
    private BigDecimal precioVenta;
    private EstadoProducto estado;

    protected Producto(Integer id, String referencia, String descripcion, BigDecimal precioVenta) {
        if (referencia == null || referencia.isBlank() || descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("Referencia y descripción son obligatorias.");
        }
        Dinero.exigirPositivo(precioVenta, "El precio de venta");
        this.id = id;
        this.referencia = referencia;
        this.descripcion = descripcion;
        this.precioVenta = Dinero.redondear(precioVenta);
        this.estado = EstadoProducto.ACTIVO;
    }

    public abstract ModalidadProducto modalidad();

    /** Solo los productos a granel usan una precisión de pesada distinta de cero. */
    public int decimalesPrecision() {
        return 0;
    }

    public final void validarCantidad(BigDecimal cantidad) {
        modalidad().validarCantidad(cantidad, decimalesPrecision());
    }

    public final BigDecimal calcularImporte(BigDecimal cantidad) {
        return modalidad().calcularImporte(cantidad, precioVenta);
    }

    /** Cambio de precio: MySQL generará el historial mediante trigger, no Java. */
    public void cambiarPrecio(BigDecimal nuevoPrecio) {
        Dinero.exigirPositivo(nuevoPrecio, "El precio de venta");
        this.precioVenta = Dinero.redondear(nuevoPrecio);
    }

    public void retirar() {
        this.estado = EstadoProducto.RETIRADO;
    }

    public boolean estaActivo() {
        return estado == EstadoProducto.ACTIVO;
    }

    public Integer getId() {
        return id;
    }

    public String getReferencia() {
        return referencia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public EstadoProducto getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return referencia + " - " + descripcion + " (" + precioVenta + " € / " + modalidad().unidad() + ")";
    }
}
