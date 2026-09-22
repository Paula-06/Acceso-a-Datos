import java.sql.*;
import java.time.LocalDate;

public class Pago {
    //Atributos
    private int id;
    private int idCliente;
    private LocalDate fecha;
    private double importe;
    private double litros;
    private String combustible;

    //Constructor
    public Pago(int id, String combustible, double litros, double importe, LocalDate fecha, int idCliente) {
        this.id = id;
        this.combustible = combustible;
        this.litros = litros;
        this.importe = importe;
        this.fecha = fecha;
        this.idCliente = idCliente;
    }


    //Getters
    public int getId() {
        return id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public Date getFecha() {
        return getFecha();
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
