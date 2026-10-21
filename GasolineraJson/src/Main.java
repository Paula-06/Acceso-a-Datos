public class Main {
    public static void main(String[] args) {
        RepoJson repoClientesJson = new RepoJson();
        RepoJsonP repoPagosJson = new RepoJsonP();
        RepositorioClientes repoClientesCsv = new RepositorioClientes();
        RepositorioPagos repoPagosCsv = new RepositorioPagos();

        //Ejecutar la migración al principio
        MigrarCsvtoJson migrador = new MigrarCsvtoJson(repoClientesJson, repoPagosJson, repoClientesCsv, repoPagosCsv);
        migrador.migracion();

        //------------- CSV
        // GestorCliente gestorCliente = new GestorCliente(repoClientesCsv);
        // GestorPagos gestorPagos = new GestorPagos(repoPagosCsv, repoClientesCsv);

        //------------- JSON
        GestorCliente gestorCliente = new GestorCliente(repoClientesJson);
        GestorPagos gestorPagos = new GestorPagos(repoPagosJson, repoClientesJson);


        Menu menu = new Menu(gestorCliente, gestorPagos);

        //Arrancar la aplicación
        menu.iniciar();
    }
}