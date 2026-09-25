import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RepositorioPagos implements IRepositorioPagos {
    static final String FICHERO = "pagos.csv";

    //Carga los Pagos ya almacenados
    public List<Pago> cargarPagos() {
    List<Pago> pagos = new ArrayList<>();

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        Path ruta = Path.of(FICHERO);
        try (BufferedReader br = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) {
                    continue; // ignorar las líneas que están vacías o que solo tienen espacios en blanco.
                }
                String[] datos = linea.split(";");

                if (datos.length != 4) {
                    continue;
                }
                if (datos.length != 6) {
                    continue;
                }

                try {

                    int id = Integer.parseInt(datos[0]);
                    int idCliente = Integer.parseInt(datos[1]);
                    Date fecha = formato.parse(datos[2]);
                    double importe = Double.parseDouble(datos[3]);
                    double litros = Double.parseDouble(datos[4]);
                    String combustible = datos[5];

                    Pago pago = new Pago(id, combustible, litros, importe, fecha, idCliente);
                    pagos.add(pago);

                } catch (ParseException e) {
                    System.out.println("Error: " + e);
                }




            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return pagos;
    }

    //Guarda Pago
    public void guardarPago(Pago pago) {
        Path ruta = Path.of(FICHERO);

        try (BufferedWriter escritor = Files.newBufferedWriter(ruta,
                StandardCharsets.UTF_8)) {
            escritor.write(pago.toCsv());
            escritor.newLine();
        } catch (IOException e) {
            System.out.println("Error al guardar pago: " + e.getMessage());
        }
    }

    // // Calcula el siguiente id para un nuevo cliente.
    public int obtenerSigId() {
        List<Pago> pagos = cargarPagos();

        int max = 0;

        for (Pago p : pagos) {
            if (p.getId() > max ) {
                max = p.getId();
            }
        }
        return max + 1;
    }
}
