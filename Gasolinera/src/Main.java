public class Main {
    public static void main(String[] args) {

        ClientesDAO repoClientes = new RepoJson();
        PagosDAO repoPagos = new RepoJsonP();

        GestorCliente gestorCliente = new GestorCliente(repoClientes);
        GestorPagos gestionPagos = new GestorPagos(repoPagos, repoClientes);

        Menu menu = new Menu(gestorCliente, gestionPagos);

        menu.iniciar();
    }
}