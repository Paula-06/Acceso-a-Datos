# AGENTS.md — notas para agentes (OpenCode / Copilot)

## Estado del proyecto

- Práctica de Acceso a Datos (2.º DAM). **Fase 1 entregada**: diseño relacional,
  scripts SQL, modelo Java de dominio, demo y documentación.
- La **fase 2** implementará los métodos JDBC. **Todavía no** hay DAOs MySQL ni
  llamadas SQL desde Java: no las añadas ahora.

## Reglas de modificación

1. No tocar `.idea/`, `.settings/`, `bin/`, `.classpath`, `.project`,
   `GasolineraJson.iml` ni los ficheros de datos (`clientes.csv`, `pagos.csv`,
   `clientes.json`, `pagos.json`, `gasolinera.sqlite`). El `bin/` está obsoleto:
   compilar siempre a `out/` o a un directorio temporal.
2. `src` es la carpeta de fuentes. El código antiguo vive en paquetes
   `gasolinera.*` y el nuevo dominio en `dominio.*`. Si añades clases, declara
   el `package` coherente con su carpeta: Java no permite importar clases del
   *default package* desde un paquete nombrado.
3. Las clases del dominio **no** abren conexiones ni ejecutan SQL. La lógica de
   negocio y la persistencia siguen separadas: la capa MySQL irá en un paquete
   nuevo de persistencia cuando se implemente.
4. No cambiar mensajes, formatos CSV/JSON ni validaciones de la aplicación de
   consola original sin una petición explícita. Los pagos de repostaje de
   `RegistroGasolinera` y los cobros de tienda `dominio.venta.Pago` son cosas
   distintas: no fusionarlos ni reinterpretar registros antiguos.
5. Mantener el redondeo monetario centralizado en `dominio.comunes.Dinero`
   (2 decimales, `HALF_UP`) y las reglas de cantidad en
   `dominio.comunes.ModalidadProducto`.
6. Todas las tablas nuevas deben tener PK `INT AUTO_INCREMENT` única y
   referencias de negocio con `UNIQUE`. Usar InnoDB. No desactivar FKs.
7. El historial de precios lo genera el trigger de `sql/02_triggers.sql`.
   Ningún código Java debe insertar, editar ni borrar filas de
   `historial_precio`.

## Comandos útiles

```powershell
# Compilar todo (JDK 21)
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName

# Demo del dominio
java -cp out dominio.demo.DemoProductos

# Aplicación de consola original
java -cp out gasolinera.app.Main
```

Scripts SQL, en orden: `sql/01_esquema.sql` → `sql/02_triggers.sql` →
`sql/03_datos.sql` → `sql/04_comprobaciones.sql`. En este entorno de desarrollo
no hay MySQL instalado: los scripts se revisan por inspección; ejecútalos en el
entorno del alumno antes de dar por verificada la base.

## Antes de dar una tarea por terminada

- Compilar **todos** los `.java` con `javac -encoding UTF-8`.
- Ejecutar la demo y comprobar que aparecen los rechazos esperados.
- Si se toca el esquema, actualizar `docs/DISENO.md`, los comentarios de
  `sql/04_comprobaciones.sql` y `Cambios.md`.
- Explicar qué reglas quedan pendientes para la fase JDBC en lugar de presentar
  como automáticas comprobaciones que hoy solo hace Java.
