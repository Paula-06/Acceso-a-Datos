
USE gasolinera_ampliacion;

INSERT INTO ficha_facturacion (id, codigo_configuracion, serie_tickets, siguiente_numero, texto_pie) VALUES
 (1, 'CFG-01', 'A', 100, 'Gracias por su visita'),
 (2, 'CFG-02', 'B', 200, 'Conserve su ticket'),
 (3, 'CFG-03', 'C', 300, 'Atención al cliente 900000001'),
 (4, 'CFG-04', 'D', 400, 'Forma de pago en caja'),
 (5, 'CFG-05', 'E', 500, 'Facturación electrónica disponible');

INSERT INTO estacion (id, codigo_interno, nombre, direccion, telefono, fecha_apertura, ficha_id) VALUES
 (1, 'EST-01', 'Gasolinera Norte', 'Calle Mayor 1',   '910000001', '2015-03-01', 1),
 (2, 'EST-02', 'Gasolinera Sur',   'Avenida Sur 20',  '910000002', '2018-06-15', 2),
 (3, 'EST-03', 'Gasolinera Este',  'Calle Este 7',    '910000003', '2019-01-10', 3),
 (4, 'EST-04', 'Gasolinera Oeste', 'Plaza Oeste 3',   '910000004', '2020-09-20', 4),
 (5, 'EST-05', 'Gasolinera Centro','Gran Vía 50',     '910000005', '2021-11-05', 5);

INSERT INTO surtidor (id, codigo_equipo, numero_visible, tipo_combustible, estado, estacion_id) VALUES
 (1, 'EQ-001', 1, 'GASOLINA', 'OPERATIVO', 1),
 (2, 'EQ-002', 2, 'DIESEL',   'AVERIADO',  1),
 (3, 'EQ-003', 3, 'GASOLINA', 'OPERATIVO', 1),
 (4, 'EQ-004', 1, 'DIESEL',   'OPERATIVO', 2),
 (5, 'EQ-005', 1, 'GASOLINA', 'OPERATIVO', 3),
 (6, 'EQ-006', 2, 'DIESEL',   'MANTENIMIENTO', 3),
 (7, 'EQ-007', 1, 'GASOLINA', 'OPERATIVO', 4),
 (8, 'EQ-008', 1, 'DIESEL',   'OPERATIVO', 5);

INSERT INTO empleado
 (id, numero_interno, documento_identidad, nombre, telefono, email, fecha_incorporacion, estacion_id, supervisor_id) VALUES
 (1, 'EMP-001', '11111111A', 'Lucía',  '600111111', NULL,             '2016-01-10', 1, NULL),
 (2, 'EMP-002', '22222222B', 'Diego',  '600222222', NULL,             '2026-01-05', 1, 1),
 (3, 'EMP-003', '33333333C', 'Sara',   NULL,        'sara@correo.es',  '2019-04-20', 1, 1),
 (4, 'EMP-004', '44444444D', 'Marcos', NULL,        'marcos@correo.es','2020-03-01', 2, NULL),
 (5, 'EMP-005', '55555555E', 'Elena',  '600555555', NULL,             '2018-06-01', 3, 4),
 (6, 'EMP-006', '66666666F', 'Javier', '600666666', NULL,             '2022-07-01', 4, 4);

INSERT INTO funcion (id, codigo, descripcion) VALUES
 (1, 'CAJERO', 'Cajero'),
 (2, 'EXPENDEDOR', 'Expendedor'),
 (3, 'RESPONSABLE_TIENDA', 'Responsable de tienda'),
 (4, 'JEFE_ESTACION', 'Jefe de estación');

