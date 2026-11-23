package dominio.cliente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Campaña promocional; en esta fase solo se gestiona la inscripción. */
public class Campana {

    private Integer id;
    private final String codigo;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private final List<Inscripcion> inscripciones = new ArrayList<>();

    public Campana(Integer id, String codigo, String nombre, String descripcion,
                   LocalDate fechaInicio, LocalDate fechaFin) {
        if (codigo == null || codigo.isBlank() || nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Código y nombre de la campaña obligatorios.");
        }
        if (fechaInicio == null || fechaFin == null || fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio.");
        }
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public Inscripcion inscribir(Cliente cliente, LocalDate fecha) {
        if (cliente == null || fecha == null) {
            throw new IllegalArgumentException("Cliente y fecha de inscripción obligatorios.");
        }
        if (fecha.isBefore(fechaInicio) || fecha.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La inscripción debe estar dentro del periodo de la campaña.");
        }
        for (Inscripcion i : inscripciones) {
            if (i.getCliente() == cliente) {
                throw new IllegalStateException("Ese cliente ya está inscrito en esta campaña.");
            }
        }
        Inscripcion nueva = new Inscripcion(null, this, cliente, fecha);
        inscripciones.add(nueva);
        cliente.anyadirInscripcion(nueva);
        return nueva;
    }

    public List<Inscripcion> getInscripciones() {
        return Collections.unmodifiableList(inscripciones);
    }

    public Integer getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
