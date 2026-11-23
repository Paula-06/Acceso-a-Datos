package dominio.comunes;

/** Un producto retirado sigue siendo identificable en tickets antiguos. */
public enum EstadoProducto {
    ACTIVO("Activo"),
    RETIRADO("Retirado");

    private final String texto;

    EstadoProducto(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return texto;
    }
}