INSERT INTO asignacion_funcion (id, empleado_id, funcion_id, fecha_inicio, fecha_fin, condiciones) VALUES
 (1,  1, 4, '2016-01-10', NULL,         'Nivel máximo'),
 (2,  1, 1, '2020-05-01', NULL,         'Caja de refuerzo'),
 (3,  3, 2, '2019-04-20', NULL,         'Acreditación vigente'),
 (4,  3, 3, '2021-01-01', '2022-01-01', 'Periodo anterior'),
 (5,  3, 3, '2025-02-01', NULL,         'Recuperada con nuevas condiciones'),
 (6,  4, 1, '2020-03-01', NULL,         'Caja habitual'),
 (7,  5, 2, '2019-06-01', NULL,         'Acreditación vigente'),
 (8,  6, 1, '2022-07-01', NULL,         'Caja habitual'),
 (9,  2, 1, '2026-01-05', '2026-02-01', 'Solo enero'),
 (10, 1, 3, '2023-01-01', '2023-12-31', 'Responsable temporal'),
 (11, 3, 1, '2024-01-01', NULL,         'Caja compartida'),
 (12, 5, 2, '2018-01-01', '2019-05-31', 'Primer periodo'),
 (13, 6, 2, '2023-01-01', NULL,         'Acreditación vigente'),
 (14, 4, 4, '2021-01-01', NULL,         'Nivel 2');

INSERT INTO funcion_cajero (id, asignacion_id, limite_devoluciones) VALUES
 (1, 2, 500.00), (2, 6, 300.00), (3, 8, 250.00), (4, 9, 200.00), (5, 11, 150.00);

INSERT INTO funcion_expendedor (id, asignacion_id, numero_acreditacion, caducidad) VALUES
 (1, 3, 'ACR-77', '2027-12-31'),
 (2, 7, 'ACR-88', '2028-05-31'),
 (3, 12, 'ACR-55', '2019-05-31'),
 (4, 13, 'ACR-66', '2029-03-31');

INSERT INTO funcion_responsable_tienda (id, asignacion_id, presupuesto_compras) VALUES
 (1, 4, 2000.00), (2, 5, 3000.00), (3, 10, 2500.00);

INSERT INTO funcion_jefe_estacion (id, asignacion_id, nivel_autorizacion) VALUES
 (1, 1, 3), (2, 14, 2);

INSERT INTO cliente (id, codigo, nombre, telefono, email, direccion) VALUES
 (1, 'CLI-001', 'Ana',    '600444444', NULL,             NULL),
 (2, 'CLI-002', 'Marcos', NULL,        'marcos@correo.es', NULL),
 (3, 'CLI-003', 'Paula',  '600777777', 'paula@correo.es', 'Calle Sol 4'),
 (4, 'CLI-004', 'Carlos', '600888888', NULL,             NULL),
 (5, 'CLI-005', 'Lucía',  NULL,        'lucia@correo.es', NULL),
 (6, 'CLI-006', 'Nuria',  '600999999', NULL,             NULL);

INSERT INTO tarjeta_fidelizacion (id, numero, fecha_emision, cliente_id, fecha_asignacion) VALUES
 (1, 'TF-001', '2026-01-05', 1,    '2026-01-20'),
 (2, 'TF-002', '2026-02-01', NULL, NULL),  -- recibida en lote, sin entregar
 (3, 'TF-003', '2026-03-01', 3,    '2026-03-05'),
 (4, 'TF-004', '2026-04-01', NULL, NULL),
 (5, 'TF-005', '2026-05-01', 5,    '2026-05-10');

INSERT INTO campana (id, codigo, nombre, descripcion, fecha_inicio, fecha_fin) VALUES
 (1, 'CAMP-01', 'Verano 2026',      'Descuentos de temporada', '2026-06-01', '2026-09-30'),
 (2, 'CAMP-02', 'Navidad 2026',     'Campaña aún sin inscritos','2026-12-01', '2026-12-31'),
 (3, 'CAMP-03', 'Primavera 2026',   'Participación múltiple',  '2026-03-01', '2026-05-31'),
 (4, 'CAMP-04', 'Otoño 2026',       'Campaña de otoño',        '2026-10-01', '2026-11-30'),
 (5, 'CAMP-05', 'Clientes nuevos',  'Bienvenida',              '2026-01-01', '2026-12-31');

