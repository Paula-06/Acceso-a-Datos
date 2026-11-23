import java.io.IOException;
import java.util.List;

/**
 * Interfaz única de lectura/escritura para clientes y pagos.
 * La lógica (gestor y menú) solo conoce esta interfaz, nunca CSV ni JSON.
 */
public interface RepositorioGasolinera {

    // ---- Clientes ----
    List<RegistroGasolinera> cargarClientes();

    void guardarCliente(RegistroGasolinera cliente);

    int obtenerSiguienteIdCliente();

    boolean existeMatricula(String matricula);

    RegistroGasolinera buscarClientePorId(int id);

    List<RegistroGasolinera> getClientesOrdenados();

    // ---- Pagos ----
    List<RegistroGasolinera> cargarPagos();

    void guardarPago(RegistroGasolinera pago);

    int obtenerSiguienteIdPago();

    // ---- Operaciones de apoyo para la migración ----
    /** Indica si ya existe algún fichero de datos de este almacén. */
    boolean existenFicheros();

    /** Crea los ficheros vacíos cuando no existen (respuesta "n" a la migración). */
    void prepararFicheros() throws IOException;

    /**
     * Escribe clientes y pagos como una sola operación. Cada almacén decide cómo
     * hacerlo de forma segura; el JSON usa ficheros temporales para no dejar
     * resultados parciales ni sobrescribir datos ante un error.
     */
    void guardarConjunto(List<RegistroGasolinera> clientes,
                         List<RegistroGasolinera> pagos) throws IOException;
}
