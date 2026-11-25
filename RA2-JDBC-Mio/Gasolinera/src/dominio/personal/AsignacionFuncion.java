package dominio.personal;

import java.time.LocalDate;

/**
 * Periodo en el que una persona desempeña una función con unas condiciones
 * concretas. Alta, baja y reincorporación se registran como filas distintas;
 * el historial nunca se reescribe.
 */
public class AsignacionFuncion {

    private Integer id;
    private final Empleado empleado;
    private final Funcion funcion;
    private final LocalDate fechaInicio;
    private LocalDate fechaFin;             // null = sigue vigente
    private final String condiciones;

    public AsignacionFuncion(Integer id, Empleado empleado, Funcion funcion,
                             LocalDate fechaInicio, LocalDate fechaFin, String condiciones) {
        if (empleado == null || funcion == null || fechaInicio == null) {
            throw new IllegalArgumentException("Empleado, función y fecha de inicio son obligatorios.");
        }
        if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio.");
        }
        this.id = id;
        this.empleado = empleado;
        this.funcion = funcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.condiciones = condiciones;
    }

    public boolean activaEn(LocalDate fecha) {
        return !fecha.isBefore(fechaInicio) && (fechaFin == null || !fecha.isAfter(fechaFin));
    }

    /** Solapa con un periodo abierto que empieza ese día. */
    public boolean solapaCon(LocalDate inicioNuevo) {
        return fechaFin == null || fechaFin.isAfter(inicioNuevo);
    }

    public void retirar(LocalDate fecha) {
        if (fecha.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La baja no puede ser anterior al inicio.");
        }
        this.fechaFin = fecha;
    }

    public Integer getId() {
        return id;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public String getCondiciones() {
        return condiciones;
    }

    @Override
    public String toString() {
        return funcion + " [" + fechaInicio + " - " + (fechaFin == null ? "actualidad" : fechaFin) + "]";
    }
}
