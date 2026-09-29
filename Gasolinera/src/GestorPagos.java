

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class GestorPagos {
    //Funcionalidades obligatorias

    PagosDAO repPago;
    ClientesDAO repoCliente;
    Scanner sc = new Scanner(System.in);

    // Constructor que recibe ambos repositorios
    public GestorPagos(PagosDAO repositorioPagos, ClientesDAO repositorioClientes) {
        this.repPago = repositorioPagos;
        this.repoCliente = repositorioClientes;
    }

    // Procesar un pago de repostaje
    public void procesarPago() {
    List<Cliente> clientes = repoCliente.cargarClientes();

    if (clientes == null || clientes.isEmpty()) {
        System.out.println("No se han encontrado clientes.");
        return;
    }

        System.out.printf("%-5s %-15s %-12s %-12s%n", "ID", "NOMBRE", "TELÉFONO", "MATRÍCULA");
        for (Cliente c : clientes) {
            System.out.printf("%-5d %-15s %-12s %-12s%n", c.getId(), c.getNombre(), c.getTelefono(), c.getMatricula());
        }

        // Pedir ID del cliente y verificar que existe
        Cliente clienteSeleccionado = null;

        do {
            System.out.println("ID del cliente: ");
            String IdCliente = sc.nextLine().trim();
            try {
                int idBuscado = Integer.parseInt(IdCliente);
                if (idBuscado <= 0) {
                    System.out.println("El identificador debe ser un entero positivo.");
                    continue;
                }
                clienteSeleccionado = repoCliente.buscarPorId(idBuscado);
                if (clienteSeleccionado == null) {
                    System.out.println("No existe un cliente con ese identificador. No se ha registrado el pago.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("No existe un cliente con ese identificador. No se ha registrado el pago.");
                return;
            }
        } while (clienteSeleccionado == null);


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

        //Pedir importe
        double importe = 0;
        do {
            System.out.println("Importe(€): ");
            importe = sc.nextDouble();
            try {
                if (importe <= 0) {
                    System.out.println("Introduce una cantidad mayor que cero y con un máximo de dos decimales");
                }
                //Comprobar decimal
                if (Math.round(importe*100) != importe *100) {
                    throw new IllegalArgumentException("");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } while (importe <= 0);

        //Pedir litros
        double litros;
        do {
            System.out.println("Litros: ");
            litros = sc.nextDouble();
            try {
                if (litros <= 0) {
                    System.out.println("Introduce una cantidad mayor que cero y con un máximo de dos dcimales");
                }
                BigDecimal bd = new BigDecimal(litros);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }  while (litros <= 0);

        sc.nextLine();

        //Pedir combustible
        Combustible combustible = null;
        do {
            System.out.println("Combustible: ");
            String opciones = sc.nextLine();

            if (opciones.equalsIgnoreCase("Gasolina")) {
                combustible = Combustible.Gasolina;
            } else if (opciones.equalsIgnoreCase("Diesel")) {
                combustible = Combustible.Diesel;
            } else {
                System.out.println("Debes ser Gasolina o Diesel");
            }
        } while (combustible == null);

        //Crear Pago
        int idPago = repPago.obtenerSigId();
        Pago pago = new Pago(idPago, combustible, litros, importe, fecha, clienteSeleccionado.getNombre());

        //Guardar pago
        repPago.guardarPago(pago);
        System.out.println(
                "Pago registrado con ID " + idPago + ". Cliente: "
                        + clienteSeleccionado.getNombre()
                        + ". Importe: " + importe + " €"
                        + "litros: " + litros
                        + "COMBUSTIBLE: " + combustible);
    }

    //Consultar pagos
    public void consultarPago() {
        List<Pago> pagos = repPago.cargarPagos();
        if (pagos.isEmpty()) {
            System.out.println("No hay pagos registrados.");
            return;
        }
        System.out.println("\n----- LISTADO DE PAGOS -----");
        for (Pago p : pagos) {
            System.out.println(p);
        }
    }
}
