package dominio.incidencia;

import dominio.comunes.EstadoIncidencia;
import dominio.estacion.Estacion;
import dominio.personal.Empleado;

import java.time.LocalDateTime;

/** Incidencia de una estación: solo se conserva el responsable actual. */
public class Incidencia {

    private Integer id;
    private final String referencia;
    private final Estacion estacion;
    private final String descripcion;
    private final LocalDateTime fechaApertura;
    private EstadoIncidencia estado;
    private Empleado responsable;

    public Incidencia(Integer id, String referencia, Estacion estacion, String descripcion,
                      LocalDateTime fechaApertura) {
        if (referencia == null || referencia.isBlank() || estacion == null
                || descripcion == null || descripcion.isBlank() || fechaApertura == null) {
            throw new IllegalArgumentException("Faltan datos obligatorios de la incidencia.");
        }
        this.id = id;
        this.referencia = referencia;
        this.estacion = estacion;
        this.descripcion = descripcion;
        this.fechaApertura = fechaApertura;
        this.estado = EstadoIncidencia.ABIERTA;
    }

    public void asignar(Empleado responsable) {
        if (responsable == null) {
            throw new IllegalArgumentException("El responsable no puede ser nulo.");
        }
        this.responsable = responsable;
        if (estado == EstadoIncidencia.ABIERTA) {
            this.estado = EstadoIncidencia.EN_CURSO;
        }
    }

    public void cambiarEstado(EstadoIncidencia nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }
        this.estado = nuevoEstado;
    }

    public Integer getId() {
        return id;
    }

    public String getReferencia() {
        return referencia;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public EstadoIncidencia getEstado() {
        return estado;
    }

    public Empleado getResponsable() {
        return responsable;
    }

    @Override
    public String toString() {
        return referencia + " - " + descripcion + " (" + estado
                + (responsable == null ? ", sin asignar" : ", " + responsable) + ")";
    }
}
