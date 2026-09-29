

import java.math.BigDecimal;
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
                if (clientes != null) {
                    for (Cliente c : clientes) {
                        if (c.getId() == idBuscado) {
                            clienteSeleccionado = c;
                            break;
                        }
                    }
                }

                if (clienteSeleccionado == null) {
                    System.out.println("Cliente inexistente");
                    return; // Si el cliente no existe, corta la ejecución según el requisito
                }
            } catch (NumberFormatException e) {
                System.out.println("Cliente inexistente");
                return;
            }
        } while (clienteSeleccionado == null);


        Date fecha = null;
        SimpleDateFormat formateo = new SimpleDateFormat("dd/MM/yyyy");
        do {
            System.out.println("Fecha (dd/MM/yyyy; vacío para hoy)");
            String fechaTexto = sc.nextLine();

            if (fechaTexto.isEmpty()) {
                fecha = new Date();
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

                if (Math.round(importe*100);

                //BigDecimal bd = new BigDecimal(importe);
                //boolean esDecimal = bd.scale() <= 2;

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

        //Pedir combustible
        Combustible combustible = null;
        do {
            System.out.println("Combustible: ");
            


        } while (combustible == null);



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
