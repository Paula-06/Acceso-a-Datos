import java.util.List;

public interface ClientesDAO {

    List<Cliente> cargarClientes();

    void guardarCliente(Cliente cliente);

    int obtenerSiguienteId();

    boolean existeMatricula(String matricula);

    Cliente buscarPorId(int id);

    List<Cliente> getClienteOrden();
}