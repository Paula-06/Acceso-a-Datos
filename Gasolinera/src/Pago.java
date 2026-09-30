import java.text.SimpleDateFormat;
import java.util.Date;

public class Pago {
    //Atributos
    private int id;
    private String cliente;
    private Date fecha;
    private double importe;
    private double litros;
    private Combustible combustible;

    //Constructor
    public Pago(int id, String cliente, Date fecha, double importe, double litros, Combustible combustible) {
        this.id = id;
        this.cliente = cliente;
        this.fecha = fecha;
        this.importe = importe;
        this.litros = litros;
        this.combustible = combustible;
    }

    public  String toCsv() {
        SimpleDateFormat formateo = new SimpleDateFormat("dd/MM/yyyy");
        return id + ";" +
                cliente + ";" +
                formateo.format(fecha) + ";" +
                importe + ";" +
                litros + ";" +
                combustible.name();
    }

    //Getters
    public int getId() {
        return id;
    }

    public String getNombreCliente() {
        return cliente;
    }

    public Date getFecha() {
        return fecha;
    }

    public double getImporte() {
        return importe;
    }

    public double getLitros() {
        return litros;
    }

    public Combustible getCombustible() {
        return combustible;
    }




    @Override
    public String toString() {
        return "Pago: " +
                "id: " + id +
                ", Nombre: " + cliente +
                ", fecha: " + fecha +
                ", importe: " + importe +
                ", litros: " + litros +
                ", combustible: " + combustible;
    }
}
