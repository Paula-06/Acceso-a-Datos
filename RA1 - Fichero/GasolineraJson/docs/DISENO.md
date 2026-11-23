# Diseño de la Fase 1 — Ampliación de GasolineraJson

Este documento describe el modelo relacional (sin diagrama entidad-relación), el
diccionario de datos, las decisiones del modelo Java y el estado de cada regla de
negocio. Los scripts ejecutables están en `../sql/` y deben ejecutarse en este
orden: `01_esquema.sql`, `02_triggers.sql`, `03_datos.sql` y, por último,
`04_comprobaciones.sql`.

Convención común a **todas** las tablas: la clave primaria es un único `INT
AUTO_INCREMENT` llamado `id`. Los códigos, números y referencias de negocio son
`VARCHAR` con `UNIQUE`, nunca claves primarias. El identificador técnico se
asigna en MySQL; en Java se modela como `Integer` que vale `null` hasta que se
persiste. Todas las tablas son `InnoDB` (MySQL 8.0.16+ para `CHECK` y columnas
generadas).

---

## 1. Modelo relacional y diccionario de datos

### 1.1 Estaciones y facturación

| Tabla | Finalidad | Columnas (tipo; Obl/Opc; default) | Claves / restricciones | Referencias |
|---|---|---|---|---|
| `ficha_facturacion` | Configuración de emisión de tickets de **una** estación. No son datos fiscales de la empresa. | `id` INT; `codigo_configuracion` VARCHAR(20) O; `serie_tickets` VARCHAR(10) O; `siguiente_numero` INT O (=1); `texto_pie` VARCHAR(200) Op | PK `id`; `UNIQUE(codigo_configuracion)` (soporte identifica la ficha); `CHECK(siguiente_numero>0)` | La referencia a estación está en `estacion.ficha_id` |
| `estacion` | Datos de la gasolinera: código interno, nombre, dirección, teléfono, apertura. | `id` INT; `codigo_interno` VARCHAR(20) O; `nombre` VARCHAR(100) O; `direccion` VARCHAR(200) Op; `telefono` VARCHAR(20) Op; `fecha_apertura` DATE O; `ficha_id` INT O | PK `id`; `UNIQUE(codigo_interno)`; `UNIQUE(ficha_id)` → una ficha no se comparte | `ficha_id` → `ficha_facturacion(id)` `RESTRICT` |
| `surtidor` | Equipo de una estación. | `id` INT; `codigo_equipo` VARCHAR(20) O; `numero_visible` INT O; `tipo_combustible` ENUM O; `estado` ENUM O (='OPERATIVO'); `estacion_id` INT O | PK `id`; `UNIQUE(codigo_equipo)` (toda la empresa); `UNIQUE(estacion_id, numero_visible)` (repetible en otra estación); `CHECK(numero_visible>0)` | `estacion_id` → `estacion(id)` `RESTRICT` |

**Alta conjunta estación + ficha.** La FK obligatoria y única está en `estacion`,
así que MySQL impide una estación sin ficha y una ficha compartida. MySQL no
tiene claves foráneas diferidas: una ficha aislada podría insertarse sola. La
fase JDBC lo resolverá insertando ficha y estación **en una sola transacción** y
exponiendo una única operación `darDeAltaEstacion(...)`; si algún paso falla, se
hace `ROLLBACK` y no queda una operación confirmada incompleta. Se prefiere esta
dirección de la FK antes que un bucle de referencias impossible de cargar.

### 1.2 Personal y organización del trabajo

