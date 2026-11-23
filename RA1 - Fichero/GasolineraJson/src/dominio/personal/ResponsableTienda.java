package dominio.personal;

import java.math.BigDecimal;

/** Responsable de tienda: presupuesto autorizado de compras. */
public final class ResponsableTienda extends Funcion {

    private final BigDecimal presupuestoCompras;

    public ResponsableTienda(BigDecimal presupuestoCompras) {
        super("RESPONSABLE_TIENDA", "Responsable de tienda");
        if (presupuestoCompras == null || presupuestoCompras.signum() <= 0) {
            throw new IllegalArgumentException("El presupuesto debe ser mayor que cero.");
        }
        this.presupuestoCompras = presupuestoCompras;
    }

    public BigDecimal getPresupuestoCompras() {
        return presupuestoCompras;
    }
}
