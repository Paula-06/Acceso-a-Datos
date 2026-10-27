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

