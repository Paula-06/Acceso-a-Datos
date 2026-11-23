package dominio.demo;

import dominio.cliente.Campana;
import dominio.cliente.Cliente;
import dominio.cliente.TarjetaFidelizacion;
import dominio.comunes.EstadoSurtidor;
import dominio.comunes.MedioPago;
import dominio.comunes.TipoCombustible;
import dominio.estacion.Estacion;
import dominio.estacion.FichaFacturacion;
import dominio.estacion.Surtidor;
import dominio.incidencia.Incidencia;
import dominio.personal.AsignacionFuncion;
import dominio.personal.Cajero;
import dominio.personal.Empleado;
import dominio.personal.Expendedor;
import dominio.personal.JefeEstacion;
import dominio.personal.ResponsableTienda;
import dominio.producto.Producto;
import dominio.producto.ProductoEnvasado;
import dominio.producto.ProductoGranel;
import dominio.producto.Proveedor;
import dominio.producto.SuministroProveedor;
import dominio.venta.Devolucion;
import dominio.venta.LineaVenta;
import dominio.venta.Pago;
import dominio.venta.Venta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Demostración breve sin MySQL: construye objetos relacionados de todo el dominio
 * y muestra el comportamiento de las dos modalidades de producto (unidades
 * completas frente a kilos con precisión de pesada), cobros parciales y
 * devoluciones sucesivas. También prueba algunos rechazos esperados.
 */
public class DemoProductos {