INSERT INTO inscripcion (id, campana_id, cliente_id, fecha) VALUES
 (1, 1, 1, '2026-06-10'),
 (2, 1, 3, '2026-06-12'),
 (3, 3, 1, '2026-03-15'),
 (4, 3, 5, '2026-03-20'),
 (5, 4, 3, '2026-10-02'),
 (6, 5, 2, '2026-02-01'),
 (7, 5, 6, '2026-02-05');

INSERT INTO producto (id, referencia, descripcion, precio_venta, estado, modalidad, formato_envase, precision_pesada) VALUES
 (1, 'PROD-001', 'Café en sobre',  0.29, 'ACTIVO',   'ENAVASADO', 'paquete 10 sobres', NULL),
 (2, 'PROD-002', 'Almendras',     12.50, 'ACTIVO',   'GRANEL',    NULL, 2),
 (3, 'PROD-003', 'Agua 1,5 L',     0.75, 'ACTIVO',   'ENAVASADO', 'botella', NULL),
 (4, 'PROD-004', 'Refresco lata',  0.90, 'ACTIVO',   'ENAVASADO', 'lata 33 cl', NULL),
 (5, 'PROD-005', 'Arroz',          2.40, 'ACTIVO',   'GRANEL',    NULL, 2),
 (6, 'PROD-006', 'Pan de molde',   1.15, 'ACTIVO',   'ENAVASADO', 'bolsa 500 g', NULL),
 (7, 'PROD-007', 'Aceite 1 L',     4.25, 'ACTIVO',   'ENAVASADO', 'botella 1 L', NULL),
 (8, 'PROD-008', 'Nueces',        15.00, 'RETIRADO', 'GRANEL',    NULL, 2);

INSERT INTO proveedor (id, codigo_interno, identificacion_fiscal, razon_social, telefono, email, direccion) VALUES
 (1, 'PROV-001', 'B12345678', 'Suministros del Norte', '910111111', NULL,                 'Polígono 1'),
 (2, 'PROV-002', 'B87654321', 'Alimentos Sur',         NULL,        'pedidos@alsur.es',    'Polígono 2'),
 (3, 'PROV-003', 'B11223344', 'Bebidas Centro',        '910333333', NULL,                 'Calle Centro 8'),
 (4, 'PROV-004', 'B55667788', 'Granel Express',        NULL,        'info@granelexpress.es','Avenida 9'),
 (5, 'PROV-005', 'B99887766', 'Casa Matriz',           '910555555', NULL,                 'Sede central');

INSERT INTO suministro_proveedor
 (id, proveedor_id, producto_id, referencia_proveedor, precio_compra, plazo_entrega_dias, vigente) VALUES
 (1,  1, 1, 'REF-P1-CAFE',      0.18, 3, TRUE),
 (2,  1, 2, 'REF-P1-ALM',       9.00, 5, TRUE),
 (3,  2, 2, 'REF-P2-ALM',       8.80, 4, TRUE),
 (4,  2, 5, 'REF-P2-ARROZ',     2.00, 4, TRUE),
 (5,  3, 3, 'REF-P3-AGUA',      0.45, 2, TRUE),
 (6,  3, 4, 'REF-P3-LATA',      0.60, 2, TRUE),
 (7,  4, 5, 'REF-P4-ARROZ',     1.95, 3, TRUE),
 (8,  4, 8, 'REF-P4-NUECES',   12.00, 6, TRUE),
 (9,  5, 1, 'REF-P5-CAFE-ANT',  0.20, 5, FALSE),
 (10, 5, 6, 'REF-P5-PAN',       0.80, 1, TRUE),
 (11, 5, 7, 'REF-P5-ACEITE',    3.50, 3, TRUE);

INSERT INTO venta (id, referencia_ticket, estacion_id, empleado_id, cliente_id, fecha_hora, estado) VALUES
 (1, 'A-100', 1, 1, 1,    '2026-11-20 09:10:00', 'PAGADA'),
 (2, 'A-101', 1, 2, NULL, '2026-11-20 10:25:00', 'PAGADA'),
 (3, 'A-102', 2, 3, 2,    '2026-11-21 11:40:00', 'PAGADA'),
 (4, 'B-1',   2, 4, NULL, '2026-11-21 12:05:00', 'PENDIENTE'),
 (5, 'B-2',   3, 5, 3,    '2026-11-22 13:15:00', 'PAGADA'),
 (6, 'B-3',   4, 6, 4,    '2026-11-22 17:45:00', 'PAGADA');

