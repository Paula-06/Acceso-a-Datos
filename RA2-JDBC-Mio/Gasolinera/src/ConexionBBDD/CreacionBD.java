package ConexionBBDD;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreacionBD {

        public static void main(String[] args) {
                Path archivo = Path.of("gasolinera.sqlite");
                String url = "jdbc:sqlite:" + archivo.toAbsolutePath();

                String clientes = """
            CREATE TABLE IF NOT EXISTS clientes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                telefono TEXT,
                matricula TEXT
            )""";

                String pagos = """
            CREATE TABLE IF NOT EXISTS pagos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_cliente INTEGER NOT NULL REFERENCES clientes(id),
                fecha TEXT NOT NULL,
                importe REAL NOT NULL,
                litros REAL NOT NULL,
                combustible TEXT NOT NULL
                    CHECK (combustible IN ('gasolina', 'diesel'))
            )""";

                try (Connection conexion = DriverManager.getConnection(url);
                     Statement st = conexion.createStatement()) {
                    st.execute("PRAGMA foreign_keys = ON");
                    st.execute(clientes);
                    st.execute(pagos);
                    System.out.println("Tablas creadas en: " + archivo.toAbsolutePath());
                } catch (SQLException e) {
                    System.err.println("Error al crear la BD: " + e.getMessage());
                }
            }
    }

