import java.util.Scanner;

public class Menu {

    private Scanner sc = new Scanner(System.in);

    private GestorCliente gestorCliente;
    private GestorPagos gestorPagos;

    public Menu(GestorCliente gestorCliente,
                GestorPagos gestorPagos) {

        this.gestorCliente = gestorCliente;
        this.gestorPagos = gestorPagos;
    }

    public void iniciar() {
        int opcion;
        do {

            mostrarMenu();
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {

                case 1 -> gestorCliente.altaCliente();
                case 2 -> gestorCliente.listarClientes();
                case 3 -> gestorCliente.buscarCliente();
                case 4 -> gestorPagos.procesarPago();
                case 5 -> gestorPagos.consultarPago();
                case 0 -> System.out.println("Hasta Pronto.");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void mostrarMenu() {

        System.out.println("\n=== GESTIÓN DE GASOLINERA ===");
        System.out.println("1. Dar de alta un cliente");
        System.out.println("2. Listar clientes");
        System.out.println("3. Buscar clientes");
        System.out.println("4. Procesar un pago de repostaje");
        System.out.println("5. Consultar pagos");
        System.out.println("0. Salir");
        System.out.print("Opción: ");
    }
}