import java.util.Date;

public class Pago {
    //Atributos
    private int id;
    private int idCliente;
    private Date fecha;
    private double importe;
    private double litros;
    private String combustible;

    //Constructor
    public Pago(int id, String combustible, double litros, double importe, Date fecha, int idCliente) {
        this.id = id;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.importe = importe;
        this.litros = litros;
        this.combustible = combustible;
    }
    public  String toCsv() {
        return id + ";" +
                idCliente + ";" +
                fecha + ";" +
                importe + ";" +
                litros + ";" +
                combustible;
    }

    //Getters
    public int getId() {
        return id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public Date getFecha() {
        return fecha;
    }

    public Double getImporte() {
        return importe;
    }

    public Double getLitros() {
        return litros;
    }

    public String getCombustible() {
        return combustible;
    }




    @Override
    public String toString() {
        return "Pago{" +
                "id=" + id +
                ", idCliente=" + idCliente +
                ", fecha=" + fecha +
                ", importe=" + importe +
                ", litros=" + litros +
                ", combustible='" + combustible + '\'' +
                '}';
    }
}
