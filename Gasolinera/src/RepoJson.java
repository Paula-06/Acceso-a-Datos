import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RepoJson implements ClientesDAO {

    static final String FICHERO = "clientes.json";
    private final Path clientejson = Path.of(FICHERO);


    // @Override
    public List<Cliente> cargarClientes() {
        List<Cliente> clientes = new ArrayList<>();

        if (!Files.exists(clientejson)) {
            return clientes;
        }
        try (BufferedReader br = Files.newBufferedReader(clientejson, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) {
                    continue; // ignorar las líneas que están vacías o que solo tienen espacios en blanco.
                }
                String[] datos = linea.split(",");
                if (datos.length != 4) {
                    continue;

                }

                int id = Integer.parseInt(datos[0].split(":")[1].trim());
                String nombre = datos[1].split(":")[1].trim();
                String telefono = datos[2].split(":")[1].trim();
                String matricula = datos[3].split(":")[1].trim();

                Cliente cliente = new Cliente(id, nombre, telefono, matricula);
                clientes.add(cliente);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } return clientes;
    }

    public void guardarCliente(Cliente cliente) {
        Path ruta = Path.of(FICHERO);

            try (BufferedWriter escritor = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                escritor.write(cliente.toJson());
                escritor.newLine();

            } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    //Operaciones
    public int obtenerSiguienteId() {
        List<Cliente> clientes = cargarClientes();
        int maximo = 0;
        for (Cliente c : clientes) {
            if (c.getId() > maximo) {
                maximo = c.getId();
            }
        }
        return maximo + 1;
    }

    public boolean existeMatricula(String matricula) {
        List<Cliente> clientes = cargarClientes();
        for (Cliente c : clientes) {
            if (c.getMatricula()
                    .equalsIgnoreCase(matricula)) {
                return true;
            }
        }
        return false;
    }

    public Cliente buscarPorId(int id) {
        List<Cliente> clientes = cargarClientes();
        for (Cliente cliente : clientes) {
            if (cliente.getId() == id) {
                return cliente;
            }
        }
        return null;
    }

    public List<Cliente> getClienteOrden() {
        List<Cliente> clientes = cargarClientes();
        return clientes.stream().sorted(Comparator.comparing(Cliente::getNombre,String.CASE_INSENSITIVE_ORDER).thenComparing(Cliente::getId)).toList();
        //ordena la lista por nombre, resuelve empates por ID y devuelve el resultado como nueva lista.
    }


}
