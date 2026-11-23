package dominio.cliente;

import java.time.LocalDate;

/** Tarjeta de fidelización: puede estar en stock (sin titular) o asignada a uno. */
public class TarjetaFidelizacion {

    private Integer id;
    private final String numero;
    private final LocalDate fechaEmision;
    private LocalDate fechaAsignacion;
    private Cliente titular;

    /** Alta de un lote recibido pero todavía no entregado. */
    public TarjetaFidelizacion(Integer id, String numero, LocalDate fechaEmision) {
        if (numero == null || numero.isBlank() || fechaEmision == null) {
            throw new IllegalArgumentException("Número y fecha de emisión son obligatorios.");
        }
        this.id = id;
        this.numero = numero;
        this.fechaEmision = fechaEmision;
    }

    public void asignarA(Cliente cliente, LocalDate fecha) {
        if (cliente == null || fecha == null) {
            throw new IllegalArgumentException("Cliente y fecha de asignación son obligatorios.");
        }
        if (titular != null) {
            throw new IllegalStateException("Esa tarjeta ya tiene titular.");
        }
        if (cliente.getTarjeta() != null) {
            throw new IllegalStateException("Ese cliente ya tiene una tarjeta.");
        }
        this.titular = cliente;
        this.fechaAsignacion = fecha;
        cliente.enlazarTarjeta(this);
    }

    public boolean estaAsignada() {
        return titular != null;
    }

    public Integer getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    public Cliente getTitular() {
        return titular;
    }

    @Override
    public String toString() {
        return numero + (estaAsignada() ? " (" + titular + ")" : " (sin asignar)");
    }
}
