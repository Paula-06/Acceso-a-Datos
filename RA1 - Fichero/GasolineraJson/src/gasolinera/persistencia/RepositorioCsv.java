package gasolinera.persistencia;

import gasolinera.modelo.RegistroGasolinera;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Almacenamiento CSV unificado (clientes + pagos).
 * La escritura se conserva tal cual: CREATE + APPEND sobre clientes.csv y pagos.csv.
 * La lectura se usa solo en la migración: si aparece un dato inválido se informa
 * y se lanza una excepción para que la migración se cancele antes de escribir nada.
 */
public class RepositorioCsv implements RepositorioGasolinera {

    static final String FICHERO_CLIENTES = "clientes.csv";
    static final String FICHERO_PAGOS = "pagos.csv";

    private final Path rutaClientes = Path.of(FICHERO_CLIENTES);
    private final Path rutaPagos = Path.of(FICHERO_PAGOS);

    @Override
    public List<RegistroGasolinera> cargarClientes() {
        List<RegistroGasolinera> clientes = new ArrayList<>();
        if (!Files.exists(rutaClientes)) {
            return clientes; // no existe: lista vacía
        }
        try (BufferedReader br = Files.newBufferedReader(rutaClientes, StandardCharsets.UTF_8)) {
            String linea;
            int numeroLinea = 0;
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                if (linea.isBlank()) {
                    continue; // ignorar líneas vacías o con solo espacios
                }
                String[] datos = linea.split(";");
                if (datos.length != 4) {
                    throw new IllegalArgumentException(
                            "Error: dato inválido en " + FICHERO_CLIENTES + ", línea " + numeroLinea);
                }
                try {
                    int id = Integer.parseInt(datos[0]);
                    String nombre = datos[1];
                    String telefono = datos[2];
                    String matricula = datos[3];
                    clientes.add(RegistroGasolinera.nuevoCliente(id, nombre, telefono, matricula));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Error: dato inválido en " + FICHERO_CLIENTES + ", línea " + numeroLinea, e);
                }
            }
        } catch (IOException e) {
            // Envuelve el error de acceso: la migración debe abortar sin escribir.
            throw new UncheckedIOException(e);
        }
        return clientes;
    }

    @Override
    public List<RegistroGasolinera> cargarPagos() {
        List<RegistroGasolinera> pagos = new ArrayList<>();
        if (!Files.exists(rutaPagos)) {
            return pagos;
        }
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        try (BufferedReader br = Files.newBufferedReader(rutaPagos, StandardCharsets.UTF_8)) {
            String linea;
            int numeroLinea = 0;
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                if (linea.isBlank()) {
                    continue;
                }
                String[] datos = linea.split(";");
                if (datos.length != 6) {
                    throw new IllegalArgumentException(
                            "Error: dato inválido en " + FICHERO_PAGOS + ", línea " + numeroLinea);
                }
                try {
                    int id = Integer.parseInt(datos[0]);
                    String cliente = datos[1];
                    Date fecha = formato.parse(datos[2]);
                    double importe = Double.parseDouble(datos[3]);
                    double litros = Double.parseDouble(datos[4]);
                    RegistroGasolinera.Combustible combustible =
                            RegistroGasolinera.Combustible.valueOf(datos[5]);
                    pagos.add(RegistroGasolinera.nuevoPago(id, cliente, fecha, importe, litros, combustible));
                } catch (ParseException | IllegalArgumentException e) {
                    throw new IllegalArgumentException(
                            "Error: dato inválido en " + FICHERO_PAGOS + ", línea " + numeroLinea, e);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return pagos;
    }

    /** Escritura de clientes conservada de la versión anterior. */
    @Override
    public void guardarCliente(RegistroGasolinera cliente) {
        try (BufferedWriter escritor = Files.newBufferedWriter(rutaClientes, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            escritor.write(cliente.toCsv());
            escritor.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /** Escritura de pagos conservada de la versión anterior. */
    @Override
    public void guardarPago(RegistroGasolinera pago) {
        try (BufferedWriter escritor = Files.newBufferedWriter(rutaPagos, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            escritor.write(pago.toCsv());
            escritor.newLine();
        } catch (IOException e) {
            System.out.println("Error al guardar pago: " + e.getMessage());
        }
    }

    @Override
    public int obtenerSiguienteIdCliente() {
        int maximo = 0;
        for (RegistroGasolinera c : cargarClientes()) {
            if (c.getId() > maximo) {
                maximo = c.getId();
            }
        }
        return maximo + 1;
    }

    @Override
    public boolean existeMatricula(String matricula) {
        for (RegistroGasolinera c : cargarClientes()) {
            if (c.getMatricula().equalsIgnoreCase(matricula)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public RegistroGasolinera buscarClientePorId(int id) {
        for (RegistroGasolinera cliente : cargarClientes()) {
            if (cliente.getId() == id) {
                return cliente;
            }
        }
        return null;
    }

    @Override
    public List<RegistroGasolinera> getClientesOrdenados() {
        // Ordena por nombre (ignorando mayúsculas), desempata por id.
        return cargarClientes().stream()
                .sorted(Comparator.comparing(RegistroGasolinera::getNombre, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(RegistroGasolinera::getId))
                .toList();
    }

    // Unificado con el JSON: máximo + 1, para que ambos almacenes sean intercambiables.
    @Override
    public int obtenerSiguienteIdPago() {
        int max = 0;
        for (RegistroGasolinera p : cargarPagos()) {
            if (p.getId() > max) {
                max = p.getId();
            }
        }
        return max + 1;
    }

    @Override
    public boolean existenFicheros() {
        return Files.exists(rutaClientes) || Files.exists(rutaPagos);
    }

    @Override
    public void prepararFicheros() {
        // Los CSV son el origen de la migración: nunca se crean ni se modifican aquí.
    }

    @Override
    public void guardarConjunto(List<RegistroGasolinera> clientes,
                                List<RegistroGasolinera> pagos) throws IOException {
        // Los CSV no son destino de la migración; se delega en la escritura de siempre.
        for (RegistroGasolinera cliente : clientes) {
            guardarCliente(cliente);
        }
        for (RegistroGasolinera pago : pagos) {
            guardarPago(pago);
        }
    }
}
