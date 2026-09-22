import java.util.List;

public interface IRepositorioClientes {

    List<Cliente> cargarClientes();

    void guardarCliente(Cliente cliente);

    int obtenerSiguienteId();

    boolean existeMatricula(String matricula);
}