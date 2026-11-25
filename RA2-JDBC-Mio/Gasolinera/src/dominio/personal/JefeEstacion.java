package dominio.personal;

/** Jefe de estación: nivel de autorización para operaciones excepcionales. */
public final class JefeEstacion extends Funcion {

    private final int nivelAutorizacion;

    public JefeEstacion(int nivelAutorizacion) {
        super("JEFE_ESTACION", "Jefe de estación");
        if (nivelAutorizacion < 1 || nivelAutorizacion > 3) {
            throw new IllegalArgumentException("El nivel de autorización debe estar entre 1 y 3.");
        }
        this.nivelAutorizacion = nivelAutorizacion;
    }

    public int getNivelAutorizacion() {
        return nivelAutorizacion;
    }
}
