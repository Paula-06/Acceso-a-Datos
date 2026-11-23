package dominio.venta;

import dominio.comunes.Dinero;
import dominio.producto.Producto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Línea de ticket: guarda la cantidad, el precio realmente cobrado y el importe.
 * El precio del catálogo puede cambiar después sin alterar esta línea.
 */
public class LineaVenta {

    private Integer id;
    private final Venta venta;
    private final Producto producto;
    private final int numeroOrden;
    private final BigDecimal cantidad;
    private final BigDecimal precioUnitario;
    private final BigDecimal importe;
    private final List<Devolucion> devoluciones = new ArrayList<>();

    public LineaVenta(Integer id, Venta venta, Producto producto, int numeroOrden, BigDecimal cantidad) {
        if (venta == null || producto == null || numeroOrden <= 0) {
            throw new IllegalArgumentException("Faltan datos obligatorios de la línea.");
        }
        producto.validarCantidad(cantidad);
        this.id = id;
        this.venta = venta;
        this.producto = producto;
        this.numeroOrden = numeroOrden;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecioVenta();
        this.importe = producto.calcularImporte(cantidad);
    }

    void anyadirDevolucion(Devolucion devolucion) {
        devoluciones.add(devolucion);
    }

    public BigDecimal cantidadDevuelta() {
        return devoluciones.stream().map(Devolucion::getCantidad)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal importeDevuelto() {
        return devoluciones.stream().map(Devolucion::getImporteReintegrado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal cantidadDisponible() {
        return cantidad.subtract(cantidadDevuelta());
    }

    public BigDecimal importeDisponible() {
        return importe.subtract(importeDevuelto());
    }

    public List<Devolucion> getDevoluciones() {
        return Collections.unmodifiableList(devoluciones);
    }

    public Integer getId() {
        return id;
    }

    public Venta getVenta() {
        return venta;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getNumeroOrden() {
        return numeroOrden;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    @Override
    public String toString() {
        return numeroOrden + ". " + producto.getReferencia() + " x " + cantidad
                + " " + producto.modalidad().unidad() + " @ " + precioUnitario + " = "
                + Dinero.redondear(importe) + " €";
    }
}
