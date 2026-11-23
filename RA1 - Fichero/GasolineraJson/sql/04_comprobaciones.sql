-- ============================================================================
-- 04_comprobaciones.sql  --  Comprobaciones del diseño y del trigger
-- Ejecutar AL FINAL. Las consultas de las secciones A y B terminan con éxito.
-- La sección C contiene sentencias QUE DEBEN FALLAR: están comentadas y hay
-- que ejecutarlas de una en una (mysql --force o copiándolas al cliente SQL)
-- para que un error esperado no interrumpa la preparación de la base.
-- ============================================================================

USE gasolinera_ampliacion;

-- ----------------------------------------------------------------------------
-- A. Coherencia de los datos de ejemplo (todas deben devolver filas)
-- ----------------------------------------------------------------------------

-- A.1 Mínimo de 5 filas en las tablas principales y 10+ líneas de venta.
--     Esperado: 5 estaciones, 5 fichas, 8 surtidores, 6 empleados, 6 clientes,
--     5 tarjetas, 5 campañas, 8 productos, 5 proveedores, 6 ventas, 14 líneas,
--     9 pagos, 5 devoluciones, 5 incidencias, 14 asignaciones.
SELECT 'estacion' AS tabla, COUNT(*) AS filas FROM estacion
UNION ALL SELECT 'ficha_facturacion', COUNT(*) FROM ficha_facturacion
UNION ALL SELECT 'surtidor',          COUNT(*) FROM surtidor
UNION ALL SELECT 'empleado',          COUNT(*) FROM empleado
UNION ALL SELECT 'cliente',           COUNT(*) FROM cliente
UNION ALL SELECT 'tarjeta',           COUNT(*) FROM tarjeta_fidelizacion
UNION ALL SELECT 'campana',           COUNT(*) FROM campana
UNION ALL SELECT 'producto',          COUNT(*) FROM producto
UNION ALL SELECT 'proveedor',         COUNT(*) FROM proveedor
UNION ALL SELECT 'venta',             COUNT(*) FROM venta
UNION ALL SELECT 'linea_venta',       COUNT(*) FROM linea_venta
UNION ALL SELECT 'pago',              COUNT(*) FROM pago
UNION ALL SELECT 'devolucion',        COUNT(*) FROM devolucion
UNION ALL SELECT 'incidencia',        COUNT(*) FROM incidencia
UNION ALL SELECT 'asignacion_funcion',COUNT(*) FROM asignacion_funcion;

-- A.2 1:1 estación-ficha exacta: 5 estaciones, 5 fichas, ninguna sin pareja.
SELECT (SELECT COUNT(*) FROM estacion)                       AS estaciones,
       (SELECT COUNT(*) FROM ficha_facturacion)              AS fichas,
       (SELECT COUNT(*) FROM estacion e LEFT JOIN ficha_facturacion f
          ON f.id = e.ficha_id WHERE f.id IS NULL)           AS estaciones_sin_ficha,
       (SELECT COUNT(*) FROM ficha_facturacion f LEFT JOIN estacion e
          ON e.ficha_id = f.id WHERE e.id IS NULL)           AS fichas_huerfanas;
-- Esperado: 5, 5, 0, 0.

-- A.3 Surtidores: la estación 1 tiene 3; el número 1 se repite entre estaciones
--     pero nunca dentro de una misma estación (lo garantiza el UNIQUE compuesto).
SELECT e.codigo_interno, COUNT(s.id) AS surtidores,
       COUNT(DISTINCT s.numero_visible) AS numeros_distintos
FROM estacion e JOIN surtidor s ON s.estacion_id = e.id
GROUP BY e.id, e.codigo_interno ORDER BY e.id;

-- A.4 Personal: Lucía y Marcos supervisan a varios; Diego no tiene funciones
--     activas; Sara tiene varias a la vez y recuperó una función tras cerrarla.
SELECT s.nombre AS supervisor, COUNT(*) AS supervisados
FROM empleado s JOIN empleado e ON e.supervisor_id = s.id
GROUP BY s.id, s.nombre ORDER BY supervisados DESC;

SELECT e.nombre, COUNT(a.id) AS funciones_activas_2026_11_23
FROM empleado e LEFT JOIN asignacion_funcion a
  ON a.empleado_id = e.id
 AND a.fecha_inicio <= '2026-11-23'
 AND (a.fecha_fin IS NULL OR a.fecha_fin >= '2026-11-23')
GROUP BY e.id, e.nombre ORDER BY e.id;

SELECT e.nombre, f.descripcion, a.fecha_inicio, a.fecha_fin, a.condiciones
FROM asignacion_funcion a
JOIN empleado e ON e.id = a.empleado_id
JOIN funcion f ON f.id = a.funcion_id
WHERE e.numero_interno = 'EMP-003' ORDER BY a.fecha_inicio;