    public static void main(String[] args) {
        LocalDateTime ahora = LocalDateTime.of(2026, 11, 23, 10, 30);

        // ---- Estaciones: alta conjunta con ficha y al menos un surtidor ----
        Estacion norte = new Estacion(1, "EST-01", "Gasolinera Norte", "Calle Mayor 1",
                "910000001", LocalDate.of(2015, 3, 1),
                new FichaFacturacion(1, "CFG-01", "A", 100, "Gracias por su visita"));
        norte.anyadirSurtidor(new Surtidor(1, "EQ-001", 1, TipoCombustible.GASOLINA, EstadoSurtidor.OPERATIVO));
        norte.anyadirSurtidor(new Surtidor(2, "EQ-002", 2, TipoCombustible.DIESEL, EstadoSurtidor.OPERATIVO));
        Estacion sur = new Estacion(2, "EST-02", "Gasolinera Sur", "Avenida Sur 20",
                "910000002", LocalDate.of(2018, 6, 15),
                new FichaFacturacion(2, "CFG-02", "B", 1, "Conserve su ticket"));
        sur.anyadirSurtidor(new Surtidor(3, "EQ-003", 1, TipoCombustible.GASOLINA, EstadoSurtidor.OPERATIVO));
        System.out.println("Estaciones: " + norte + " con " + norte.getSurtidores().size()
                + " surtidores; " + sur + " con " + sur.getSurtidores().size());
        System.out.println("Siguiente ticket: " + norte.getFicha().siguienteNumeroTicket());

        // ---- Personal: varias funciones, sin funciones, recuperación y supervisión ----
        Empleado jefa = new Empleado(1, "EMP-001", "11111111A", "Lucía", "600111111", null,
                LocalDate.of(2016, 1, 10), norte);
        jefa.asignarFuncion(new JefeEstacion(3), LocalDate.of(2016, 1, 10), "Nivel máximo");
        jefa.asignarFuncion(new Cajero(new BigDecimal("500.00")), LocalDate.of(2020, 5, 1), "Caja de refuerzo");
        Empleado sinFunciones = new Empleado(2, "EMP-002", "22222222B", "Diego", "600222222", null,
                LocalDate.of(2026, 11, 1), norte);
        Empleado experimentado = new Empleado(3, "EMP-003", "33333333C", "Sara", "600333333", "sara@correo.es",
                LocalDate.of(2019, 4, 20), norte);
        experimentado.asignarFuncion(new Expendedor("ACR-77", LocalDate.of(2027, 12, 31)),
                LocalDate.of(2019, 4, 20), "Acreditación vigente");
        AsignacionFuncion anterior = experimentado.asignarFuncion(
                new ResponsableTienda(new BigDecimal("2000.00")), LocalDate.of(2021, 1, 1), "Periodo anterior");
        experimentado.retirarFuncion(ResponsableTienda.class, LocalDate.of(2022, 1, 1));
        experimentado.asignarFuncion(new ResponsableTienda(new BigDecimal("3000.00")),
                LocalDate.of(2025, 2, 1), "Recuperada con nuevas condiciones");
        sinFunciones.setSupervisor(jefa);
        experimentado.setSupervisor(jefa);   // una persona supervisa a varias
        System.out.println("Jefa supervisa a: " + jefa.getNombre() + " -> " + sinFunciones.getNombre()
                + ", " + experimentado.getNombre());
        System.out.println("Funciones activas de Sara el 2026-11-23: "
                + experimentado.funcionesActivas(LocalDate.of(2026, 11, 23)));
        System.out.println("Historial intacto (responsable tienda): " + anterior);

        // ---- Clientes: con tarjeta, sin tarjeta y tarjeta sin asignar ----
        Cliente ana = new Cliente(1, "CLI-001", "Ana", "600444444", null, null);
        Cliente sinTarjeta = new Cliente(2, "CLI-002", "Marcos", null, "marcos@correo.es", null);
        TarjetaFidelizacion tarjeta = new TarjetaFidelizacion(1, "TF-001", LocalDate.of(2026, 1, 5));
        tarjeta.asignarA(ana, LocalDate.of(2026, 1, 20));
        TarjetaFidelizacion stock = new TarjetaFidelizacion(2, "TF-002", LocalDate.of(2026, 2, 1));
        Campana verano = new Campana(1, "CAMP-01", "Verano 2026", "Descuentos de temporada",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 9, 30));
        verano.inscribir(ana, LocalDate.of(2026, 6, 10));
        Campana sinInscritos = new Campana(2, "CAMP-02", "Navidad", "Campaña sin inscritos",
                LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 31));
        System.out.println("Tarjetas: " + tarjeta + ", " + stock + "; campaña " + verano
                + " con " + verano.getInscripciones().size() + " inscrito(s); "
                + sinInscritos.getCodigo() + " sin inscritos");

        // ---- Productos de ambas modalidades ----
        Producto cafe = new ProductoEnvasado(1, "PROD-001", "Café en sobre",
                new BigDecimal("0.29"), "paquete 10 sobres");
        Producto frutos = new ProductoGranel(2, "PROD-002", "Almendras a granel",
                new BigDecimal("12.50"), 2);
        Proveedor proveedor = new Proveedor(1, "PROV-001", "B12345678", "Suministros del Norte",
                "910111111", null, "Polígono 1");
        SuministroProveedor acuerdo1 = proveedor.anyadirSuministro(cafe, "REF-P1-CAFE",
                new BigDecimal("0.18"), 3);
        proveedor.anyadirSuministro(frutos, "REF-P1-ALM", new BigDecimal("9.00"), 5);
        System.out.println("Acuerdos de " + proveedor + ": " + proveedor.getSuministros().size()
                + " (primero: " + acuerdo1.getReferenciaProveedor() + ")");

        // Venta identificada con dos líneas y dos cobros
        Venta venta = new Venta(1, "A-100", norte, jefa, ana, ahora);
        LineaVenta lineaCafe = venta.anyadirLinea(cafe, new BigDecimal("3"));
        LineaVenta lineaFrutos = venta.anyadirLinea(frutos, new BigDecimal("1.50"));
        System.out.println("Línea envasada: " + lineaCafe);
        System.out.println("Línea granel:   " + lineaFrutos);
        System.out.println("Total venta " + venta.getReferenciaTicket() + ": " + venta.importeTotal() + " €");
        Pago efectivo = venta.anyadirPago(new BigDecimal("10.00"), ahora.plusMinutes(1), MedioPago.EFECTIVO, "EF-1");
        venta.anyadirPago(venta.importeTotal().subtract(efectivo.getImporte()),
                ahora.plusMinutes(2), MedioPago.TARJETA, "TJ-1");
        venta.confirmarPago();
        System.out.println("Venta " + venta.getEstado() + " con " + venta.getPagos().size() + " cobros");

        // El precio de catálogo cambia, pero el ticket no se recalcula
        cafe.cambiarPrecio(new BigDecimal("0.35"));
        System.out.println("Precio vigente de café: " + cafe.getPrecioVenta()
                + " | precio cobrado en la línea: " + lineaCafe.getPrecioUnitario());

        // Devoluciones parciales sucesivas sobre la línea de granel
        new Devolucion(1, lineaFrutos, new BigDecimal("0.40"), "Peso incorrecto",
                ahora.plusMinutes(10), jefa, false);
        new Devolucion(2, lineaFrutos, new BigDecimal("0.25"), "Producto dañado",
                ahora.plusMinutes(12), jefa, true);
        System.out.println("Devuelta de almendras: " + lineaFrutos.cantidadDevuelta()
                + " kg / " + lineaFrutos.importeDevuelto() + " € (quedan "
                + lineaFrutos.cantidadDisponible() + " kg)");

        // Venta anónima
        Venta anonima = new Venta(2, "B-1", sur, sinFunciones, null, ahora.plusHours(1));
        anonima.anyadirLinea(new ProductoEnvasado(3, "PROD-003", "Agua 1,5 L",
                new BigDecimal("0.75"), "botella"), new BigDecimal("2"));
        System.out.println("Venta anónima: " + anonima + ", cliente=" + anonima.getCliente());

        // Incidencias: sin asignar y con responsable
        Incidencia sinAsignar = new Incidencia(1, "INC-001", norte, "Surtidor 2 averiado", ahora);
        Incidencia asignada = new Incidencia(2, "INC-002", norte, "Caja sin papel", ahora.plusMinutes(5));
        asignada.asignar(experimentado);
        System.out.println("Incidencias: " + sinAsignar + " | " + asignada);

        // ---- Rechazos esperados (no detienen la demostración) ----
        comprobarRechazo("1,5 unidades de producto envasado",
                () -> cafe.validarCantidad(new BigDecimal("1.5")));
        comprobarRechazo("3 decimales en granel con precisión 2",
                () -> frutos.validarCantidad(new BigDecimal("1.234")));
        comprobarRechazo("carrito vacío",
                () -> new Venta(3, "C-1", norte, jefa, null, ahora).confirmarPago());
        comprobarRechazo("supervisor formando ciclo", () -> jefa.setSupervisor(experimentado));
        comprobarRechazo("dos inscripciones iguales",
                () -> verano.inscribir(ana, LocalDate.of(2026, 7, 1)));
        comprobarRechazo("devolver más de lo comprado",
                () -> new Devolucion(3, lineaCafe, new BigDecimal("9"), "Error",
                        ahora.plusMinutes(15), jefa, false));

        System.out.println("Demo terminada.");
    }

    private static void comprobarRechazo(String descripcion, Runnable accion) {
        try {
            accion.run();
            System.out.println("RECHAZO NO PROBADO: " + descripcion);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Rechazado correctamente (" + descripcion + "): " + e.getMessage());
        }
    }
}
