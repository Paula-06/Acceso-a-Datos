package gasolinera.negocio;

import gasolinera.modelo.RegistroGasolinera;
import gasolinera.persistencia.RepositorioGasolinera;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Gestor único de la gasolinera: reúne la lógica de clientes y de pagos.
 * Solo depende de {@link RepositorioGasolinera}, nunca del formato de almacenamiento.
 */
public class GestorGasolinera {

    private final RepositorioGasolinera repositorio;
    private final Scanner sc = new Scanner(System.in);

    public GestorGasolinera(RepositorioGasolinera repositorio) {
        this.repositorio = repositorio;
    }

    // Dar de alta un cliente (mismas validaciones y mensajes que GestorCliente)
    public void altaCliente() {
        String nombre;
        String telefono;
        String matricula;

        do {
            System.out.print("Nombre: ");
            nombre = sc.nextLine().trim();  // eliminamos espacios en blanco
            if (nombre.isEmpty()) {
                System.out.println("El nombre no puede estar vacío.");
            }
        } while (nombre.isEmpty());

        do {
            System.out.print("Teléfono: ");
            telefono = sc.nextLine().trim();
            if (telefono.isEmpty()) {
                System.out.println("El teléfono no puede estar vacío.");
            }
        } while (telefono.isEmpty());

        do {
            System.out.print("Matrícula: ");
            matricula = sc.nextLine().trim().toUpperCase(); // ponemos en mayúsculas
            if (matricula.isEmpty()) {
                System.out.println("La matrícula no puede estar vacía.");
            }
        } while (matricula.isEmpty());

        if (repositorio.existeMatricula(matricula)) {
            System.out.println("Esa matrícula ya está registrada.");
            return;
        }
        int id = repositorio.obtenerSiguienteIdCliente();

        RegistroGasolinera cliente = RegistroGasolinera.nuevoCliente(id, nombre, telefono, matricula);
        repositorio.guardarCliente(cliente);
        System.out.println("Cliente registrado con ID " + id);
    }

    // Listar clientes
    public void listarClientes() {
        List<RegistroGasolinera> clientes = repositorio.getClientesOrdenados();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        System.out.println("\n----- LISTADO DE CLIENTES -----");
        for (RegistroGasolinera cliente : clientes) {
            System.out.println(cliente);
        }
    }

    // Buscar cliente
    public void buscarCliente() {
        List<RegistroGasolinera> clientes = repositorio.getClientesOrdenados();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        System.out.print("Buscar: ");
        String texto = sc.nextLine().trim().toLowerCase();

        boolean encontrado = false;
        for (RegistroGasolinera cliente : clientes) {
            String datos = (cliente.getNombre() + " " +
                    cliente.getTelefono() + " " +
                    cliente.getMatricula()).toLowerCase();
            if (datos.contains(texto)) {
                System.out.println(cliente);
                encontrado = true;
            }
        }
        if (!encontrado) {
            System.out.println("No se encontraron coincidencias.");
        }
    }

    // Procesar un pago de repostaje (mismas validaciones y mensajes que GestorPagos)
    public void procesarPago() {
        List<RegistroGasolinera> clientes = repositorio.cargarClientes();
        if (clientes == null || clientes.isEmpty()) {
            System.out.println("No se han encontrado clientes.");
            return;
        }
        System.out.printf("%-5s %-15s %-12s %-12s%n", "ID", "NOMBRE", "TELÉFONO", "MATRÍCULA");
        for (RegistroGasolinera c : clientes) {
            System.out.printf("%-5d %-15s %-12s %-12s%n", c.getId(), c.getNombre(), c.getTelefono(), c.getMatricula());
        }

        // Pedir ID del cliente y verificar que existe
        RegistroGasolinera clienteSeleccionado = null;
        do {
            System.out.println("ID del cliente: ");
            String IdCliente = sc.nextLine().trim();
            try {
                int idBuscado = Integer.parseInt(IdCliente);
                if (idBuscado <= 0) {
                    System.out.println("El identificador debe ser un entero positivo.");
                    continue;
                }
                clienteSeleccionado = repositorio.buscarClientePorId(idBuscado);
                if (clienteSeleccionado == null) {
                    System.out.println("No existe un cliente con ese identificador. No se ha registrado el pago.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("No existe un cliente con ese identificador. No se ha registrado el pago.");
                return;
            }
        } while (clienteSeleccionado == null);

        // Pedir fecha
        Date fecha = null;
        SimpleDateFormat formateo = new SimpleDateFormat("dd/MM/yyyy");
        do {
            System.out.println("Fecha (dd/MM/yyyy; vacío para hoy)");
            String fechaTexto = sc.nextLine();
            try {
                if (fechaTexto.isEmpty()) {
                    fecha = new Date();
                } else {
                    fecha = formateo.parse(fechaTexto);
                }
            } catch (ParseException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (fecha == null);

        // Pedir importe: se repite hasta que sea positivo y tenga como máximo dos decimales
        double importe;
        boolean importeValido;
        do {
            System.out.println("Importe(€): ");
            importe = sc.nextDouble();
            importeValido = importe > 0 && tieneMaximoDosDecimales(importe);
            if (!importeValido) {
                System.out.println("Introduce una cantidad mayor que cero y con un máximo de dos decimales");
            }
        } while (!importeValido);

        // Pedir litros: misma restricción de dos decimales que el importe
        double litros;
        boolean litrosValidos;
        do {
            System.out.println("Litros: ");
            litros = sc.nextDouble();
            litrosValidos = litros > 0 && tieneMaximoDosDecimales(litros);
            if (!litrosValidos) {
                System.out.println("Introduce una cantidad mayor que cero y con un máximo de dos dcimales");
            }
        } while (!litrosValidos);
        sc.nextLine();

        // Pedir combustible
        RegistroGasolinera.Combustible combustible = null;
        do {
            System.out.println("Combustible: ");
            String opciones = sc.nextLine();
            if (opciones.equalsIgnoreCase("Gasolina")) {
                combustible = RegistroGasolinera.Combustible.Gasolina;
            } else if (opciones.equalsIgnoreCase("Diesel")) {
                combustible = RegistroGasolinera.Combustible.Diesel;
            } else {
                System.out.println("Debes ser Gasolina o Diesel");
            }
        } while (combustible == null);

        // Crear el pago y guardarlo antes de mostrar el éxito
        int idPago = repositorio.obtenerSiguienteIdPago();
        RegistroGasolinera pago = RegistroGasolinera.nuevoPago(
                idPago, clienteSeleccionado.getNombre(), fecha, importe, litros, combustible);
        repositorio.guardarPago(pago);
        System.out.printf(
                "%-5d %-15s %-10.2f %-10.2f %-15s%n",
                idPago,
                clienteSeleccionado.getNombre(),
                importe,
                litros,
                combustible
        );
    }

    // Consultar pagos
    public void consultarPago() {
        List<RegistroGasolinera> pagos = repositorio.cargarPagos();
        if (pagos.isEmpty()) {
            System.out.println("No hay pagos registrados.");
            return;
        }
        System.out.println("\n----- LISTADO DE PAGOS -----");
        for (RegistroGasolinera p : pagos) {
            System.out.println(p);
        }
    }

    /**
     * Comprueba hasta dos decimales usando la representación decimal corta del double,
     * evitando los falsos rechazos de Math.round(valor * 100) (p. ej. 0,29).
     */
    private boolean tieneMaximoDosDecimales(double valor) {
        if (!Double.isFinite(valor)) {
            return false;
        }
        return BigDecimal.valueOf(valor).scale() <= 2;
    }
}
