package dominio.personal;

/**
 * Función que puede desempeñar un empleado. Es abstracta porque cada función
 * tiene condiciones específicas propias; añadir una nueva no obliga a tocar
 * Empleado ni AsignacionFuncion.
 */
public abstract class Funcion {

    private final String codigo;
    private final String descripcion;

    protected Funcion(String codigo, String descripcion) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de la función es obligatorio.");
        }
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
