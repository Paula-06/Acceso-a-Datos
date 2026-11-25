package dominio.personal;

import dominio.estacion.Estacion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Empleado con identidad estable: cambiar de estación, de funciones o de
 * supervisor no crea otra persona ni altera la autoría de sus operaciones.
 */
public class Empleado {

    private Integer id;
    private final String numeroInterno;
    private final String documentoIdentidad;
    private String nombre;
    private String telefono;
    private String email;
    private LocalDate fechaIncorporacion;
    private Estacion estacion;
    private Empleado supervisor;                  // solo el actual; sin historial
    private final List<AsignacionFuncion> asignaciones = new ArrayList<>();

    public Empleado(Integer id, String numeroInterno, String documentoIdentidad, String nombre,
                    String telefono, String email, LocalDate fechaIncorporacion, Estacion estacion) {
        if (numeroInterno == null || numeroInterno.isBlank()
                || documentoIdentidad == null || documentoIdentidad.isBlank()
                || nombre == null || nombre.isBlank()
                || fechaIncorporacion == null || estacion == null) {
            throw new IllegalArgumentException("Faltan datos obligatorios del empleado.");
        }
        this.id = id;
        this.numeroInterno = numeroInterno;
        this.documentoIdentidad = documentoIdentidad;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.fechaIncorporacion = fechaIncorporacion;
        this.estacion = estacion;
    }

    /** Alta de una función: se rechazan periodos solapados de la MISMA función. */
    public AsignacionFuncion asignarFuncion(Funcion funcion, LocalDate inicio, String condiciones) {
        for (AsignacionFuncion anterior : asignaciones) {
            if (anterior.getFuncion().getClass() == funcion.getClass() && anterior.solapaCon(inicio)) {
                throw new IllegalStateException("Ya hay un periodo vigente de " + funcion.getCodigo() + " en esa fecha.");
            }
        }
        AsignacionFuncion nueva = new AsignacionFuncion(null, this, funcion, inicio, null, condiciones);
        asignaciones.add(nueva);
        return nueva;
    }

    public void retirarFuncion(Class<? extends Funcion> tipo, LocalDate fecha) {
        boolean encontrada = false;
        for (AsignacionFuncion a : asignaciones) {
            if (a.getFuncion().getClass() == tipo && a.activaEn(fecha)) {
                a.retirar(fecha);
                encontrada = true;
            }
        }
        if (!encontrada) {
            throw new IllegalArgumentException("No había ninguna asignación activa de ese tipo.");
        }
    }

    public List<Funcion> funcionesActivas(LocalDate fecha) {
        List<Funcion> activas = new ArrayList<>();
        for (AsignacionFuncion a : asignaciones) {
            if (a.activaEn(fecha)) {
                activas.add(a.getFuncion());
            }
        }
        return Collections.unmodifiableList(activas);
    }

    public List<AsignacionFuncion> getAsignaciones() {
        return Collections.unmodifiableList(asignaciones);
    }

    /** Se prohíbe supervisarse y formar ciclos, directos o indirectos. */
    public void setSupervisor(Empleado supervisor) {
        if (supervisor == this) {
            throw new IllegalArgumentException("Un empleado no puede supervisarse a sí mismo.");
        }
        Set<Empleado> visitados = new HashSet<>();
        Empleado actual = supervisor;
        while (actual != null) {
            if (actual == this) {
                throw new IllegalArgumentException("La supervisión no puede formar un ciclo.");
            }
            if (!visitados.add(actual)) {
                break; // ciclo ajeno ya existente: cortamos la comprobación
            }
            actual = actual.supervisor;
        }
        this.supervisor = supervisor;
    }

    /** Cambio de adscripción: no afecta a ventas ni asignaciones pasadas. */
    public void cambiarEstacion(Estacion nueva) {
        if (nueva == null) {
            throw new IllegalArgumentException("La estación de destino es obligatoria.");
        }
        this.estacion = nueva;
    }

    public Integer getId() {
        return id;
    }

    public String getNumeroInterno() {
        return numeroInterno;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getFechaIncorporacion() {
        return fechaIncorporacion;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public Empleado getSupervisor() {
        return supervisor;
    }

    @Override
    public String toString() {
        return numeroInterno + " - " + nombre;
    }
}
