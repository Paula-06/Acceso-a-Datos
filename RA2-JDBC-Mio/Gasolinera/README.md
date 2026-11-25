# README.md — GasolineraJson (Acceso a Datos, 2.º DAM)

## Qué es
Aplicación Java de gestión de gasolineras. La práctica anterior guarda
clientes y pagos de repostajes en CSV/JSON. La ampliación añade el diseño
MySQL y un modelo de dominio nuevo (estaciones, tienda, personal, clientes,
campañas, ventas, devoluciones e incidencias).

## Regla principal
**Todo el acceso a MySQL será solo con JDBC (`java.sql`).**
Nada de JPA, Hibernate, Spring ni otros ORM o frameworks. Si algo no se
puede hacer con JDBC, se pregunta; no se cambia de tecnología.

## Estado actual
- Fase 1 entregada: base de datos (23 tablas), scripts SQL, modelo de
  dominio y demo en memoria.
- Fase 2 (pendiente): métodos JDBC. Hoy no hay DAOs MySQL ni SQL en Java.
- CSV y JSON siguen funcionando y sus datos no se tocan.

## Mapa de carpetas
| Ruta | Qué contiene | ¿Java o MySQL? |
|---|---|---|
| `src/gasolinera/{modelo,persistencia,negocio,ui,app,migracion}` | Práctica anterior (consola, CSV/JSON) | Java |
| `src/ConexionBBDD/` | `PrimeraConexion.java`, conexión JDBC ya preparada. **Sin tocar** | Java |
| `src/dominio/comunes` | `Dinero` (redondeo) y `ModalidadProducto` (reglas de cantidad) | Java |
| `src/dominio/{estacion,personal,cliente,producto,venta,incidencia}` | Modelo de dominio nuevo | Java |
| `src/dominio/demo` | `DemoProductos` (demostración en memoria) | Java |
| `sql/` | 01 esquema, 02 triggers, 03 datos, 04 comprobaciones | MySQL |
| `docs/DISENO.md` | Modelo relacional, diccionario, reglas y JDBC futuro | Documentación |
| `README.md`, `AGENTS.md`, `style.md`, `Cambios.md`, `AUDITORIA_IA.odt` | Documentación y reglas | Documentación |
| `clientes.csv/.json`, `pagos.csv/.json`, `gasolinera.sqlite` | Datos de la práctica anterior. **No tocar** | Datos |

## Cómo saber si algo es Java o MySQL
- `.java` dentro de `src/` → **Java**.
- `.sql` dentro de `sql/` → **MySQL**.
- Historial de precios → **trigger de MySQL** (`sql/02_triggers.sql`), nunca Java.
- Claves foráneas, unicidad y `CHECK` → **MySQL**.
- Validar cantidades y calcular importes según la modalidad → **Java**
  (`dominio.comunes` y `dominio.producto`).
- Reglas entre varios registros (altas conjuntas, ciclos de supervisión,
  periodos solapados, devoluciones acumuladas) → **pendientes para JDBC**.

## Clases del dominio
Total: 34 clases (33 de modelo + `DemoProductos`), verificadas contra `src/dominio`.

