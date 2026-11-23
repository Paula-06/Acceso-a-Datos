package dominio.estacion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Estación de servicio. Forma una única alta junto a su ficha de facturación y
 * debe tener al menos un surtidor. Las reglas que afectan a varios registros se
 * refuerzan aquí en memoria; en MySQL quedarán recogidas en el diseño y algunas
 * se completarán en la fase de persistencia.
 */
public class Estacion {

    private Integer id;                    // null hasta que MySQL asigne el autoincremental
    private final String codigoInterno;    // referencia de negocio, única
    private String nombre;
    private String direccion;
    private String telefono;
    private LocalDate fechaApertura;
    private FichaFacturacion ficha;
    private final List<Surtidor> surtidores = new ArrayList<>();

    public Estacion(Integer id, String codigoInterno, String nombre, String direccion,
                    String telefono, LocalDate fechaApertura, FichaFacturacion ficha) {
        if (codigoInterno == null || codigoInterno.isBlank()) {
            throw new IllegalArgumentException("El código interno de la estación es obligatorio.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la estación es obligatorio.");
        }
        if (fechaApertura == null) {
            throw new IllegalArgumentException("La fecha de apertura es obligatoria.");
        }
        this.id = id;
        this.codigoInterno = codigoInterno;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.fechaApertura = fechaApertura;
        this.ficha = ficha;
        if (ficha != null) {
            ficha.enlazar(this);
        }
    }

    /** Alta conjunta: estación y ficha se crean y enlazan en el mismo constructor. */
    public FichaFacturacion getFicha() {
        return ficha;
    }

    public void anyadirSurtidor(Surtidor surtidor) {
        if (surtidor == null) {
            throw new IllegalArgumentException("El surtidor no puede ser nulo.");
        }
        if (surtidor.getEstacion() != null && surtidor.getEstacion() != this) {
            throw new IllegalStateException("Ese equipo ya pertenece a otra estación.");
        }
        for (Surtidor existente : surtidores) {
            if (existente.getNumeroVisible() == surtidor.getNumeroVisible()) {
                throw new IllegalArgumentException("Ya existe el número visible " + surtidor.getNumeroVisible() + " en esta estación.");
            }
        }
        surtidor.enlazar(this);
        surtidores.add(surtidor);
    }

    /** No se permite dejar la estación sin ningún surtidor registrado. */
    public void retirarSurtidor(Surtidor surtidor) {
        if (surtidores.size() <= 1) {
            throw new IllegalStateException("Una estación no puede quedarse sin surtidores registrados.");
        }
        if (!surtidores.remove(surtidor)) {
            throw new IllegalArgumentException("El surtidor no pertenece a esta estación.");
        }
    }

    public List<Surtidor> getSurtidores() {
        return Collections.unmodifiableList(surtidores);
    }

    public Integer getId() {
        return id;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public LocalDate getFechaApertura() {
        return fechaApertura;
    }

    @Override
    public String toString() {
        return codigoInterno + " - " + nombre;
    }
}
