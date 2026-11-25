package gasolinera.persistencia;

import gasolinera.modelo.RegistroGasolinera;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Almacenamiento JSON unificado (clientes + pagos).
 * La lectura nunca modifica el fichero: si una línea está malformada se informa
 * y se ignora esa línea, sin sobrescribir los datos ya existentes.
 */
public class RepoJson implements RepositorioGasolinera {

    static final String FICHERO_CLIENTES = "clientes.json";
    static final String FICHERO_PAGOS = "pagos.json";

    private final Path rutaClientes = Path.of(FICHERO_CLIENTES);
    private final Path rutaPagos = Path.of(FICHERO_PAGOS);

    @Override
    public List<RegistroGasolinera> cargarClientes() {
        List<RegistroGasolinera> clientes = new ArrayList<>();
        if (!Files.exists(rutaClientes)) {
            return clientes;
        }
        try (BufferedReader br = Files.newBufferedReader(rutaClientes, StandardCharsets.UTF_8)) {
            String linea;
            int numeroLinea = 0;
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                if (linea.isBlank()) {
                    continue;
                }
                try {
                    String[] datos = linea.split(",");
                    if (datos.length != 4) {
                        throw new IllegalArgumentException("Número de campos incorrecto");
                    }
                    int id = Integer.parseInt(datos[0].split(":")[1].trim());
                    String nombre = datos[1].split(":")[1].trim();
                    String telefono = datos[2].split(":")[1].trim();
                    // La última línea del registro termina en '}': se quita antes de guardar
                    // la matrícula (el formato de escritura se mantiene igual).
                    String matricula = datos[3].split(":")[1].replace("}", "").trim();
                    clientes.add(RegistroGasolinera.nuevoCliente(id, nombre, telefono, matricula));
                } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("Error: datos JSON malformados en " + FICHERO_CLIENTES +
                            ", línea " + numeroLinea + ". No se ha modificado el fichero.");
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
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
                try {
                    String[] datos = linea.split(",");
                    if (datos.length != 6) {
                        throw new IllegalArgumentException("Número de campos incorrecto");
                    }
                    int id = Integer.parseInt(datos[0].split(":")[1].trim());
                    String cliente = datos[1].split(":")[1].trim();
                    Date fecha = formato.parse(datos[2].split(":")[1].trim());
                    double importe = Double.parseDouble(datos[3].split(":")[1].trim());
                    double litros = Double.parseDouble(datos[4].split(":")[1].trim());
                    String combustibleTexto = datos[5].split(":")[1]
                            .replace("}", "").replace("\"", "").trim();
                    RegistroGasolinera.Combustible combustible =
                            RegistroGasolinera.Combustible.valueOf(combustibleTexto);
                    pagos.add(RegistroGasolinera.nuevoPago(id, cliente, fecha, importe, litros, combustible));
                } catch (ParseException | IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("Error: datos JSON malformados en " + FICHERO_PAGOS +
                            ", línea " + numeroLinea + ". No se ha modificado el fichero.");
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return pagos;
    }

    /** Escritura de clientes conservada de la versión anterior. */
    @Override
    public void guardarCliente(RegistroGasolinera cliente) {
        try (BufferedWriter escritor = Files.newBufferedWriter(rutaClientes, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            escritor.write(cliente.toJson());
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
            escritor.write(pago.toJson());
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
        return cargarClientes().stream()
                .sorted(Comparator.comparing(RegistroGasolinera::getNombre, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(RegistroGasolinera::getId))
                .toList();
    }

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
    public void prepararFicheros() throws IOException {
        if (Files.notExists(rutaClientes)) {
            Files.createFile(rutaClientes);
        }
        if (Files.notExists(rutaPagos)) {
            Files.createFile(rutaPagos);
        }
    }

    /**
     * Migración atómica: se escribe en ficheros temporales y solo se sustituyen
     * los definitivos cuando los dos bloques se han escrito correctamente.
     * Si algo falla, se borra lo que se hubiera llegado a mover y no quedan
     * resultados parciales.
     */
    @Override
    public void guardarConjunto(List<RegistroGasolinera> clientes,
                                List<RegistroGasolinera> pagos) throws IOException {
        if (Files.exists(rutaClientes) || Files.exists(rutaPagos)) {
            throw new IOException("Los ficheros de destino ya existen.");
        }
        Path directorio = rutaClientes.toAbsolutePath().getParent();
        Path temporalClientes = null;
        Path temporalPagos = null;
        boolean clientesMovidos = false;
        try {
            temporalClientes = Files.createTempFile(directorio, "clientes-", ".tmp");
            temporalPagos = Files.createTempFile(directorio, "pagos-", ".tmp");
            escribir(temporalClientes, clientes);
            escribir(temporalPagos, pagos);
            Files.move(temporalClientes, rutaClientes, StandardCopyOption.REPLACE_EXISTING);
            clientesMovidos = true;
            Files.move(temporalPagos, rutaPagos, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // Revertimos el único fichero que pudiera haberse movido.
            if (clientesMovidos) {
                try {
                    Files.deleteIfExists(rutaClientes);
                } catch (IOException ignorado) {
                    System.out.println("No se ha podido revertir " + FICHERO_CLIENTES);
                }
            }
            throw e;
        } finally {
            borrarSiExiste(temporalClientes);
            borrarSiExiste(temporalPagos);
        }
    }

    /** Escribe todos los registros con el formato JSON del modelo unificado. */
    private void escribir(Path ruta, List<RegistroGasolinera> registros) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            for (RegistroGasolinera registro : registros) {
                escritor.write(registro.toJson());
                escritor.newLine();
            }
        }
    }

    private void borrarSiExiste(Path ruta) {
        if (ruta == null) {
            return;
        }
        try {
            Files.deleteIfExists(ruta);
        } catch (IOException e) {
            System.out.println("No se ha podido borrar el temporal " + ruta + ": " + e.getMessage());
        }
    }
}
