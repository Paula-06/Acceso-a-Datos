public class Main {
    public static void main(String[] args) {
        // 1. Instanciar los repositorios necesarios
        RepoJson repoClientesJson = new RepoJson();
        RepoJsonP repoPagosJson = new RepoJsonP();
        RepositorioClientes repoClientesCsv = new RepositorioClientes();
        RepositorioPagos repoPagosCsv = new RepositorioPagos();

        // 2. Ejecutar la migración en el primer inicio (antes de mostrar el menú)
        MigrarCsvtoJson migrador = new MigrarCsvtoJson(
            repoClientesJson, 
            repoPagosJson, 
            repoClientesCsv, 
            repoPagosCsv
        );
        migrador.migracion();

        // 3. Inicializar los gestores y el menú principal de la aplicación
        GestorCliente gestorCliente = new GestorCliente(repoClientesJson);
        GestorPagos gestorPagos = new GestorPagos(repoPagosJson, repoClientesJson);

        Menu menu = new Menu(gestorCliente, gestorPagos);
        
        // 4. Arrancar la aplicación
        menu.iniciar();
    }
}