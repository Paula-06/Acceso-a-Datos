Práctica 1: gestión de clientes y repostajes de una gasolinera

-- Versión del JDK --
JDK 21


-- Instrucciones de compilación --
El proyecto ha sido desarrollado con IntelliJ IDEA y JDK 21.
    // .java //


-- Instrucciones de ejecución --
Para ejecutar, en la clase Main.


-- Ubicación --


-- Formato de los ficheros -- 
El formato de los ficheros son:
### clientes.csv
id;nombre;telefono;matricula

### clientes.json
{
    [
        {id: xxxx, nombre: xxxx, telefono: xxxxxx, matricula: xxxxxx}
    ]
}

### pagos.txt
idPago;cliente;fecha;litros;importe;combustible

### pagos.json
{
    [
        {idPago: xxxx, nomclientebre: xxxx, fecha: xxxxxx, litros: xxxxxx, importe: xxxxx, combustible: Diesel/Gasolina}
    ]
}

-- Cambios realizados --
- Creación de la clase "MigrarCsvToJson"
- Crear los almacenamientos para los ficheros de json 
- Cambios en la clase Menú para que el destinatario sean los ficheros json y no csv
- Añadir en el main la migracion antes de inicializar las opciones del menú en caso de no existir los ficheros json

-- Fase 1: ampliación con MySQL (diseño + modelo Java) --
- Las 8 clases anteriores se han movido a paquetes gasolinera.* (modelo,
  persistencia, negocio, ui, app, migracion) para poder importarlas desde el
  nuevo dominio. CSV/JSON y sus mensajes no han cambiado.
- Nuevo modelo de dominio en src/dominio (34 clases, 33 de modelo + demo): comunes, estacion,
  personal, cliente, producto, venta, incidencia y demo. Las funciones del
  personal se modelan con Funcion abstracta + asignaciones con historial; las
  modalidades de producto con Producto abstracta + ModalidadProducto.
- Dinero centraliza el redondeo monetario (2 decimales, HALF_UP).
  DemoProductos muestra unidades completas frente a kilos, cobros parciales,
  devoluciones sucesivas y los rechazos esperados. No usa MySQL.
- Nuevos scripts en sql/ (01 esquema, 02 trigger de precios, 03 datos, 04
  comprobaciones) y documentación en docs/DISENO.md, README.md y AGENTS.md.
- El trigger trg_producto_precio_cambiado registra en MySQL solo cambios reales
  de precio; con InnoDB se deshace con la transacción. Java no inserta en el
  histórico. Quedan documentadas las reglas pendientes para la fase JDBC.
- Ajuste de esquema indicado: tarjeta_fidelizacion.cliente_id usa ahora
  ON DELETE RESTRICT ON UPDATE RESTRICT (antes SET NULL/CASCADE), por lo que
  borrar un cliente exige desasignar antes su tarjeta (cliente_id y
  fecha_asignacion a NULL en la misma transacción). Se ha eliminado el CHECK
  chk_empleado_no_se_supervisa: la auto-supervisión y los ciclos los sigue
  rechazando Java, y quedan marcados como pendientes de reforzar en BD en la
  fase JDBC. DISENO.md y 04_comprobaciones.sql actualizados.