-- A.5 Clientes/tarjetas/campañas: 3 clientes con tarjeta, 3 sin tarjeta,
--     2 tarjetas en stock, campaña 2 sin inscritos, cliente 4 sin campañas.
SELECT SUM(cliente_id IS NOT NULL) AS tarjetas_asignadas,
       SUM(cliente_id IS NULL)     AS tarjetas_en_stock
FROM tarjeta_fidelizacion;

SELECT c.codigo, COUNT(i.id) AS inscripciones
FROM cliente c LEFT JOIN inscripcion i ON i.cliente_id = c.id
GROUP BY c.id, c.codigo ORDER BY c.id;

SELECT cam.codigo, COUNT(i.id) AS inscritos
FROM campana cam LEFT JOIN inscripcion i ON i.campana_id = cam.id
GROUP BY cam.id, cam.codigo ORDER BY cam.id;

-- A.6 Proveedores sin producto aislado (cada proveedor tiene ≥1 acuerdo vigente)
--     y productos sin proveedor (debe devolver 0 filas en ambas consultas).
SELECT p.codigo_interno FROM proveedor p
LEFT JOIN suministro_proveedor s
  ON s.proveedor_id = p.id AND s.vigente = TRUE
WHERE s.id IS NULL;

SELECT pr.referencia FROM producto pr
LEFT JOIN suministro_proveedor s
  ON s.producto_id = pr.id AND s.vigente = TRUE
WHERE s.id IS NULL;

-- A.7 Producto con varios proveedores (2 y 5) y proveedor con varios productos (1).
SELECT pr.referencia, COUNT(s.id) AS acuerdos
FROM producto pr JOIN suministro_proveedor s ON s.producto_id = pr.id
GROUP BY pr.id, pr.referencia HAVING COUNT(s.id) > 1;

SELECT pv.codigo_interno, COUNT(s.id) AS acuerdos
FROM proveedor pv JOIN suministro_proveedor s ON s.proveedor_id = pv.id
GROUP BY pv.id, pv.codigo_interno HAVING COUNT(s.id) > 1;

-- A.8 Importes de líneas, totales, cobros y estado. Ninguna venta PAGADA debe
--     tener desajuste entre suma de pagos e importe de sus líneas (0 filas).
SELECT v.referencia_ticket, v.estado,
       SUM(l.importe) AS total_lineas,
       (SELECT COALESCE(SUM(p.importe),0) FROM pago p WHERE p.venta_id = v.id) AS total_cobrado
FROM venta v JOIN linea_venta l ON l.venta_id = v.id
GROUP BY v.id, v.referencia_ticket, v.estado ORDER BY v.id;

SELECT v.referencia_ticket FROM venta v
WHERE v.estado = 'PAGADA'
  AND (SELECT COALESCE(SUM(p.importe),0) FROM pago p WHERE p.venta_id = v.id)
      <> (SELECT SUM(l.importe) FROM linea_venta l WHERE l.venta_id = v.id);

-- A.9 Devoluciones parciales sucesivas de la línea 2: 0,65 kg <= 1,50 kg y
--     8,13 € <= 18,75 €. Se conserva la línea original y su precio.
SELECT lv.id, lv.cantidad AS comprada, lv.precio_unitario AS precio_cobrado,
       SUM(d.cantidad) AS devuelta, SUM(d.importe_reintegrado) AS reintegrado
FROM linea_venta lv JOIN devolucion d ON d.linea_venta_id = lv.id
WHERE lv.id = 2 GROUP BY lv.id;

-- A.10 Redondeo común: 0,750 kg x 12,50 = 9,375 -> 9,38 (HALF_UP en DECIMAL).
SELECT id, cantidad, precio_unitario, importe FROM linea_venta WHERE id = 7;

-- A.11 Incidencias: una sin asignar, otra con responsable, Javier sin incidencias.
SELECT i.referencia, i.estado, e.nombre AS responsable
FROM incidencia i LEFT JOIN empleado e ON e.id = i.empleado_responsable_id
ORDER BY i.id;

-- ----------------------------------------------------------------------------
-- B. Trigger de historial de precios (secuencia con ROLLBACK final)
--    Se ejecuta entera sin errores. Las SELECT intermedias muestran el efecto.
--    Al terminar se deshace todo para dejar la base como quedó tras 03_datos.sql.
--    Para conservar el historial, cambia el ROLLBACK final por COMMIT.
-- ----------------------------------------------------------------------------

START TRANSACTION;

SELECT 'B.0 Historial al inicio ( esperado: 0 )' AS paso, COUNT(*) AS filas FROM historial_precio;