| Tabla | Finalidad | Columnas | Claves / restricciones | Referencias |
|---|---|---|---|---|
| `empleado` | Identidad estable del personal (número interno, DNI, nombre, contacto, incorporación, estación actual, supervisor actual). | `id`; `numero_interno` VARCHAR(20) O; `documento_identidad` VARCHAR(30) O; `nombre` VARCHAR(100) O; `telefono` VARCHAR(20) Op; `email` VARCHAR(150) Op; `fecha_incorporacion` DATE O; `estacion_id` INT O; `supervisor_id` INT Op | PK; `UNIQUE(numero_interno)`; `UNIQUE(documento_identidad)`; `CHECK(telefono IS NOT NULL OR email IS NOT NULL)`; auto-supervisión controlada solo en Java (`Empleado.setSupervisor`) | `estacion_id` → `estacion` `RESTRICT`; `supervisor_id` → `empleado` `ON DELETE SET NULL` |
| `funcion` | Catálogo de tipos de función (4 filas; ampliable sin cambiar el código). | `id`; `codigo` VARCHAR(30) O; `descripcion` VARCHAR(100) O | PK; `UNIQUE(codigo)` | — |
| `asignacion_funcion` | Periodo en que una persona ejerce una función con unas condiciones. Alta, baja y reincorporación = filas distintas. | `id`; `empleado_id` INT O; `funcion_id` INT O; `fecha_inicio` DATE O; `fecha_fin` DATE Op; `condiciones` VARCHAR(200) Op; `abierta` INT generada | PK; `CHECK(fecha_fin IS NULL OR fecha_fin>=fecha_inicio)`; `UNIQUE(empleado_id, funcion_id, abierta)` evita dos periodos abiertos de la misma función | `empleado_id` → `empleado` `RESTRICT`; `funcion_id` → `funcion` `RESTRICT` |
| `funcion_cajero` | `limite_devoluciones` DECIMAL(10,2) O, `>0`. | `id`; `asignacion_id` INT O | PK; `UNIQUE(asignacion_id)`; CHECK | `asignacion_id` → `asignacion_funcion` `CASCADE` |
| `funcion_expendedor` | `numero_acreditacion` VARCHAR(30) O, `caducidad` DATE O. | ídem | PK; `UNIQUE(asignacion_id)`; `UNIQUE(numero_acreditacion)` | ídem |
| `funcion_responsable_tienda` | `presupuesto_compras` DECIMAL(10,2) O, `>0`. | ídem | PK; `UNIQUE(asignacion_id)`; CHECK | ídem |
| `funcion_jefe_estacion` | `nivel_autorizacion` TINYINT O, entre 1 y 3. | ídem | PK; `UNIQUE(asignacion_id)`; CHECK | ídem |

Una persona puede tener simultáneas **funciones distintas** (Sara: expendedora,
responsable y cajera en los datos), ninguna (Diego tras darse de baja en enero) o
recuperar una función cerrada con condiciones nuevas (Sara en 2025). El historial
nunca se reescribe: `retirarFuncion` solo rellena `fecha_fin`.

La supervisión actual es un FK autorreferencial con `ON DELETE SET NULL`; no se
guarda historial de supervisión. **No hay `CHECK` en base de datos** que prohíba
`A → A`: la validación vive en `Empleado.setSupervisor`, que rechaza tanto la
auto-supervisión como los ciclos directos e indirectos. Por tanto, una
modificación SQL directa podría introducirlos; se registra como regla pendiente
de reforzar en la fase JDBC (ver §4).

### 1.3 Clientes, tarjetas y campañas

| Tabla | Finalidad | Columnas | Claves / restricciones | Referencias |
|---|---|---|---|---|
| `cliente` | Cliente registrado (las compras anónimas **no** crean fila). | `id`; `codigo` VARCHAR(20) O; `nombre` VARCHAR(100) O; `telefono` Op; `email` Op; `direccion` Op | PK; `UNIQUE(codigo)`; `CHECK(telefono IS NOT NULL OR email IS NOT NULL)` | — |
| `tarjeta_fidelizacion` | Tarjeta emitida; puede estar en stock. | `id`; `numero` VARCHAR(20) O; `fecha_emision` DATE O; `cliente_id` Op; `fecha_asignacion` DATE Op | PK; `UNIQUE(numero)`; `UNIQUE(cliente_id)` (una tarjeta por cliente); `CHECK` pareja cliente↔fecha | `cliente_id` → `cliente` `ON DELETE RESTRICT ON UPDATE RESTRICT` |
| `campana` | Campaña promocional; solo se gestiona la inscripción. | `id`; `codigo` O; `nombre` O; `descripcion` Op; `fecha_inicio` DATE O; `fecha_fin` DATE O | PK; `UNIQUE(codigo)`; `CHECK(fecha_fin>=fecha_inicio)` | — |
| `inscripcion` | Participación de un cliente en una campaña. | `id`; `campana_id` O; `cliente_id` O; `fecha` O | PK; `UNIQUE(campana_id, cliente_id)` | ambas `RESTRICT` |

**Criterio documentado de contacto:** código y nombre son obligatorios; pueden
faltar teléfono, email y dirección, pero se exige al menos teléfono o email para
poder contactar al titular. La inscripción solo exige cliente registrado, no
tarjeta.

