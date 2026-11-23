public class Main {
    public static void main(String[] args) {
        RepoJson repoJson = new RepoJson();
        RepositorioCsv repoCsv = new RepositorioCsv();

        // Migración al principio, usando las clases unificadas
        MigrarCsvtoJson migrador = new MigrarCsvtoJson(repoCsv, repoJson);
        migrador.migracion();

        // Para trabajar con CSV en lugar de JSON bastaría con:
        // RepositorioGasolinera almacen = repoCsv;
        RepositorioGasolinera almacen = repoJson;
        GestorGasolinera gestor = new GestorGasolinera(almacen);
        Menu menu = new Menu(gestor);

        // Arrancar la aplicación
        menu.iniciar();
    }
}
