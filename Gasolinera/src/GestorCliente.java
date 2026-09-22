import java.util.*;

public class GestorCliente {
    /*
    * private RepositorioClientes clienteDAO;

    * Constructor publicRepositorioClientes(RepositorioClientes clienteDAO) {
    * this.clienteDAO = clienteDAO;
    * }
    * */

    Scanner sc = new Scanner(System.in);

    //Dar de alta un cliente
    public void altaCliente() {
 /*   String nombre;
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
            System.out.println("Teléfono: ");
            telefono = sc.nextLine().trim();

            if (telefono.isEmpty()) {
                System.out.println("El teléfono no puede estar vacío.");
            }
        } while (telefono.isEmpty());

    //Pedimos matricula y verificamos que no exista
        boolean matriculaValida = false;
        do {
            System.out.println("Matrícula: ");
            matricula = sc.nextLine().trim().toUpperCase();//Ponemos en mayusculas

            if (matricula.isEmpty()) {
                System.out.println("La matrícula no puede estar vacía.");
            }
        } while (matricula.isEmpty());

        //A futuro cuando este el DAO de cliente implementar la logica
        //Para comprobar si existe o no y guardar los nuevos clientes
        Cliente nuevoCLiente = new Cliente(nombre, telefono, matricula);

        System.out.println("Cliente registrado con ID 1");
        //System.out.println(nuevoCliente);

  */
    }

    //Listar clientes
    public void listarCliente() {
    /*

        List<Cliente> clientes = RepositorioClientes();

        //Comprobamos que existan
        if (clientes == null || clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }

        //Ordena la lista automáticamente
        Collections.sort(clientes);

        //Mostrar en main
        System.out.println("\n--- LISTADO DE CLIENTES ---");
        System.out.printf("ID", "NOMBRE", "TELÉFONO", "MATRÍCULA");
        System.out.println("--------------------------------------------------");

        for (Cliente c : clientes) {
            System.out.printf(
                    c.getId(),
                    c.getNombre(),
                    c.getTelefono(),
                    c.getMatricula());
        }

     */
    }

    //Buscar clientes
    public void buscarCliente() {
        /*
        List<Cliente> clientesRegistrados = RepositorioClientes;

        //Comprobamos que existan
        if (clientesRegistrados == null || clientesRegistrados.isEmpty()) {
            System.out.println("No hay clientes registrados en el sistema.");
            return;
        }

        //Solicitamos texto de busqueda
        String textoBusqueda;
        do {
            System.out.print("Ingrese texto a buscar (nombre, teléfono o matrícula): ");
            textoBusqueda = sc.nextLine().trim();

            if (textoBusqueda.isEmpty()) {
                System.out.println("El texto de búsqueda no puede estar vacío.");
            }
        } while (textoBusqueda.isEmpty());

        //Convertir a minúsculas para comparar si distingue mayúsculas/minúsculas
        String comprobar = textoBusqueda.toLowerCase();
        List<Cliente> coincidencias = new ArrayList<>();

        //Buscar coincidencias con lista
        for (Cliente c : clientesRegistrados) {
            // Creamos una sola cadena con todos los campos
            String datosCliente = (c.getNombre() + " " + c.getTelefono() + " " + c.getMatricula()).toLowerCase();

            if (datosCliente.contains(comprobar)) {
                coincidencias.add(c);
            }
        }

            if (coincidencias.isEmpty()) {
                System.out.println("No se han encontrado clientes.");
                return;
            }

            Collections.sort(coincidencias);


        //Imprimir cabecera alineada en columnas
        System.out.println("ID NOMBRE TELÉFONO MATRÍCULA");

        //Imprimir filas
        for (Cliente c : coincidencias) {
            System.out.println(c.getId() + " " + c.getNombre() + " " + c.getTelefono() + " " + c.getMatricula());
        }

         */
    }

    // Obtiene un cliente por su identificador.
    public Cliente obtenerClientePorId(int id) {

        return null;
    }

}