| Clase | Paquete | Función | ¿Tabla MySQL? |
|---|---|---|---|
| `Dinero` | `dominio.comunes` | Centraliza el redondeo monetario (2 decimales, `HALF_UP`). | No (utilidad) |
| `ModalidadProducto` | `dominio.comunes` | Enum con comportamiento: reglas de cantidad de envasados y granel. | No (tipa `producto.modalidad`) |
| `MedioPago` | `dominio.comunes` | Enum de medios de cobro. | No (tipa `pago.medio_pago`) |
| `EstadoVenta` | `dominio.comunes` | Enum de estados del ticket. | No (tipa `venta.estado`) |
| `EstadoSurtidor` | `dominio.comunes` | Enum de estados del surtidor. | No (tipa `surtidor.estado`) |
| `EstadoIncidencia` | `dominio.comunes` | Enum de estados de la incidencia. | No (tipa `incidencia.estado`) |
| `EstadoProducto` | `dominio.comunes` | Enum de estados del catálogo. | No (tipa `producto.estado`) |
| `TipoCombustible` | `dominio.comunes` | Enum del combustible del surtidor. | No (tipa `surtidor.tipo_combustible`) |
| `Estacion` | `dominio.estacion` | Estación con ficha enlazada y surtidores; valida número visible y baja del último equipo. | `estacion` |
| `FichaFacturacion` | `dominio.estacion` | Serie y numeración de tickets de una estación. | `ficha_facturacion` |
| `Surtidor` | `dominio.estacion` | Equipo con número visible único dentro de su estación; referencia directa a `Estacion`. | `surtidor` |
| `Empleado` | `dominio.personal` | Identidad del personal; rechaza auto-supervisión y ciclos. | `empleado` |
| `Funcion` | `dominio.personal` | Clase abstracta base de las funciones. | `funcion` (catálogo) |
| `Cajero` | `dominio.personal` | Función con límite de devoluciones. | `funcion_cajero` |
| `Expendedor` | `dominio.personal` | Función con acreditación y caducidad. | `funcion_expendedor` |
| `ResponsableTienda` | `dominio.personal` | Función con presupuesto de compras. | `funcion_responsable_tienda` |
| `JefeEstacion` | `dominio.personal` | Función con nivel de autorización. | `funcion_jefe_estacion` |
| `AsignacionFuncion` | `dominio.personal` | Periodo con condiciones en que se ejerce una función. | `asignacion_funcion` |
| `Cliente` | `dominio.cliente` | Cliente registrado; exige al menos teléfono o email. | `cliente` |
| `TarjetaFidelizacion` | `dominio.cliente` | Tarjeta emitida, asignable a un cliente. | `tarjeta_fidelizacion` |
| `Campana` | `dominio.cliente` | Campaña promocional con fechas válidas. | `campana` |
| `Inscripcion` | `dominio.cliente` | Participación de un cliente en una campaña, con fecha propia. | `inscripcion` |
| `Producto` | `dominio.producto` | Clase abstracta del catálogo; valida cantidad y congela precio. | `producto` |
| `ProductoEnvasado` | `dominio.producto` | Producto en unidades completas. | `producto` (modalidad envasada) |
| `ProductoGranel` | `dominio.producto` | Producto por peso con precisión de pesada. | `producto` (modalidad granel) |
| `Proveedor` | `dominio.producto` | Proveedor con acuerdo registrado. | `proveedor` |
| `SuministroProveedor` | `dominio.producto` | Acuerdo vigente o histórico proveedor-producto. | `suministro_proveedor` |
| `HistorialPrecio` | `dominio.producto` | Solo lectura: MySQL lo genera mediante trigger. | `historial_precio` (solo lectura) |
| `Venta` | `dominio.venta` | Ticket; controla carrito y exige cobro exacto antes de `PAGADA`. | `venta` |
| `LineaVenta` | `dominio.venta` | Línea con precio cobrado e importe calculado. | `linea_venta` |
| `Pago` | `dominio.venta` | Cobro de tienda (distinto del pago de repostaje). | `pago` |
| `Devolucion` | `dominio.venta` | Reintegro parcial acumulado de una línea. | `devolucion` |
| `Incidencia` | `dominio.incidencia` | Incidencia de una estación con responsable actual. | `incidencia` |
| `DemoProductos` | `dominio.demo` | Demostración en memoria; no guarda ni lee MySQL. | No |

## Ejemplo de correspondencia Java ↔ MySQL: surtidores
- MySQL: tabla `surtidor` (`codigo_equipo` único en toda la empresa,
  `UNIQUE(estacion_id, numero_visible)`, `tipo_combustible`, `estado` y FK
  `estacion_id` a `estacion` con `RESTRICT`).
- Java: `dominio.estacion.Surtidor` guarda `codigoEquipo`, `numeroVisible`,
  `tipoCombustible` y `estado`, y mantiene una referencia directa a `Estacion`
  mediante el método `enlazar(Estacion)`, con visibilidad de paquete.
  `Estacion.anyadirSurtidor` rechaza repetir `numeroVisible` en esa estación
  y `retirarSurtidor` impide dejarla sin equipos. El `id` es `Integer` y vale
  `null` hasta que la capa JDBC asigna el autoincremental.

## Dos "Pago" distintos (no fusionar)
- Pagos de repostaje de la práctica anterior (`RegistroGasolinera`).
- Cobros de tienda: `dominio.venta.Pago`.

## Reglas que no se deben romper
Ver `AGENTS.md`. Resumen: solo lectura salvo autorización, nada fuera de
JDBC, no tocar CSV/JSON/SQLite ni `ConexionBBDD`, y no insertar nunca en
`historial_precio` desde Java.
