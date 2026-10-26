
/** Sintaxis CSV incorrecta o propiedades que no corresponden al esquema. */
public final class FormatoCSVIncorrectoException extends Exception {
    public FormatoCSVIncorrectoException(String mensaje) {
        super(mensaje);
    }
}