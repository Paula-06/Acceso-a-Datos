package gasolinera.modelo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Modelo único de la gasolinera: un registro puede ser un cliente o un pago.
 * El enum {@link Combustible} se define aquí dentro para reducir el número de clases.
 * Los formatos de texto (CSV/JSON y salida por consola) son idénticos a los de las
 * antiguas clases Cliente y Pago.
 */
public class RegistroGasolinera implements Comparable<RegistroGasolinera> {

    /** Tipos de combustible admitidos (antes en Combustible.java). */
    public enum Combustible {
        Gasolina,
        Diesel
    }

    // Un único conjunto de atributos para los dos tipos de registro:
    // los campos de cliente se usan en las altas y los de pago en los repostajes.
    private final int id;
    private final String nombre;           // cliente, o nombre del cliente en un pago
    private final String telefono;         // solo clientes
    private final String matricula;        // solo clientes
    private final Date fecha;              // solo pagos
    private final double importe;          // solo pagos
    private final double litros;           // solo pagos
    private final Combustible combustible; // solo pagos
    private final boolean esPago;          // distingue el tipo de registro

    private RegistroGasolinera(int id, String nombre, String telefono, String matricula,
                               Date fecha, double importe, double litros,
                               Combustible combustible, boolean esPago) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.matricula = matricula;
        this.fecha = fecha;
        this.importe = importe;
        this.litros = litros;
        this.combustible = combustible;
        this.esPago = esPago;
    }

    /** Alta de cliente: equivalente al antiguo constructor de Cliente. */
    public static RegistroGasolinera nuevoCliente(int id, String nombre, String telefono, String matricula) {
        return new RegistroGasolinera(id, nombre, telefono, matricula, null, 0, 0, null, false);
    }

    /** Pago de repostaje: equivalente al antiguo constructor de Pago. */
    public static RegistroGasolinera nuevoPago(int id, String cliente, Date fecha,
                                               double importe, double litros, Combustible combustible) {
        return new RegistroGasolinera(id, cliente, null, null, fecha, importe, litros, combustible, true);
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getMatricula() {
        return matricula;
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

    public boolean esPago() {
        return esPago;
    }

    /** Serialización CSV: mantiene el formato exacto de Cliente.toCsv() y Pago.toCsv(). */
    public String toCsv() {
        if (esPago) {
            SimpleDateFormat formateo = new SimpleDateFormat("dd/MM/yyyy");
            return id + ";" +
                    nombre + ";" +
                    formateo.format(fecha) + ";" +
                    importe + ";" +
                    litros + ";" +
                    combustible.name();
        }
        return id + ";" +
                nombre + ";" +
                telefono + ";" +
                matricula.toUpperCase();
    }

    /** Serialización JSON: mantiene el formato exacto de Cliente.toJson() y Pago.toJson(). */
    public String toJson() {
        if (esPago) {
            SimpleDateFormat formateo = new SimpleDateFormat("dd/MM/yyyy");
            return " { id: " + id + ", cliente: " + nombre +
                    ", fecha: " + formateo.format(fecha) +
                    ", importe: " + importe +
                    ", litros: " + litros +
                    ", combustible: " + combustible + " }";
        }
        return "{ id: " + id + ", nombre: " + nombre +
                ", telefono: " + telefono +
                ", matricula: " + matricula.toUpperCase() + "}";
    }

    /** Salida por consola: mantiene el formato exacto de Cliente.toString() y Pago.toString(). */
    @Override
    public String toString() {
        if (esPago) {
            SimpleDateFormat formateo = new SimpleDateFormat("dd/MM/yyyy");
            return "Pago: " +
                    "id: " + id +
                    ", Nombre: " + nombre +
                    ", fecha: " + formateo.format(fecha) +
                    ", importe: " + importe +
                    ", litros: " + litros +
                    ", combustible: " + combustible.name();
        }
        return "ID: " + id +
                ", Nombre: " + nombre +
                ", Teléfono: " + telefono +
                ", Matrícula: " + matricula;
    }

    /** Misma comparación que el antiguo Cliente: nombre (sin distinción de case) y id. */
    @Override
    public int compareTo(RegistroGasolinera otro) {
        int comparacionNombre = this.nombre.compareToIgnoreCase(otro.nombre);
        if (comparacionNombre != 0) {
            return comparacionNombre;
        }
        return Integer.compare(this.id, otro.id);
    }
}
