package dominio.producto;

import java.math.BigDecimal;

/** Acuerdo de suministro entre un proveedor y un producto. */
public class SuministroProveedor {

    private Integer id;
    private final Proveedor proveedor;
    private final Producto producto;
    private final String referenciaProveedor;
    private final BigDecimal precioCompra;
    private final int plazoEntregaDias;
    private boolean vigente;

    public SuministroProveedor(Integer id, Proveedor proveedor, Producto producto,
                               String referenciaProveedor, BigDecimal precioCompra, int plazoEntregaDias) {
        if (proveedor == null || producto == null || referenciaProveedor == null || referenciaProveedor.isBlank()) {
            throw new IllegalArgumentException("Faltan datos obligatorios del acuerdo.");
        }
        if (precioCompra == null || precioCompra.signum() <= 0 || plazoEntregaDias < 0) {
            throw new IllegalArgumentException("Precio de compra y plazo deben ser válidos.");
        }
        this.id = id;
        this.proveedor = proveedor;
        this.producto = producto;
        this.referenciaProveedor = referenciaProveedor;
        this.precioCompra = precioCompra;
        this.plazoEntregaDias = plazoEntregaDias;
        this.vigente = true;
    }

    /** La baja del último acuerdo queda pendiente de control en la fase JDBC. */
    public void darDeBaja() {
        this.vigente = false;
    }

    public Integer getId() {
        return id;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public Producto getProducto() {
        return producto;
    }

    public String getReferenciaProveedor() {
        return referenciaProveedor;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public int getPlazoEntregaDias() {
        return plazoEntregaDias;
    }

    public boolean isVigente() {
        return vigente;
    }
}
