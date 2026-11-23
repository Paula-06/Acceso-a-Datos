package gasolinera.migracion;

import gasolinera.modelo.RegistroGasolinera;
import gasolinera.persistencia.RepositorioGasolinera;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Scanner;

/**
 * Traductora CSV -> JSON adaptada a las clases unificadas.
 * Lee con el repositorio CSV y escribe con el repositorio JSON a través de la
 * interfaz común: no modifica los CSV, rechaza un destino ya existente, valida
 * todos los datos antes de escribir y no deja resultados parciales.
 */
public class MigrarCsvtoJson {

    private final RepositorioGasolinera origenCsv;
    private final RepositorioGasolinera destinoJson;
    private final Scanner sc = new Scanner(System.in);

    private int clientesMigrados = 0;
    private int pagosMigrados = 0;

    public MigrarCsvtoJson(RepositorioGasolinera origenCsv, RepositorioGasolinera destinoJson) {
        this.origenCsv = origenCsv;
        this.destinoJson = destinoJson;
    }

    public void migracion() {
        // Si el destino ya existe, no se toca nada.
        if (destinoJson.existenFicheros()) {
            System.out.println("El destino de la migración ya existe. No se realiza la migración.");
            return;
        }

        System.out.print("¿Desea migrar datos CSV a JSON? (Y/n): ");
        String input;
        do {
            input = sc.nextLine().trim();
        } while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n") && !input.isEmpty());

        if (input.equalsIgnoreCase("y") || input.isEmpty()) {
            try {
                // 1) Leer y validar TODO antes de crear o escribir ningún fichero.
                List<RegistroGasolinera> clientes = origenCsv.cargarClientes();
                List<RegistroGasolinera> pagos = origenCsv.cargarPagos();
                if (!datosValidos(clientes, pagos)) {
                    System.out.println("Error: hay datos inválidos en los CSV. No se ha realizado la migración.");
                    imprimirContadores();
                    return;
                }
                // 2) Escritura conjunta: el destino decide cómo hacerla de forma segura.
                destinoJson.guardarConjunto(clientes, pagos);
                clientesMigrados = clientes.size();
                pagosMigrados = pagos.size();
            } catch (UncheckedIOException e) {
                System.out.println("Error al leer los CSV: " + e.getCause().getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println("No se ha realizado la migración.");
            } catch (IOException e) {
                System.out.println("Error al migrar datos CSV a JSON: " + e.getMessage());
            }
            imprimirContadores();
        } else {
            // Como antes: si el usuario rechaza la migración se dejan los JSON vacíos.
            try {
                destinoJson.prepararFicheros();
            } catch (IOException e) {
                System.out.println("Error al crear los archivos JSON vacíos: " + e.getMessage());
            }
        }
    }

    /** Validación de los registros leídos; cualquier fallo cancela la migración. */
    private boolean datosValidos(List<RegistroGasolinera> clientes, List<RegistroGasolinera> pagos) {
        for (RegistroGasolinera c : clientes) {
            if (c.getId() <= 0 || esBlanco(c.getNombre())
                    || esBlanco(c.getTelefono()) || esBlanco(c.getMatricula())) {
                return false;
            }
        }
        for (RegistroGasolinera p : pagos) {
            if (p.getId() <= 0 || esBlanco(p.getNombre()) || p.getFecha() == null
                    || p.getImporte() <= 0 || p.getLitros() <= 0 || p.getCombustible() == null) {
                return false;
            }
        }
        return true;
    }

    private boolean esBlanco(String texto) {
        return texto == null || texto.isBlank();
    }

    private void imprimirContadores() {
        System.out.println("Clientes migrados: " + clientesMigrados);
        System.out.println("Pagos migrados: " + pagosMigrados);
    }
}
