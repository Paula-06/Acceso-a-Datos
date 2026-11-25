package dominio.producto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Proveedor con acuerdo de suministro: los contactos en negociación no se registran. */
public class Proveedor {

    private Integer id;
    private final String codigoInterno;
    private final String identificacionFiscal;
    private String razonSocial;
    private String telefono;
    private String email;
    private String direccion;
    private final List<SuministroProveedor> suministros = new ArrayList<>();

    public Proveedor(Integer id, String codigoInterno, String identificacionFiscal, String razonSocial,
                     String telefono, String email, String direccion) {
        if (codigoInterno == null || codigoInterno.isBlank()
                || identificacionFiscal == null || identificacionFiscal.isBlank()
                || razonSocial == null || razonSocial.isBlank()) {
            throw new IllegalArgumentException("Faltan datos obligatorios del proveedor.");
        }
        if ((telefono == null || telefono.isBlank()) && (email == null || email.isBlank())) {
            throw new IllegalArgumentException("Debe constar al menos un medio de contacto.");
        }
        this.id = id;
        this.codigoInterno = codigoInterno;
        this.identificacionFiscal = identificacionFiscal;
        this.razonSocial = razonSocial;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    public SuministroProveedor anyadirSuministro(Producto producto, String referenciaProveedor,
                                                 java.math.BigDecimal precioCompra, int plazoEntregaDias) {
        for (SuministroProveedor s : suministros) {
            if (s.getProducto() == producto && s.isVigente()) {
                throw new IllegalStateException("Ya existe un acuerdo vigente para ese producto.");
            }
        }
        SuministroProveedor acuerdo = new SuministroProveedor(null, this, producto,
                referenciaProveedor, precioCompra, plazoEntregaDias);
        suministros.add(acuerdo);
        return acuerdo;
    }

    public List<SuministroProveedor> getSuministros() {
        return Collections.unmodifiableList(suministros);
    }

    public Integer getId() {
        return id;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public String getIdentificacionFiscal() {
        return identificacionFiscal;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getDireccion() {
        return direccion;
    }

    @Override
    public String toString() {
        return codigoInterno + " - " + razonSocial;
    }
}
