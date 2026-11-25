# CONTEXT.md — ConexionBBDD (JDBC de la práctica anterior)

## Qué es esta carpeta
JDBC contra **SQLite**, de la práctica anterior. No es la persistencia MySQL.

| Archivo | Función |
|---|---|
| `CreacionBD.java` | Crea `gasolinera.sqlite` con las tablas `clientes` y `pagos` |
| `PrimeraConexion.java` | Abre y valida una conexión JDBC a `gasolinera.sqlite` (`jdbc:sqlite:`); activa `PRAGMA foreign_keys = ON` y no modifica la base |

## Reglas
1. No modificar ninguno de los dos archivos.
2. No ejecutar `CreacionBD`: escribe en `gasolinera.sqlite`.
3. Solo JDBC (`java.sql`). Nada de ORM ni frameworks.
4. Las tablas `clientes` y `pagos` de SQLite no son `cliente` y `pago`
   de MySQL. No mezclarlas.

## MySQL (fase 2)
La capa MySQL aún no existe. Cuando se cree irá en un paquete nuevo,
no aquí. Ver el `README.md` de la raíz y `docs/DISENO.md`.
