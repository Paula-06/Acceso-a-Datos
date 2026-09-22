import java.util.List;

public interface IRepositorioPagos {

    List<Pago> cargarPagos();

    void guardarPago(Pago pago);

    int obtenerSigId();
}