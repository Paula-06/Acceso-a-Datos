package dominio.venta;

import dominio.comunes.Dinero;
import dominio.comunes.MedioPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cobro de tienda: pertenece a una única venta. Se distingue del Pago de
 * repostajes de la práctica anterior, que sigue en CSV/JSON con sus propios
 * campos (litros y combustible) sin reinterpretación.
 */
public class Pago {

    private Integer id;
    private Venta venta;
    private final BigDecimal importe;
    private final LocalDateTime fechaHora;
    private final MedioPago medioPago;
    private final String referencia;

    public Pago(Integer id, BigDecimal importe, LocalDateTime fechaHora,
                MedioPago medioPago, String referencia) {
        Dinero.exigirPositivo(importe, "El importe del pago");
        if (fechaHora == null || medioPago == null) {
            throw new IllegalArgumentException("Fecha y medio de pago son obligatorios.");
        }
        this.id = id;
        this.importe = Dinero.redondear(importe);
        this.fechaHora = fechaHora;
        this.medioPago = medioPago;
        this.referencia = referencia;
    }

    void enlazar(Venta venta) {
        this.venta = venta;
    }

    public Integer getId() {
        return id;
    }

    public Venta getVenta() {
        return venta;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public String getReferencia() {
        return referencia;
    }

    @Override
    public String toString() {
        return medioPago + ": " + importe + " €";
    }
}
