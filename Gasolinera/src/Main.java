public class Main {
    public static void main(String[] args) {

        IRepositorioClientes repoClientes = new RepositorioClientes();
        IRepositorioPagos repoPagos = new RepositorioPagos();

        GestorCliente gestorCliente = new GestorCliente(repoClientes);
        GestorPagos gestionPagos = new GestorPagos(repoPagos, repoClientes);

        Menu menu = new Menu(gestorCliente, gestionPagos);

        menu.iniciar();
    }
}