import java.util.*;

    public class GestorCliente {

        IRepositorioClientes repositorio;
        Scanner sc = new Scanner(System.in);

        public GestorCliente(IRepositorioClientes repositorio) {
            this.repositorio = repositorio;
        }

        //Dar de alta un cliente
        public void altaCliente() {
            String nombre;
            String telefono;
            String matricula;

        //Pedimos nombre
            do {
                System.out.print("Nombre: ");
                nombre = sc.nextLine().trim();  //eliminamos los espacios en blanco

                //comprobar no contiene elementos o tiene longitud de cero
                if (nombre.isEmpty()) {
                    System.out.println("El nombre no puede estar vacío.");
                }
            } while (nombre.isEmpty());


        //Pedimos telefono
            do {
                System.out.print("Teléfono: ");
                telefono = sc.nextLine().trim();

                if (telefono.isEmpty()) {
                    System.out.println("El teléfono no puede estar vacío.");
                }
            } while (telefono.isEmpty());

        //Pedimos matricula y verificamos que no exista
            do {
                System.out.print("Matrícula: ");
                matricula = sc.nextLine().trim().toUpperCase();//Ponemos en mayusculas

                if (matricula.isEmpty()) {
                    System.out.println("La matrícula no puede estar vacía.");
                }
            } while (matricula.isEmpty());

            if(repositorio.existeMatricula(matricula)){
                System.out.println(
                        "Esa matrícula ya está registrada.");
                return;
            }

            int id = repositorio.obtenerSiguienteId();

            Cliente cliente = new Cliente(id, nombre, telefono, matricula);
            repositorio.guardarCliente(cliente);
            System.out.println("Cliente registrado con ID " + id);
        }

        // Listar clientes
        public void listarClientes() {

            List<Cliente> clientes = repositorio.cargarClientes();

            if (clientes.isEmpty()) {
                System.out.println("No hay clientes registrados.");
                return;
            }

            System.out.println("\n----- LISTADO DE CLIENTES -----");
            for (Cliente cliente : clientes) {
                System.out.println(cliente);
            }
        }
        // Buscar cliente
        public void buscarCliente() {

            List<Cliente> clientes = repositorio.cargarClientes();

            if (clientes.isEmpty()) {
                System.out.println("No hay clientes registrados.");
            }

            System.out.print("Buscar: ");
            String texto = sc.nextLine().trim().toLowerCase();

            boolean encontrado = false;

            for (Cliente cliente : clientes) {

                String datos = (
                        cliente.getNombre() + " " +
                                cliente.getTelefono() + " " +
                                cliente.getMatricula()
                ).toLowerCase();

                if (datos.contains(texto)) {
                    System.out.println(cliente);
                    encontrado = true;
                }
            }

            if (!encontrado) {
                System.out.println("No se encontraron coincidencias.");
            }
        }

        // Buscar cliente por ID
        public Cliente obtenerClientePorId(int id) {

            List<Cliente> clientes = repositorio.cargarClientes();

            for (Cliente cliente : clientes) {

                if (cliente.getId() == id) {
                    return cliente;
                }
            }

            return null;
        }
    }
