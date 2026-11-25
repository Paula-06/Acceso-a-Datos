package dominio.producto;

import dominio.comunes.ModalidadProducto;

import java.math.BigDecimal;

/** Producto cobrado por kilogramos: conserva la precisión de pesada admitida. */
public final class ProductoGranel extends Producto {

    private final int precisionPesada;

    public ProductoGranel(Integer id, String referencia, String descripcion,
                          BigDecimal precioVenta, int precisionPesada) {
        super(id, referencia, descripcion, precioVenta);
        if (precisionPesada < 0 || precisionPesada > 3) {
            throw new IllegalArgumentException("La precisión de pesada debe estar entre 0 y 3 decimales.");
        }
        this.precisionPesada = precisionPesada;
    }

    @Override
    public ModalidadProducto modalidad() {
        return ModalidadProducto.GRANEL;
    }

    @Override
    public int decimalesPrecision() {
        return precisionPesada;
    }

    public int getPrecisionPesada() {
        return precisionPesada;
    }
}
