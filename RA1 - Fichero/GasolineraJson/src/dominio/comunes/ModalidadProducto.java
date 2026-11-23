package dominio.comunes;

import java.math.BigDecimal;

/**
 * Modalidad estable de venta del producto. Cada constante encapsula cómo se
 * valida la cantidad y cómo se calcula el importe, de forma que LineaVenta y
 * Devolucion no repitan decisiones sobre "unidades" frente a "kilogramos".
 */
public enum ModalidadProducto {

    ENAVASADO {
        @Override
        public void validarCantidad(BigDecimal cantidad, int decimalesPrecision) {
            Dinero.exigirPositivo(cantidad, "La cantidad");
            // 1,5 no es válida aunque quepa en una columna DECIMAL.
            if (cantidad.stripTrailingZeros().scale() > 0) {
                throw new IllegalArgumentException("Los productos envasados se venden por unidades completas.");
            }
        }

        @Override
        public String unidad() {
            return "unidades";
        }
    },

    GRANEL {
        @Override
        public void validarCantidad(BigDecimal cantidad, int decimalesPrecision) {
            Dinero.exigirPositivo(cantidad, "La cantidad");
            if (cantidad.stripTrailingZeros().scale() > decimalesPrecision) {
                throw new IllegalArgumentException(
                        "La cantidad supera la precisión de pesada admitida (" + decimalesPrecision + " decimales).");
            }
        }

        @Override
        public String unidad() {
            return "kg";
        }
    };

    public abstract void validarCantidad(BigDecimal cantidad, int decimalesPrecision);

    public abstract String unidad();

    public final BigDecimal calcularImporte(BigDecimal cantidad, BigDecimal precioUnitario) {
        return Dinero.calcularImporte(cantidad, precioUnitario);
    }
}
