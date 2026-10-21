import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class MigrarCsvtoJson {

    private final RepoJson repoClientesJson;
    private final RepoJsonP repoPagosJson;
    private final RepositorioClientes repoClientesCsv;
    private final RepositorioPagos repoPagosCsv;
    private final Scanner sc = new Scanner(System.in);




    public MigrarCsvtoJson(RepoJson repoClientesJson, RepoJsonP repoPagosJson, RepositorioClientes repoClientesCsv, RepositorioPagos repoPagosCsv) {
        this.repoClientesJson = repoClientesJson;
        this.repoPagosJson = repoPagosJson;
        this.repoClientesCsv = repoClientesCsv;
        this.repoPagosCsv = repoPagosCsv;
    }

    public void migracion() {
        // Comprobamos si ya existen los ficheros JSON
        if (Files.notExists(Path.of("clientes.json")) && Files.notExists(Path.of("pagos.json"))) {
            System.out.print("¿Desea migrar datos CSV a JSON? (Y/n): ");
            String input;

            do {
                input = sc.nextLine().trim();
            } while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n") && !input.isEmpty());

            if (input.equalsIgnoreCase("y") || input.isEmpty()) {
                try {
                    prepararArchivosJson();
                } catch (IOException e) {
                    System.out.println("Error al preparar los archivos JSON: " + e.getMessage());
                }
                migrarClientes();
                migrarPagos();
            } else {
                try {
                    prepararArchivosJson();
                } catch (IOException e) {
                    System.out.println("Error al crear los archivos JSON vacíos: " + e.getMessage());
                }
            }
        }
    }

    private void prepararArchivosJson() throws IOException {
        Path fClientes = Path.of("clientes.json");
        Path fPagos = Path.of("pagos.json");
        if (Files.notExists(fClientes)) {
            Files.createFile(fClientes);
        }
        if (Files.notExists(fPagos)) {
            Files.createFile(fPagos);
        }
    }

    private void migrarClientes() {
        int clientesMigrados = 0;

        List<Cliente> clientes = repoClientesCsv.cargarClientes();
        if (clientes != null) {
            for (Cliente c : clientes) {
                repoClientesJson.guardarCliente(c);
                clientesMigrados++;
            }
        }
        System.out.println("Clientes migrados: " + clientesMigrados);
    }

    private void migrarPagos() {
         int pagosMigrados = 0;
        List<Pago> pagos = repoPagosCsv.cargarPagos();
        if (pagos != null) {
            for (Pago p : pagos) {
                repoPagosJson.guardarPago(p);
                pagosMigrados++;
            }
        }
        System.out.println("Pagos migrados: " + pagosMigrados);
    }
}