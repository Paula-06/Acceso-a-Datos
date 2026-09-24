import java.sql.*;

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
        this.combustible = combustible;
        this.litros = litros;
        this.importe = importe;
        this.fecha = fecha;
        this.idCliente = idCliente;
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