INSERT INTO linea_venta (id, venta_id, producto_id, numero_orden, cantidad, precio_unitario) VALUES
 (1,  1, 1, 1, 3.000, 0.29),
 (2,  1, 2, 2, 1.500, 12.50),
 (3,  2, 3, 1, 2.000, 0.75),
 (4,  2, 4, 2, 4.000, 0.90),
 (5,  3, 5, 1, 2.000, 2.40),
 (6,  3, 6, 2, 1.000, 1.15),
 (7,  4, 2, 1, 0.750, 12.50),
 (8,  4, 7, 2, 1.000, 4.25),
 (9,  5, 8, 1, 0.500, 15.00),
 (10, 5, 1, 2, 10.000, 0.29),
 (11, 5, 3, 3, 6.000, 0.75),
 (12, 6, 5, 1, 3.250, 2.40),
 (13, 6, 6, 2, 2.000, 1.15),
 (14, 6, 4, 3, 5.000, 0.90);

INSERT INTO pago (id, venta_id, importe, fecha_hora, medio_pago, referencia) VALUES
 (1, 1, 10.00, '2026-11-20 09:11:00', 'EFECTIVO',      'EF-1'),
 (2, 1,  9.62, '2026-11-20 09:11:30', 'TARJETA',       'TJ-1'),
 (3, 2,  5.10, '2026-11-20 10:26:00', 'EFECTIVO',      'EF-2'),
 (4, 3,  2.00, '2026-11-21 11:41:00', 'EFECTIVO',      'EF-3'),
 (5, 3,  3.95, '2026-11-21 11:41:20', 'TARJETA',       'TJ-2'),
 (6, 4,  5.00, '2026-11-21 12:06:00', 'EFECTIVO',      'EF-4'),  -- cobro parcial
 (7, 5, 14.90, '2026-11-22 13:16:00', 'TARJETA',       'TJ-3'),
 (8, 6, 10.00, '2026-11-22 17:46:00', 'EFECTIVO',      'EF-5'),
 (9, 6,  4.60, '2026-11-22 17:46:30', 'TRANSFERENCIA', 'TR-1');

INSERT INTO devolucion
 (id, linea_venta_id, cantidad, motivo, fecha_hora, empleado_id, importe_reintegrado, apta_para_venta) VALUES
 (1, 2,  0.400, 'Peso incorrecto',  '2026-11-20 09:30:00', 1, 5.00, FALSE),
 (2, 2,  0.250, 'Producto dañado',  '2026-11-20 09:45:00', 1, 3.13, FALSE),
 (3, 9,  0.200, 'Envase abierto',   '2026-11-22 13:40:00', 4, 3.00, FALSE),
 (4, 1,  1.000, 'Error de producto','2026-11-22 14:00:00', 2, 0.29, TRUE),
 (5, 12, 1.000, 'Arroz húmedo',     '2026-11-22 18:10:00', 6, 2.40, FALSE);

INSERT INTO incidencia
 (id, referencia, estacion_id, descripcion, fecha_apertura, estado, empleado_responsable_id) VALUES
 (1, 'INC-001', 1, 'Surtidor 2 averiado',  '2026-11-23 08:00:00', 'ABIERTA',   NULL),
 (2, 'INC-002', 1, 'Caja sin papel',       '2026-11-23 08:15:00', 'EN_CURSO',  3),
 (3, 'INC-003', 2, 'Fallo de iluminación', '2026-11-23 09:00:00', 'EN_CURSO',  4),
 (4, 'INC-004', 1, 'GPS caído',            '2026-11-23 09:10:00', 'ABIERTA',   NULL),
 (5, 'INC-005', 3, 'Dispensador atascado', '2026-11-22 10:30:00', 'CERRADA',   5);
