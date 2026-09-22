import java.sql.Date;

public class Pago {
    //Atributos
    private int id;
    private int idCliente;
    private Date fecha;
    private Double importe;
    private Double litros;
    private String combustible;

    //Constructor
    public Pago(int id, String combustible, Double litros, Double importe, Date fecha, int idCliente) {
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
        return id + "\t" +
        idCliente + "\t" +
        fecha + "\t" +
        importe + "\t" +
        litros + "\t" +
                combustible;
    }
}
