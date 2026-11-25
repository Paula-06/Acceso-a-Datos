package dominio.comunes;

/** Tipo de combustible de los surtidores del nuevo dominio. */
public enum TipoCombustible {
    GASOLINA("Gasolina"),
    DIESEL("Diesel");

    private final String texto;

    TipoCombustible(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return texto;
    }
}
