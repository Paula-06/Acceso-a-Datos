import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilidad proporcionada para objetos CSV planos con valores escalares.
 * No es un parser CSV de proposito general: el esquema del examen no usa
 * arrays ni objetos anidados. No abre ni escribe ficheros.
 */
final class CSVPlano {
    private static final Pattern NUMERO = Pattern.compile(
            "-?(?:0|[1-9][0-9]*)(?:\\.[0-9]+)?(?:[eE][+-]?[0-9]+)?");
    private final String texto;
    private int posicion;

    private CSVPlano(String texto) {
        this.texto = texto;
    }

    static Map<String, Object> leer(String texto) throws FormatoCSVIncorrectoException {
        return new CSVPlano(texto).objeto();
    }

    private Map<String, Object> objeto() throws FormatoCSVIncorrectoException {
        Map<String, Object> propiedades = new HashMap<>();
        esperar('{');
        if (!aceptar('}')) {
            do {
                blancos();
                String clave = cadena();
                esperar(':');
                Object valor = valor();
                if (propiedades.containsKey(clave)) {
                    throw error("Propiedad CSV repetida: " + clave);
                }
                propiedades.put(clave, valor);
                if (aceptar('}')) {
                    break;
                }
                esperar(',');
            } while (true);
        }
        blancos();
        if (posicion != texto.length()) {
            throw error("Contenido adicional tras el objeto CSV");
        }
        return propiedades;
    }

    private Object valor() throws FormatoCSVIncorrectoException {
        blancos();
        if (posicion < texto.length() && texto.charAt(posicion) == '"') {
            return cadena();
        }
        for (String literal : new String[]{"true", "false", "null"}) {
            if (texto.startsWith(literal, posicion)) {
                posicion += literal.length();
                return literal.equals("null") ? null : Boolean.valueOf(literal);
            }
        }
        Matcher numero = NUMERO.matcher(texto);
        numero.region(posicion, texto.length());
        if (numero.lookingAt()) {
            posicion = numero.end();
            try {
                return new BigDecimal(numero.group());
            } catch (NumberFormatException e) {
                throw error("Número CSV fuera del rango soportado");
            }
        }
        throw error("Se esperaba un valor escalar CSV");
    }

    private String cadena() throws FormatoCSVIncorrectoException {
        esperar('"');
        StringBuilder resultado = new StringBuilder();
        while (posicion < texto.length()) {
            char caracter = texto.charAt(posicion++);
            if (caracter == '"') {
                return resultado.toString();
            }
            if (caracter < 0x20) {
                throw error("Carácter de control sin escapar");
            }
            if (caracter != '\\') {
                resultado.append(caracter);
                continue;
            }
            if (posicion == texto.length()) {
                throw error("Escape incompleto");
            }
            char escape = texto.charAt(posicion++);
            switch (escape) {
                case '"', '\\', '/' -> resultado.append(escape);
                case 'b' -> resultado.append('\b');
                case 'f' -> resultado.append('\f');
                case 'n' -> resultado.append('\n');
                case 'r' -> resultado.append('\r');
                case 't' -> resultado.append('\t');
                case 'u' -> {
                    if (posicion + 4 > texto.length()) {
                        throw error("Escape Unicode incompleto");
                    }
                    String hexadecimal = texto.substring(posicion, posicion + 4);
                    if (!hexadecimal.matches("[0-9a-fA-F]{4}")) {
                        throw error("Escape Unicode incorrecto");
                    }
                    resultado.append((char) Integer.parseInt(hexadecimal, 16));
                    posicion += 4;
                }
                default -> throw error("Escape CSV desconocido");
            }
        }
        throw error("Cadena CSV sin cerrar");
    }

    private boolean aceptar(char esperado) {
        blancos();
        if (posicion < texto.length() && texto.charAt(posicion) == esperado) {
            posicion++;
            return true;
        }
        return false;
    }

    private void esperar(char esperado) throws FormatoCSVIncorrectoException {
        if (!aceptar(esperado)) {
            throw error("Se esperaba '" + esperado + "'");
        }
    }

    private void blancos() {
        while (posicion < texto.length()
                && " \t\r\n".indexOf(texto.charAt(posicion)) >= 0) {
            posicion++;
        }
    }

    private FormatoCSVIncorrectoException error(String mensaje) {
        return new FormatoCSVIncorrectoException(
                mensaje + " (posición " + (posicion + 1) + ")");
    }

    static String citar(String texto) {
        StringBuilder resultado = new StringBuilder("\"");
        for (int i = 0; i < texto.length(); i++) {
            char caracter = texto.charAt(i);
            switch (caracter) {
                case '"' -> resultado.append("\\\"");
                case '\\' -> resultado.append("\\\\");
                case '\n' -> resultado.append("\\n");
                case '\r' -> resultado.append("\\r");
                case '\t' -> resultado.append("\\t");
                default -> {
                    if (caracter < 0x20) {
                        resultado.append(String.format("\\u%04x", (int) caracter));
                    } else {
                        resultado.append(caracter);
                    }
                }
            }
        }
        return resultado.append('"').toString();
    }
}