Con `ON DELETE RESTRICT`, no se puede borrar un cliente que tenga una tarjeta
asignada: hay que desasignarla antes en la misma transacción, poniendo a `NULL`
`cliente_id` **y** `fecha_asignacion` para no romper `chk_tarjeta_asignacion`.
Así se conserva la trazabilidad de la asignación. `ON UPDATE RESTRICT` impide
además cambiar el `id` técnico del cliente (los autoincrementales no se
actualizan en la aplicación).

### 1.4 Catálogo, proveedores e histórico

| Tabla | Finalidad | Columnas | Claves / restricciones | Referencias |
|---|---|---|---|---|
| `producto` | Catálogo. `modalidad` es estable; un producto retirado sigue identificable. | `id`; `referencia` VARCHAR(30) O; `descripcion` VARCHAR(150) O; `precio_venta` DECIMAL(10,2) O; `estado` ENUM O (='ACTIVO'); `modalidad` ENUM O; `formato_envase` Op; `precision_pesada` TINYINT Op | PK; `UNIQUE(referencia)`; `CHECK(precio_venta>0)`; `CHECK` coherencia modalidad↔formato/precisión (0..3) | — |
| `historial_precio` | Entradas generadas **por MySQL** al cambiar el precio. | `id`; `producto_id` O; `precio_anterior` DECIMAL(10,2) O; `precio_nuevo` O; `fecha_cambio` DATETIME O (=`CURRENT_TIMESTAMP`) | PK; `CHECK(precio_anterior<>precio_nuevo)`; sin operaciones ordinarias de edición/borrado | `producto_id` → `producto` `RESTRICT` |
| `proveedor` | Solo proveedores con acuerdo (no contactos en negociación). | `id`; `codigo_interno` O; `identificacion_fiscal` O; `razon_social` O; `telefono` Op; `email` Op; `direccion` Op | PK; `UNIQUE` en código y fiscal; `CHECK` al menos teléfono o email | — |
| `suministro_proveedor` | Acuerdo proveedor↔producto: referencia del proveedor, precio de compra, plazo y vigencia. | `id`; `proveedor_id` O; `producto_id` O; `referencia_proveedor` VARCHAR(40) O; `precio_compra` DECIMAL(10,2) O; `plazo_entrega_dias` INT O (=0); `vigente` BOOLEAN O (=TRUE); `vigente_flag` generada | PK; `UNIQUE(proveedor_id, producto_id, vigente_flag)` → no duplicar el acuerdo **vigente**; CHECK precio>0 y plazo>=0 | ambas `RESTRICT` |

No se guarda un historial completo de condiciones de suministro: basta con
conservar los acuerdos antiguos marcados `vigente = FALSE`, lo que mantiene la
referencia de los productos retirados.

### 1.5 Ventas, cobros y devoluciones

| Tabla | Finalidad | Columnas | Claves / restricciones | Referencias |
|---|---|---|---|---|
| `venta` | Ticket de tienda en una estación, registrado por un empleado; cliente opcional. | `id`; `referencia_ticket` VARCHAR(30) O; `estacion_id` O; `empleado_id` O; `cliente_id` Op; `fecha_hora` DATETIME O; `estado` ENUM O (='PENDIENTE') | PK; `UNIQUE(referencia_ticket)` | las tres `RESTRICT` |
| `linea_venta` | Línea de ticket con precio **realmente cobrado**; `importe` es generado. | `id`; `venta_id` O; `producto_id` O; `numero_orden` INT O; `cantidad` DECIMAL(12,3) O; `precio_unitario` DECIMAL(10,2) O; `importe` DECIMAL(12,2) GENERATED `ROUND(cantidad*precio_unitario,2)` | PK; `UNIQUE(venta_id, numero_orden)`; CHECK cantidad>0, precio>0, orden>0 | ambas `RESTRICT` |
| `pago` | Cobro de tienda: importe, fecha/hora y medio. Uno o varios por venta. | `id`; `venta_id` O; `importe` DECIMAL(10,2) O; `fecha_hora` DATETIME O; `medio_pago` ENUM O; `referencia` VARCHAR(40) Op | PK; `CHECK(importe>0)` | `venta_id` → `venta` `RESTRICT` |
| `devolucion` | Reintegro parcial de **una línea**; no modifica la línea original. | `id`; `linea_venta_id` O; `cantidad` DECIMAL(12,3) O; `motivo` VARCHAR(200) O; `fecha_hora` O; `empleado_id` O; `importe_reintegrado` DECIMAL(12,2) O; `apta_para_venta` BOOLEAN O (=FALSE) | PK; CHECK cantidad>0, importe>0 | línea y empleado `RESTRICT` |

