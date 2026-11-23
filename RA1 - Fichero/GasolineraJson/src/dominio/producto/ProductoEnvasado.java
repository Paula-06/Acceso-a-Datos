package dominio.producto;

import dominio.comunes.ModalidadProducto;

import java.math.BigDecimal;

/** Producto cobrado por unidades completas: conserva el formato del envase. */
public final class ProductoEnvasado extends Producto {

    private final String formatoEnvase;

    public ProductoEnvasado(Integer id, String referencia, String descripcion,
                            BigDecimal precioVenta, String formatoEnvase) {
        super(id, referencia, descripcion, precioVenta);
        if (formatoEnvase == null || formatoEnvase.isBlank()) {
            throw new IllegalArgumentException("El formato del envase es obligatorio.");
        }
        this.formatoEnvase = formatoEnvase;
    }

    @Override
    public ModalidadProducto modalidad() {
        return ModalidadProducto.ENAVASADO;
    }

    public String getFormatoEnvase() {
        return formatoEnvase;
    }
}
