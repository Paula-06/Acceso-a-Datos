import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RepoJsonP implements PagosDAO{
    static final String FICHERO = "pagos.json";
    private final Path ruta = Path.of(FICHERO);
    //Debe


    //Carga los Pagos ya almacenados
    public List<Pago> cargarPagos() {
        List<Pago> pagos = new ArrayList<>();
        if (!Files.exists(ruta)) {
            return pagos;
        }
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        try (BufferedReader br = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String lineas;
            while ((lineas = br.readLine()) != null) {
                if (lineas.isBlank()) {
                    continue; // ignorar las líneas que están vacías o que solo tienen espacios en blanco.
                }

                String[] datos = lineas.split(",");
                if (datos.length != 6) {
                    continue;
                }
                int id = Integer.parseInt(datos[0]);
                String cliente = datos[1];
                Date fecha = formato.parse(datos[2]);
                double importe = Double.parseDouble(datos[3]);
                double litros = Double.parseDouble(datos[4]);
                Combustible combustible = Combustible.valueOf(datos[5]);

                Pago pago = new Pago(id, cliente, fecha, importe, litros, combustible);
                pagos.add(pago);
            }


        } catch (IOException | ParseException e) {
            System.out.println(e.getMessage());;
        }
        return pagos;
    }

    //Guarda Pago
    public void guardarPago(Pago pago) {
        Path ruta = Path.of(FICHERO);

        try (BufferedWriter escritor = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {



            escritor.write(pago.toJson());
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
        } return max;
    }
}