La estación se guarda en `venta`, no se deduce del empleado: un cambio de
adscripción posterior no altera tickets anteriores. El importe de la línea se
calcula con el precio cobrado, nunca con el precio vigente del catálogo, de modo
que el cambio de precio demostrado en `04_comprobaciones.sql` no altera tickets
antiguos.

**Convivencia con `Pagos` de la práctica anterior.** Los pagos de repostaje
siguen en `pagos.csv` / `pagos.json` gestionados por `RegistroGasolinera`
(cliente, fecha, importe, litros, combustible). La tabla `pago` es exclusiva de
cobros de tienda y su clase es `dominio.venta.Pago`. No se migran ni se
reinterpretan registros antiguos en esta fase: sería una pérdida de información
(litros/combustible). En una fase posterior se podría añadir un tipo de pago
común con columnas opcionales específicas, documentando la correspondencia.

### 1.6 Incidencias

| Tabla | Finalidad | Columnas | Claves / restricciones | Referencias |
|---|---|---|---|---|
| `incidencia` | Incidencia de una estación con responsable **actual** opcional. | `id`; `referencia` VARCHAR(20) O; `estacion_id` O; `descripcion` VARCHAR(250) O; `fecha_apertura` DATETIME O; `estado` ENUM O (='ABIERTA'); `empleado_responsable_id` Op | PK; `UNIQUE(referencia)` | estación `RESTRICT`; responsable `ON DELETE SET NULL` |

Responsable, supervisor y funciones son tres relaciones distintas con fines
diferentes; no se identifican entre sí.

---

## 2. Justificación del modelo Java

El dominio tiene **34 clases** (33 de modelo + `DemoProductos`), por encima de
las 15 de referencia. No son clases vacías: cada una responde a una entidad, una
asociación con datos propios, una jerarquía o un enum con comportamiento.
Tampoco se cuentan interfaces DAO, utilidades de conexión ni pruebas.

| Paquete | Clases | Decisión |
|---|---|---|
| `dominio.comunes` | `Dinero`, `ModalidadProducto`, `MedioPago`, `EstadoVenta`, `EstadoSurtidor`, `EstadoIncidencia`, `EstadoProducto`, `TipoCombustible` | `Dinero` centraliza el redondeo (2 decimales, `HALF_UP`) para precios, importes, cobros y devoluciones. Los enums tipan estados y evitan cadenas libres. |
| `dominio.estacion` | `Estacion`, `FichaFacturacion`, `Surtidor` | La ficha se enlaza en el constructor de la estación: alta conjunta en memoria. `anyadirSurtidor` valida el número visible por estación; `retirarSurtidor` impide dejarla sin equipos. |
| `dominio.personal` | `Empleado`, `Funcion` (abstracta), `Cajero`, `Expendedor`, `ResponsableTienda`, `JefeEstacion`, `AsignacionFuncion` | `Funcion` es abstracta: cada subtipo guarda sus condiciones específicas y añadir otra función no obliga a tocar `Empleado`. `AsignacionFuncion` separa *persona* de *periodo con condiciones*, por lo que baja y reincorporación son filas nuevas. `Empleado.setSupervisor` rechaza auto-supervisión y ciclos. |
| `dominio.cliente` | `Cliente`, `TarjetaFidelizacion`, `Campana`, `Inscripcion` | `Inscripcion` es una clase porque la asociación tiene fecha propia. La tarjeta puede existir sin titular y se asigna comprobando ambos sentidos del 1:1. |
| `dominio.producto` | `Producto` (abstracta), `ProductoEnvasado`, `ProductoGranel`, `Proveedor`, `SuministroProveedor`, `HistorialPrecio` | La modalidad es **estable** y se modela con jerarquía + `ModalidadProducto` (enum con comportamiento). `LineaVenta` y `Devolucion` llaman a `producto.validarCantidad(...)` y no repiten reglas. `HistorialPrecio` solo se leerá por JDBC; Java no lo inserta. |
| `dominio.venta` | `Venta`, `LineaVenta`, `Pago`, `Devolucion` | `Venta` controla carrito vacío y que la suma de cobros iguale el total antes de pasar a `PAGADA`. `LineaVenta` congela `precioUnitario` e `importe`. |
| `dominio.incidencia` | `Incidencia` | Guarda solo el responsable actual. |

