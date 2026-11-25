package dominio.cliente;

import java.time.LocalDate;

/** Participación de un cliente en una campaña; fecha de alta incluida. */
public class Inscripcion {

    private Integer id;
    private final Campana campana;
    private final Cliente cliente;
    private final LocalDate fecha;

    public Inscripcion(Integer id, Campana campana, Cliente cliente, LocalDate fecha) {
        if (campana == null || cliente == null || fecha == null) {
            throw new IllegalArgumentException("Campaña, cliente y fecha son obligatorios.");
        }
        this.id = id;
        this.campana = campana;
        this.cliente = cliente;
        this.fecha = fecha;
    }

    public Integer getId() {
        return id;
    }

    public Campana getCampana() {
        return campana;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    @Override
    public String toString() {
        return cliente + " en " + campana + " (" + fecha + ")";
    }
}
