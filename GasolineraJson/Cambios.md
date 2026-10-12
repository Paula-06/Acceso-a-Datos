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

-- Decisiones de diseño tomadas --
- Uso de clases Cliente y Pago como entidades.
- Uso de gestores para la lógica de negocio.
- Uso de interfaces para desacoplar la persistencia.
- Uso de repositorios para lectura y escritura en ficheros, uno para cada.
- Uso de migracion para la traduccion de csv a json
