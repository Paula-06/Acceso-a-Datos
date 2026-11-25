package dominio.venta;

import dominio.cliente.Cliente;
import dominio.comunes.Dinero;
import dominio.comunes.EstadoVenta;
import dominio.comunes.MedioPago;
import dominio.estacion.Estacion;
import dominio.personal.Empleado;
import dominio.producto.Producto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Venta de tienda. Un carrito vacío no es una venta registrada: se controla al
 * confirmar. Admite cobros parciales hasta que la suma coincide con el total.
 */
public class Venta {

    private Integer id;
    private final String referenciaTicket;
    private final Estacion estacion;
    private final Empleado empleado;
    private final Cliente cliente;          // null = compra anónima
    private final LocalDateTime fechaHora;
    private EstadoVenta estado;
    private final List<LineaVenta> lineas = new ArrayList<>();
    private final List<Pago> pagos = new ArrayList<>();

    public Venta(Integer id, String referenciaTicket, Estacion estacion, Empleado empleado,
                 Cliente cliente, LocalDateTime fechaHora) {
        if (referenciaTicket == null || referenciaTicket.isBlank()
                || estacion == null || empleado == null || fechaHora == null) {
            throw new IllegalArgumentException("Faltan datos obligatorios de la venta.");
        }
        this.id = id;
        this.referenciaTicket = referenciaTicket;
        this.estacion = estacion;
        this.empleado = empleado;
        this.cliente = cliente;
        this.fechaHora = fechaHora;
        this.estado = EstadoVenta.PENDIENTE;
    }

    public LineaVenta anyadirLinea(Producto producto, BigDecimal cantidad) {
        if (estado != EstadoVenta.PENDIENTE) {
            throw new IllegalStateException("No se pueden añadir líneas a una venta pagada.");
        }
        LineaVenta linea = new LineaVenta(null, this, producto, lineas.size() + 1, cantidad);
        lineas.add(linea);
        return linea;
    }

    public Pago anyadirPago(BigDecimal importe, LocalDateTime fechaHora,
                            MedioPago medioPago, String referencia) {
        if (estado != EstadoVenta.PENDIENTE) {
            throw new IllegalStateException("La venta ya está pagada.");
        }
        Pago pago = new Pago(null, importe, fechaHora, medioPago, referencia);
        pago.enlazar(this);
        pagos.add(pago);
        return pago;
    }

    public BigDecimal importeTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (LineaVenta linea : lineas) {
            total = total.add(linea.getImporte());
        }
        return Dinero.redondear(total);
    }

    public BigDecimal importePagado() {
        BigDecimal total = BigDecimal.ZERO;
        for (Pago pago : pagos) {
            total = total.add(pago.getImporte());
        }
        return Dinero.redondear(total);
    }

    /** Confirma el cobro solo si la suma coincide exactamente con el total. */
    public void confirmarPago() {
        if (lineas.isEmpty()) {
            throw new IllegalStateException("Un carrito vacío no se puede confirmar como venta.");
        }
        if (importePagado().compareTo(importeTotal()) != 0) {
            throw new IllegalStateException("Los cobros (" + importePagado()
                    + ") no coinciden con el total (" + importeTotal() + ").");
        }
        this.estado = EstadoVenta.PAGADA;
    }

    public List<LineaVenta> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    public List<Pago> getPagos() {
        return Collections.unmodifiableList(pagos);
    }

    public Integer getId() {
        return id;
    }

    public String getReferenciaTicket() {
        return referenciaTicket;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public EstadoVenta getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return referenciaTicket + " (" + estado + ", " + importeTotal() + " €)";
    }
}
