package dominio.cliente;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cliente registrado. Código y nombre son obligatorios; de los medios de
 * contacto puede faltar cualquiera, pero se exige al menos uno (criterio
 * documentado: se necesita algún medio para contactar al titular).
 */
public class Cliente {

    private Integer id;
    private final String codigo;
    private String nombre;
    private String telefono;
    private String email;
    private String direccion;
    private TarjetaFidelizacion tarjeta;
    private final List<Inscripcion> inscripciones = new ArrayList<>();

    public Cliente(Integer id, String codigo, String nombre, String telefono,
                   String email, String direccion) {
        if (codigo == null || codigo.isBlank() || nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El código y el nombre del cliente son obligatorios.");
        }
        if ((telefono == null || telefono.isBlank()) && (email == null || email.isBlank())) {
            throw new IllegalArgumentException("Debe constar al menos un medio de contacto.");
        }
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    void enlazarTarjeta(TarjetaFidelizacion tarjeta) {
        this.tarjeta = tarjeta;
    }

    void anyadirInscripcion(Inscripcion inscripcion) {
        inscripciones.add(inscripcion);
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

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getDireccion() {
        return direccion;
    }

    public TarjetaFidelizacion getTarjeta() {
        return tarjeta;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
