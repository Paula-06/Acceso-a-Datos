import java.util.List;

public class RepositorioClientes implements IRepositorioClientes {

    static final String FICHERO = "clientes.txt";
    //Devuelve todo vacío de clientes hasta que este la interfaz

    @Override
    public List<Cliente> cargarClientes() {
        return null;
    }

    @Override
    public void guardarCliente(Cliente cliente) {

    }

    @Override
    public int obtenerSiguienteId() {
        return  0;
    }
    @Override
    public boolean existeMatricula(String matricula) {
        return false;
    }

}