-- B.1 Cambio REAL de precio: debe crear exactamente una entrada.
UPDATE producto SET precio_venta = 0.32 WHERE id = 1;
SELECT 'B.1 tras cambiar 0.29 -> 0.32 (esperado: 1 fila, 0.29 y 0.32)' AS paso,
       producto_id, precio_anterior, precio_nuevo FROM historial_precio;

-- B.2 Guardar otra vez el MISMO precio: NO debe añadir entrada.
UPDATE producto SET precio_venta = 0.32 WHERE id = 1;
SELECT 'B.2 tras repetir 0.32 (esperado: sigue 1 fila)' AS paso,
       COUNT(*) AS filas FROM historial_precio;

-- B.3 Modificar solo la descripción: NO debe añadir entrada.
UPDATE producto SET descripcion = 'Café en sobre (nueva descripción)' WHERE id = 1;
SELECT 'B.3 tras cambiar descripción (esperado: sigue 1 fila)' AS paso,
       COUNT(*) AS filas FROM historial_precio;

-- B.4 El precio de una línea antigua NO se recalcula al cambiar el catálogo.
SELECT 'B.4 ticket antiguo (esperado: 0.29 en precio_unitario)' AS paso,
       id, precio_unitario, importe FROM linea_venta WHERE id = 1;

-- B.5 Cambio dentro de transacción que se deshace: desaparecen precio e historial.
SAVEPOINT antes_del_rollback;
UPDATE producto SET precio_venta = 0.45 WHERE id = 2;
SELECT 'B.5 dentro de la transacción (esperado: precio 0.45 y 2 filas)' AS paso,
       (SELECT precio_venta FROM producto WHERE id = 2) AS precio,
       (SELECT COUNT(*) FROM historial_precio) AS filas;
ROLLBACK TO SAVEPOINT antes_del_rollback;
SELECT 'B.5 tras deshacer (esperado: precio 12.50 y 1 fila)' AS paso,
       (SELECT precio_venta FROM producto WHERE id = 2) AS precio,
       (SELECT COUNT(*) FROM historial_precio) AS filas;

-- B.6 Otras comprobaciones del trigger: el alta (INSERT) no genera historial.
INSERT INTO producto (referencia, descripcion, precio_venta, estado, modalidad, formato_envase, precision_pesada)
VALUES ('PROD-009', 'Producto de prueba', 1.00, 'ACTIVO', 'ENAVASADO', 'unidad', NULL);
SELECT 'B.6 tras INSERT (esperado: sigue 1 fila en historial)' AS paso,
       COUNT(*) AS filas FROM historial_precio;

-- Dejar la base exactamente como estaba tras la carga de datos.
ROLLBACK;

SELECT 'B.7 tras ROLLBACK final (esperado: precio 0.29, descripción original, 0 filas)' AS paso,
       (SELECT precio_venta FROM producto WHERE id = 1) AS precio,
       (SELECT descripcion FROM producto WHERE id = 1) AS descripcion,
       (SELECT COUNT(*) FROM historial_precio) AS filas,
       (SELECT COUNT(*) FROM producto) AS productos;

-- ----------------------------------------------------------------------------
-- C. ERRORES ESPERADOS: ejecutar UNO A LA VEZ (están comentados a propósito)
-- ----------------------------------------------------------------------------

-- C.1 Referencia de negocio duplicada (UNIQUE):
-- INSERT INTO estacion (codigo_interno, nombre, fecha_apertura, ficha_id)
-- VALUES ('EST-01', 'Duplicada', '2026-01-01', 1);
-- Esperado: ERROR 1062 Duplicate entry 'EST-01' for key 'estacion.codigo_interno'.

-- INSERT INTO producto (referencia, descripcion, precio_venta, modalidad, formato_envase)
-- VALUES ('PROD-001', 'Duplicado', 1.00, 'ENAVASADO', 'unidad');
-- Esperado: ERROR 1062 sobre producto.referencia.

-- INSERT INTO tarjeta_fidelizacion (numero, fecha_emision) VALUES ('TF-001', '2026-01-01');
-- Esperado: ERROR 1062 sobre tarjeta_fidelizacion.numero.

-- C.2 Referencia a un registro inexistente (FK):
-- INSERT INTO surtidor (codigo_equipo, numero_visible, tipo_combustible, estado, estacion_id)
-- VALUES ('EQ-999', 9, 'GASOLINA', 'OPERATIVO', 999);
-- Esperado: ERROR 1452 Cannot add or update a child row (fk_surtidor_estacion).

-- C.3 Número visible repetido DENTRO de la misma estación (UNIQUE compuesto):
-- INSERT INTO surtidor (codigo_equipo, numero_visible, tipo_combustible, estado, estacion_id)
-- VALUES ('EQ-100', 1, 'DIESEL', 'OPERATIVO', 1);
-- Esperado: ERROR 1062 sobre uq_surtidor_numero_por_estacion.
-- (El mismo número 1 en la estación 2 sí se aceptó en 03_datos.sql.)

