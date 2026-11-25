# GasolineraJson — Fase 1: ampliación con MySQL

Proyecto de 2.º DAM (Acceso a Datos). Parte de la aplicación ya existente
(clientes y pagos de repostajes en **CSV/JSON**) y añade el diseño relacional y
el modelo Java de dominio para gestionar estaciones, tienda, personal, clientes,
campañas, ventas, devoluciones e incidencias. La persistencia MySQL mediante JDBC
se implementará en la **siguiente fase**: hoy solo se entregan la base de datos,
el modelo y la demostración en memoria.

## Requisitos

- JDK 21 (se ha compilado con `javac 21.0.13`).
- MySQL 8.0.16 o superior (se usan `CHECK`, columnas
  `GENERATED ALWAYS AS (...) STORED` e InnoDB).
- Cliente MySQL o herramienta SQL (Workbench, DBeaver…) para ejecutar scripts.
- Opcional: IntelliJ IDEA / Eclipse con `src` como carpeta de fuentes.

No se necesita ninguna dependencia externa para compilar ni para ejecutar la
demo: el driver JDBC se incorporará en la fase 2.

## Configuración de la conexión (sin credenciales personales)

Los scripts crean la base `gasolinera_ampliacion`. En la fase 2 la conexión se
configurará con un fichero de propiedades **sin datos personales**, por ejemplo:

```properties
db.url=jdbc:mysql://localhost:3306/gasolinera_ampliacion?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
db.user=USUARIO_DEL_ENTORNO
db.password=CAMBIAR_EN_EL_ENTORNO_LOCAL
```

Ese fichero (o las variables de entorno) no debe subirse al repositorio con
credenciales reales. La clase `ConexionBBDD/PrimeraConexion.java` ya preparada
se conserva tal cual.

## Orden de preparación de la base

Desde la raíz del proyecto:

```bash
mysql -u USUARIO -p < sql/01_esquema.sql
mysql -u USUARIO -p < sql/02_triggers.sql
mysql -u USUARIO -p < sql/03_datos.sql
mysql -u USUARIO -p < sql/04_comprobaciones.sql
```

- `01_esquema.sql` crea la base y todas las tablas InnoDB (incluye `DROP TABLE
  IF EXISTS`, así que **no lo ejecutes sobre datos reales**).
- `02_triggers.sql` crea el trigger del historial de precios.
- `03_datos.sql` carga los ejemplos coherentes; el histórico queda vacío.
- `04_comprobaciones.sql` contiene consultas, la prueba del trigger (con
  `ROLLBACK` final) y, **comentadas**, las sentencias que deben fallar.
  Cópialas una a una si quieres provocar los errores esperados.

## Compilación y ejecución de la demostración Java

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")     # Linux/macOS
java -cp out dominio.demo.DemoProductos
```

En Windows (PowerShell):

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out dominio.demo.DemoProductos
```

La demo construye objetos relacionados de todo el dominio y muestra: 3 × 0,29 €
(unidades completas) frente a 1,50 kg × 12,50 € (granel), un ticket con dos
cobros, dos devoluciones parciales, un cambio de precio que no altera el ticket
antiguo y los rechazos esperados (1,5 unidades, 3 decimales en granel, carrito
vacío, ciclo de supervisión, inscripción duplicada y exceso de devolución).
No guarda ni lee MySQL.

## Aplicación de consola original (CSV/JSON)

```bash
java -cp out gasolinera.app.Main
```

El menú sigue funcionando igual: migración CSV → JSON al inicio, altas,
listados, búsquedas, pagos de repostaje y consultas. Los datos `clientes.csv`,
`pagos.csv`, `clientes.json` y `pagos.json` no se han modificado.

## Estructura

```text
GasolineraJson/
├── src/
│   ├── gasolinera/{modelo,persistencia,negocio,ui,app,migracion}/  ← práctica anterior
│   ├── ConexionBBDD/                                              ← sin tocar
│   └── dominio/{comunes,estacion,personal,cliente,producto,venta,incidencia,demo}/
├── sql/{01_esquema.sql,02_triggers.sql,03_datos.sql,04_comprobaciones.sql}
├── docs/DISENO.md        ← modelo relacional, diccionario, reglas y JDBC futuro
├── README.md             ← este fichero
├── AGENTS.md             ← notas para agentes
├── AUDITORIA_IA.odt      ← registro de intervenciones de IA
├── Cambios.md            ← historial de cambios
└── clientes.csv / clientes.json / pagos.csv / pagos.json / gasolinera.sqlite
```

## Verificación realizada en esta fase

- `javac -encoding UTF-8 -d out ...` sobre **todos** los `.java`: correcto.
- `java -cp out dominio.demo.DemoProductos`: correcto, con los rechazos
  esperados comprobados.
- Scripts SQL revisados para coherencia de claves, FKs, `CHECK`, datos de
  ejemplo y trigger. **No se han podido ejecutar aquí**: este entorno no tiene
  MySQL instalado; ejecútalos en tu máquina siguiendo el orden indicado y
  revisa los resultados esperados de `04_comprobaciones.sql`.