**Referencias entre objetos.** Se usan referencias directas (`Estacion` dentro
de `Empleado`, `Producto` dentro de `LineaVenta`) para que el modelo sea
navegable sin consultas. Los `Integer id` son nulos hasta que la capa JDBC
asigna el autoincremental. Las relaciones 1:N se exponen como listas no
modificables; las asociaciones con datos propios (`Inscripcion`,
`AsignacionFuncion`, `SuministroProveedor`) son clases. Las relaciones opcionales
son `null` (cliente anónimo, tarjeta en stock, supervisor ausente, incidencia sin
responsable, fecha de fin de un periodo abierto).

**Validación y cálculo implementados** (sin SQL): unidades completas para
envasados (se rechaza `1,5`), decimales limitados por `precisionPesada` en
granel (se rechaza `1,234` con precisión 2), importes con `Dinero`, total de
cobros, devoluciones acumuladas contra cantidad e importe disponibles de la
línea, fechas de campañas y periodos, duplicados de inscripción y ciclos de
supervisión. `dominio.demo.DemoProductos` construye objetos relacionados de todo
el dominio y muestra ambos cálculos (3 × 0,29 € = 0,87 € frente a 1,50 kg ×
12,50 € = 18,75 €), dos cobros, dos devoluciones parciales y los rechazos
esperados. No guarda ni lee MySQL.

Las clases del dominio **no abren conexiones ni ejecutan SQL**. La conexión ya
preparada en `ConexionBBDD` se conserva; los métodos JDBC de inserción, consulta,
actualización y borrado se implementarán en la siguiente fase.

**Compatibilidad.** Las 8 clases de la práctica anterior se movieron a paquetes
`gasolinera.*` (necesario para que el dominio pueda importarlas) sin cambiar su
comportamiento: CSV/JSON, migración, validaciones y mensajes siguen iguales. Se
verificó compilación conjunta y la demo. `bin/` no se ha regenerado: recompilar
desde el IDE o con `javac -d out ...`.

---

## 3. Registro automático de precios

`02_triggers.sql` crea `trg_producto_precio_cambiado`, un `AFTER UPDATE` sobre
`producto` que inserta en `historial_precio` solo cuando `NEW.precio_venta <>
OLD.precio_venta`. Funciona también si el precio se cambia desde un cliente SQL.
No dispara con `INSERT` (alta inicial) ni con `UPDATE` de descripción u otros
campos; guardar el mismo precio tampoco genera fila. Java no inserta en el
histórico. Al ser InnoDB, un `ROLLBACK` elimina a la vez el cambio de precio y su
entrada histórica, tal como se comprueba en la sección B de
`04_comprobaciones.sql`.

---

## 4. Reglas: garantizadas y pendientes

