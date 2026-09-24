import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class RepositorioClientes implements IRepositorioClientes{
    static final String FICHERO = "clientes.csv";

   // @Override
    public List<Cliente> cargarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        Path ruta = Path.of(FICHERO);

        if (!Files.exists(ruta)) {
            return clientes;//Ta vacio / no existe
        }

        try (BufferedReader br = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) {
                    continue; // ignorar las líneas que están vacías o que solo tienen espacios en blanco.
                }
                String[] datos = linea.split(";");

                if (datos.length != 4) {
                    continue;
                }
/*
                int id = Integer.parseInt(datos[0]);
                String nombre = datos[1];
                String telefono = datos[2];
                String matricula = datos[3];
*/
               // Cliente cliente = new Cliente(id, nombre, telefono, matricula);
             //   clientes.add(cliente);

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return clientes;
    }

    public void guardarCliente(Cliente cliente) {
        Path ruta = Path.of(FICHERO);
        //7. Escribir texto
        //8. StandardOpenOption: decidir cómo se abre
        /*
        * CREATE : crea el archivo si no existe.
        CREATE_NEW : lo crea, pero falla si ya existe.
        TRUNCATE_EXISTING : vacía el contenido anterior.
        APPEND : añade al final.
        WRITE : abre para escritura.
        READ : abre para lectura.
        * */

        try (BufferedWriter escritor = Files.newBufferedWriter(ruta,
                StandardCharsets.UTF_8,
                StandardOpenOption.APPEND,
                StandardOpenOption.CREATE)) {
            escritor.write(cliente.toCsv());
            escritor.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

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
}