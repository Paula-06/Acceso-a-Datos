import java.util.List;

public interface PagosDAO {

    List<Pago> cargarPagos();

    void guardarPago(Pago pago);

    int obtenerSigId();
}