| Regla | Dónde queda garantizada |
|---|---|
| PK única autoincremental en **todas** las tablas, incluidas asociaciones/históricos | `01_esquema.sql` |
| Códigos/referencias de negocio únicos (estación, ficha, equipo, empleado, cliente, tarjeta, campaña, producto, proveedor, ticket, incidencia) | `UNIQUE` en BD |
| Estación con exactamente una ficha; ficha no compartida | FK `NOT NULL UNIQUE` en `estacion` + transacción de alta conjunta en la fase JDBC para la ficha huérfana |
| Estación con al menos un surtidor | **Pendiente** en BD; validado en `Estacion` y se planifica trigger/control transaccional |
| Número de surtidor único por estación, repetible entre estaciones | `UNIQUE(estacion_id, numero_visible)` |
| Empleado adscrito a una estación; estación conservada en ventas antiguas | FK `NOT NULL` en `empleado` y `estacion_id` copiado en `venta` |
| Auto-supervisión prohibida | **Pendiente en BD** (se quitó el `CHECK`); la valida `Empleado.setSupervisor` en Java, incluidos ciclos indirectos |
| Funciones simultáneas distintas permitidas; duplicar función abierta no | `UNIQUE(empleado_id, funcion_id, abierta)`; solapes con periodos cerrados **pendientes** |
| Historial de funciones inalterable | Solo se rellena `fecha_fin`; no se borran filas |
| Cliente con Contacto mínimo | `CHECK` en BD y validación en Java |
| Una tarjeta por cliente y un titular por tarjeta; tarjetas sin asignar | `UNIQUE(numero)`, `UNIQUE(cliente_id)`, `CHECK` fecha↔cliente y FK `RESTRICT` (borrar cliente exige desasignar antes) |
| Campaña con fechas válidas; inscripción única | `CHECK` y `UNIQUE(campana_id, cliente_id)` |
| Precio siempre > 0 y coherencia de modalidad | `CHECK` en `producto` + `Dinero` en Java |
| Cantidad entera en envasados y precisión en granel | Java (`ModalidadProducto`); en BD solo `cantidad>0`, control declarativo completo pendiente |
| Sin duplicar acuerdo vigente; acuerdos antiguos conservados | `UNIQUE(proveedor_id, producto_id, vigente_flag)` |
| Proveedor/producto aislados **no** admitidos | **Pendiente**: alta conjunta y baja del último acuerdo en la fase JDBC |
| Venta con al menos una línea; línea con precio cobrado congelado | `Venta.confirmarPago()` + `linea_venta.precio_unitario`; carrito vacío pendiente de control en BD |
| Cobro exacto antes de `PAGADA`; cobros parciales permitidos | `Venta.confirmarPago()`; se trasladará a JDBC |
| Devolución ≤ cantidad e importe originales, con modalidad y precio de venta | Java (`LineaVenta`) con validación acumulada; **pendiente** en MySQL (trigger con `SELECT ... FOR UPDATE`) |
| Cambios de precio automáticos, solo si hay cambio real, reversibles | Trigger InnoDB (§3) |
| Incidencia sin responsable o con uno actual | `empleado_responsable_id` nullable con `ON DELETE SET NULL` |

### Propuesta para las reglas pendientes (siguiente fase)

1. **Altas conjuntas** (estación+ficha, proveedor+primer acuerdo, producto+primer
   acuerdo): un único método DAO abre transacción, inserta en orden dependiente
   y hace `COMMIT` solo si todo funciona; cualquier excepción provoca `ROLLBACK`.
2. **Bajas del último elemento** (surtidor, acuerdo): `BEFORE DELETE` con
   `SELECT COUNT(*) ... FOR UPDATE` para serializar operaciones concurrentes.
3. **Auto-supervisión, ciclos de supervisión y solapes de periodos**: la Java ya
   los rechaza; al no haber `CHECK`, la fase JDBC debe comprobarlos dentro de la
   transacción (o añadir un `BEFORE INSERT/UPDATE` con bloqueo) para que también
   los impida una modificación SQL directa.
4. **Devoluciones acumuladas**: trigger `BEFORE INSERT` que sume las devoluciones
   existentes con `FOR UPDATE` y compare cantidad e importe con la línea.
5. **Importe cobrado y carrito vacío**: comprobar en la transacción que confirma
   la venta y actualizar `estado` a `PAGADA` solo si cuadra.

No se usan referencias circulares imposibles de cargar ni se desactivan claves
foráneas para maquillar la carga de datos.

---

## 5. Tres operaciones futuras mediante JDBC

| Operación | Datos de entrada | Reglas que debe respetar |
|---|---|---|
| `darDeAltaEstacion(Estacion e)` | Código, nombre, dirección, teléfono, fecha de apertura y los datos de su ficha (código de configuración, serie, siguiente número, pie). | Insertar ficha y estación en una transacción; verificar unicidad de ambos códigos; `ROLLBACK` si falla cualquiera; exigir al menos un surtidor antes de confirmar la alta (o hacerlo en la misma transacción). |
| `registrarVenta(Venta v)` | Referencia de ticket, estación, empleado, cliente opcional, fecha/hora, líneas (producto, cantidad, precio cobrado) y cobros. | Un solo `COMMIT`; la línea debe validar cantidad según modalidad y congelar `precio_unitario`; el importe se calcula con `Dinero`; rechazar carrito vacío y confirmar `PAGADA` solo si la suma de cobros coincide; no tocar el histórico de precios. |
| `registrarDevolucion(Devolucion d)` | Línea de venta, cantidad, motivo, fecha/hora, empleado y si es apta para venta. | Bloquear la línea (`FOR UPDATE`); comprobar que la venta está `PAGADA`, que la modalidad admite la cantidad y que la suma de devoluciones no supera cantidad ni importe de la línea; calcular el reintegro con el precio cobrado; no modificar la línea; separar el reintegro de los cobros. |