-- C.4 Asociación repetida (UNIQUE):
-- INSERT INTO inscripcion (campana_id, cliente_id, fecha) VALUES (1, 1, '2026-07-01');
-- Esperado: ERROR 1062 sobre uq_inscripcion.

-- INSERT INTO suministro_proveedor
--   (proveedor_id, producto_id, referencia_proveedor, precio_compra, plazo_entrega_dias)
-- VALUES (1, 1, 'REF-DUPLICADO', 0.20, 3);
-- Esperado: ERROR 1062 sobre uq_suministro_vigente (ya existe el acuerdo 1 vigente).
-- El acuerdo 9, de baja, demuestra que un acuerdo NO vigente sí puede coexistir.

-- C.5 Periodos ABIERTOS solapados de la misma función para la misma persona:
-- INSERT INTO asignacion_funcion (empleado_id, funcion_id, fecha_inicio, condiciones)
-- VALUES (3, 2, '2026-01-01', 'Segundo expendedor a la vez');
-- Esperado: ERROR 1062 sobre uq_asignacion_abierta.
-- Funciones DISTINTAS simultáneas sí están permitidas (Sara en los datos).

-- C.6 Restricciones CHECK:
-- AUTO-SUPERVISIÓN: ya NO existe el CHECK chk_empleado_no_se_supervisa.
-- La sentencia siguiente se ACEPTA en MySQL (no ejecutarla sobre datos reales):
-- UPDATE empleado SET supervisor_id = id WHERE id = 1;
-- Solo la rechaza Empleado.setSupervisor en Java; la fase JDBC deberá reforzarla
-- con un BEFORE INSERT/UPDATE o comprobación transaccional (ver C.8).

-- UPDATE campana SET fecha_fin = '2026-05-31' WHERE id = 1;
-- Esperado: ERROR 3819 sobre chk_campana_fechas.

-- UPDATE producto SET precio_venta = 0 WHERE id = 1;
-- Esperado: ERROR 3819 sobre chk_producto_precio.

-- UPDATE producto SET modalidad = 'GRANEL' WHERE id = 1;
-- Esperado: ERROR 3819 sobre chk_producto_modalidad (sigue teniendo formato_envase).

-- INSERT INTO tarjeta_fidelizacion (numero, fecha_emision, cliente_id)
-- VALUES ('TF-100', '2026-01-01', 1);
-- Esperado: ERROR 1062 porque el cliente 1 ya tiene la tarjeta TF-001 (UNIQUE cliente_id).

-- C.7 Baja protegida por FK (RESTRICT):
-- DELETE FROM estacion WHERE id = 1;
-- Esperado: ERROR 1451 Cannot delete or update a parent row (hay surtidores,
-- empleados, ventas e incidencias que la referencian).

-- DELETE FROM cliente WHERE id = 1;
-- Esperado: ERROR 1451. Entre otras referencias está fk_tarjeta_cliente: el
-- cliente 1 tiene la tarjeta TF-001 asignada (ON DELETE RESTRICT). Para borrarlo
-- habría que desasignar antes, en la misma transacción:
-- UPDATE tarjeta_fidelizacion
--    SET cliente_id = NULL, fecha_asignacion = NULL
--  WHERE cliente_id = 1;
-- (y después eliminar también sus inscripciones y ventas, que también son RESTRICT).

-- C.8 REGLAS PENDIENTES PARA LA SIGUIENTE FASE (no presentar como rechazo automático):
--   * Retirar el último surtidor de una estación: hoy solo se impide en Java
--     (Estacion.retirarSurtidor). En MySQL hará falta un trigger BEFORE DELETE
--     o control transaccional en la capa JDBC.
--   * Crear una ficha sin estación: como la FK está en estacion, MySQL no lo
--     impide; el alta conjunta se hará con una transacción en la fase JDBC.
--   * Eliminar el último acuerdo de un proveedor o de un producto: hoy no se
--     detecta automáticamente (se conservan acuerdos con vigente = FALSE).
--   * Auto-supervisión (A->A) y ciclos indirectos (A->B->A): no hay CHECK en BD;
--     Java los rechaza, pero una sentencia SQL directa podría introducirlos.
--   * Solapes entre periodos CERRADOS y uno nuevo de la misma función: el
--     UNIQUE parcial solo cubre el periodo abierto.
--   * Acumular devoluciones por encima de la cantidad o el importe de la línea:
--     Java lo valida con LineaVenta.cantidadDisponible();MySQL no lo comprueba
--     hoy. La fase JDBC añadirá un trigger con bloqueo (SELECT ... FOR UPDATE).
--   * Que el total de cobros cuadre al pasar a PAGADA: se controla en
--     Venta.confirmarPago() y se trasladará a la persistencia en la fase JDBC.
