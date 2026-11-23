
CREATE DATABASE IF NOT EXISTS gasolinera_ampliacion
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gasolinera_ampliacion;

DROP TABLE IF EXISTS historial_precio;
DROP TABLE IF EXISTS devolucion;
DROP TABLE IF EXISTS pago;
DROP TABLE IF EXISTS linea_venta;
DROP TABLE IF EXISTS venta;
DROP TABLE IF EXISTS incidencia;
DROP TABLE IF EXISTS inscripcion;
DROP TABLE IF EXISTS funcion_cajero;
DROP TABLE IF EXISTS funcion_expendedor;
DROP TABLE IF EXISTS funcion_responsable_tienda;
DROP TABLE IF EXISTS funcion_jefe_estacion;
DROP TABLE IF EXISTS asignacion_funcion;
DROP TABLE IF EXISTS tarjeta_fidelizacion;
DROP TABLE IF EXISTS empleado;
DROP TABLE IF EXISTS surtidor;
DROP TABLE IF EXISTS estacion;
DROP TABLE IF EXISTS campana;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS suministro_proveedor;
DROP TABLE IF EXISTS producto;
DROP TABLE IF EXISTS proveedor;
DROP TABLE IF EXISTS funcion;
DROP TABLE IF EXISTS ficha_facturacion;

CREATE TABLE ficha_facturacion (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    codigo_configuracion VARCHAR(20)  NOT NULL UNIQUE,
    serie_tickets        VARCHAR(10)  NOT NULL,
    siguiente_numero     INT          NOT NULL DEFAULT 1,
    texto_pie            VARCHAR(200) NULL,
    CONSTRAINT chk_ficha_siguiente CHECK (siguiente_numero > 0)
) ENGINE=InnoDB;

CREATE TABLE estacion (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    codigo_interno   VARCHAR(20)  NOT NULL UNIQUE,
    nombre           VARCHAR(100) NOT NULL,
    direccion        VARCHAR(200) NULL,
    telefono         VARCHAR(20)  NULL,
    fecha_apertura   DATE         NOT NULL,
    ficha_id         INT          NOT NULL UNIQUE,
    CONSTRAINT fk_estacion_ficha FOREIGN KEY (ficha_id)
        REFERENCES ficha_facturacion (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE surtidor (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    codigo_equipo    VARCHAR(20) NOT NULL UNIQUE,
    numero_visible   INT         NOT NULL,
    tipo_combustible ENUM('GASOLINA','DIESEL') NOT NULL,
    estado           ENUM('OPERATIVO','AVERIADO','MANTENIMIENTO') NOT NULL DEFAULT 'OPERATIVO',
    estacion_id      INT         NOT NULL,
    CONSTRAINT uq_surtidor_numero_por_estacion UNIQUE (estacion_id, numero_visible),
    CONSTRAINT chk_surtidor_numero CHECK (numero_visible > 0),
    CONSTRAINT fk_surtidor_estacion FOREIGN KEY (estacion_id)
        REFERENCES estacion (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE empleado (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    numero_interno      VARCHAR(20)  NOT NULL UNIQUE,
    documento_identidad VARCHAR(30)  NOT NULL UNIQUE,
    nombre              VARCHAR(100) NOT NULL,
    telefono            VARCHAR(20)  NULL,
    email               VARCHAR(150) NULL,
    fecha_incorporacion DATE         NOT NULL,
    estacion_id         INT          NOT NULL,
    supervisor_id       INT          NULL,
    CONSTRAINT chk_empleado_contacto CHECK (telefono IS NOT NULL OR email IS NOT NULL),
    CONSTRAINT fk_empleado_estacion FOREIGN KEY (estacion_id)
        REFERENCES estacion (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_empleado_supervisor FOREIGN KEY (supervisor_id)
        REFERENCES empleado (id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE funcion (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    codigo      VARCHAR(30)  NOT NULL UNIQUE,
    descripcion VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE asignacion_funcion (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    empleado_id   INT          NOT NULL,
    funcion_id    INT          NOT NULL,
    fecha_inicio  DATE         NOT NULL,
    fecha_fin     DATE         NULL,
    condiciones   VARCHAR(200) NULL,
    abierta       INT GENERATED ALWAYS AS (CASE WHEN fecha_fin IS NULL THEN 1 ELSE NULL END) STORED,
    CONSTRAINT chk_asignacion_fechas CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio),
    CONSTRAINT uq_asignacion_abierta UNIQUE (empleado_id, funcion_id, abierta),
    CONSTRAINT fk_asignacion_empleado FOREIGN KEY (empleado_id)
        REFERENCES empleado (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_asignacion_funcion FOREIGN KEY (funcion_id)
        REFERENCES funcion (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE funcion_cajero (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    asignacion_id       INT            NOT NULL UNIQUE,
    limite_devoluciones DECIMAL(10,2)  NOT NULL,
    CONSTRAINT chk_cajero_limite CHECK (limite_devoluciones > 0),
    CONSTRAINT fk_cajero_asignacion FOREIGN KEY (asignacion_id)
        REFERENCES asignacion_funcion (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE funcion_expendedor (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    asignacion_id       INT          NOT NULL UNIQUE,
    numero_acreditacion VARCHAR(30)  NOT NULL UNIQUE,
    caducidad           DATE         NOT NULL,
    CONSTRAINT fk_expendedor_asignacion FOREIGN KEY (asignacion_id)
        REFERENCES asignacion_funcion (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE funcion_responsable_tienda (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    asignacion_id       INT            NOT NULL UNIQUE,
    presupuesto_compras DECIMAL(10,2)  NOT NULL,
    CONSTRAINT chk_responsable_presupuesto CHECK (presupuesto_compras > 0),
    CONSTRAINT fk_responsable_asignacion FOREIGN KEY (asignacion_id)
        REFERENCES asignacion_funcion (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE funcion_jefe_estacion (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    asignacion_id      INT NOT NULL UNIQUE,
    nivel_autorizacion TINYINT NOT NULL,
    CONSTRAINT chk_jefe_nivel CHECK (nivel_autorizacion BETWEEN 1 AND 3),
    CONSTRAINT fk_jefe_asignacion FOREIGN KEY (asignacion_id)
        REFERENCES asignacion_funcion (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE cliente (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    codigo     VARCHAR(20)  NOT NULL UNIQUE,
    nombre     VARCHAR(100) NOT NULL,
    telefono   VARCHAR(20)  NULL,
    email      VARCHAR(150) NULL,
    direccion  VARCHAR(200) NULL,
    CONSTRAINT chk_cliente_contacto CHECK (telefono IS NOT NULL OR email IS NOT NULL)
) ENGINE=InnoDB;

CREATE TABLE tarjeta_fidelizacion (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    numero           VARCHAR(20) NOT NULL UNIQUE,
    fecha_emision    DATE        NOT NULL,
    cliente_id       INT         NULL UNIQUE,
    fecha_asignacion DATE        NULL,
    CONSTRAINT chk_tarjeta_asignacion CHECK (
        (cliente_id IS NULL AND fecha_asignacion IS NULL)
        OR (cliente_id IS NOT NULL AND fecha_asignacion IS NOT NULL)
    ),
    CONSTRAINT fk_tarjeta_cliente FOREIGN KEY (cliente_id)
        REFERENCES cliente (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE campana (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    codigo       VARCHAR(20)  NOT NULL UNIQUE,
    nombre       VARCHAR(100) NOT NULL,
    descripcion  VARCHAR(250) NULL,
    fecha_inicio DATE         NOT NULL,
    fecha_fin    DATE         NOT NULL,
    CONSTRAINT chk_campana_fechas CHECK (fecha_fin >= fecha_inicio)
) ENGINE=InnoDB;

CREATE TABLE inscripcion (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    campana_id INT  NOT NULL,
    cliente_id INT  NOT NULL,
    fecha      DATE NOT NULL,
    CONSTRAINT uq_inscripcion UNIQUE (campana_id, cliente_id),
    CONSTRAINT fk_inscripcion_campana FOREIGN KEY (campana_id)
        REFERENCES campana (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_inscripcion_cliente FOREIGN KEY (cliente_id)
        REFERENCES cliente (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- Catálogo y proveedores
-- modalidad es estable; las columnas específicas se validan con CHECK.
-- precio_venta siempre informado y > 0. El histórico lo escribe el trigger.
-- ----------------------------------------------------------------------------
CREATE TABLE producto (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    referencia       VARCHAR(30)  NOT NULL UNIQUE,
    descripcion      VARCHAR(150) NOT NULL,
    precio_venta     DECIMAL(10,2) NOT NULL,
    estado           ENUM('ACTIVO','RETIRADO') NOT NULL DEFAULT 'ACTIVO',
    modalidad        ENUM('ENVASADO','GRANEL') NOT NULL,
    formato_envase   VARCHAR(50) NULL,
    precision_pesada TINYINT     NULL,
    CONSTRAINT chk_producto_precio CHECK (precio_venta > 0),
    CONSTRAINT chk_producto_modalidad CHECK (
        (modalidad = 'ENAVASADO' AND formato_envase IS NOT NULL AND precision_pesada IS NULL)
        OR (modalidad = 'GRANEL' AND precision_pesada BETWEEN 0 AND 3 AND formato_envase IS NULL)
    )
) ENGINE=InnoDB;

CREATE TABLE historial_precio (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    producto_id     INT           NOT NULL,
    precio_anterior DECIMAL(10,2) NOT NULL,
    precio_nuevo    DECIMAL(10,2) NOT NULL,
    fecha_cambio    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_historial_precios CHECK (precio_anterior <> precio_nuevo),
    CONSTRAINT fk_historial_producto FOREIGN KEY (producto_id)
        REFERENCES producto (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE proveedor (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    codigo_interno       VARCHAR(20)  NOT NULL UNIQUE,
    identificacion_fiscal VARCHAR(30) NOT NULL UNIQUE,
    razon_social         VARCHAR(150) NOT NULL,
    telefono             VARCHAR(20)  NULL,
    email                VARCHAR(150) NULL,
    direccion            VARCHAR(200) NULL,
    CONSTRAINT chk_proveedor_contacto CHECK (telefono IS NOT NULL OR email IS NOT NULL)
) ENGINE=InnoDB;

CREATE TABLE suministro_proveedor (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    proveedor_id         INT           NOT NULL,
    producto_id          INT           NOT NULL,
    referencia_proveedor VARCHAR(40)   NOT NULL,
    precio_compra        DECIMAL(10,2) NOT NULL,
    plazo_entrega_dias   INT           NOT NULL DEFAULT 0,
    vigente              BOOLEAN       NOT NULL DEFAULT TRUE,
    -- Solo se prohíbe duplicar el acuerdo VIGENTE; los antiguos se conservan.
    vigente_flag         INT GENERATED ALWAYS AS (CASE WHEN vigente THEN 1 ELSE NULL END) STORED,
    CONSTRAINT chk_suministro_precio CHECK (precio_compra > 0),
    CONSTRAINT chk_suministro_plazo CHECK (plazo_entrega_dias >= 0),
    CONSTRAINT uq_suministro_vigente UNIQUE (proveedor_id, producto_id, vigente_flag),
    CONSTRAINT fk_suministro_proveedor FOREIGN KEY (proveedor_id)
        REFERENCES proveedor (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_suministro_producto FOREIGN KEY (producto_id)
        REFERENCES producto (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- Ventas, cobros y devoluciones
-- La línea guarda el precio realmente cobrado y el importe calculado. El total
-- de pagos se comprobará al confirmar en la fase JDBC (estado PENDIENTE/PAGADA).
-- Los cobros de tienda NO sustituyen a los pagos de repostaje de CSV/JSON.
-- ----------------------------------------------------------------------------
CREATE TABLE venta (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    referencia_ticket VARCHAR(30) NOT NULL UNIQUE,
    estacion_id      INT          NOT NULL,
    empleado_id      INT          NOT NULL,
    cliente_id       INT          NULL,
    fecha_hora       DATETIME     NOT NULL,
    estado           ENUM('PENDIENTE','PAGADA') NOT NULL DEFAULT 'PENDIENTE',
    CONSTRAINT fk_venta_estacion FOREIGN KEY (estacion_id)
        REFERENCES estacion (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_venta_empleado FOREIGN KEY (empleado_id)
        REFERENCES empleado (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_venta_cliente FOREIGN KEY (cliente_id)
        REFERENCES cliente (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE linea_venta (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    venta_id        INT           NOT NULL,
    producto_id     INT           NOT NULL,
    numero_orden    INT           NOT NULL,
    cantidad        DECIMAL(12,3) NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    importe         DECIMAL(12,2) GENERATED ALWAYS AS (ROUND(cantidad * precio_unitario, 2)) STORED,
    CONSTRAINT chk_linea_orden CHECK (numero_orden > 0),
    CONSTRAINT chk_linea_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_linea_precio CHECK (precio_unitario > 0),
    CONSTRAINT uq_linea_orden UNIQUE (venta_id, numero_orden),
    CONSTRAINT fk_linea_venta FOREIGN KEY (venta_id)
        REFERENCES venta (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_linea_producto FOREIGN KEY (producto_id)
        REFERENCES producto (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE pago (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    venta_id    INT           NOT NULL,
    importe     DECIMAL(10,2) NOT NULL,
    fecha_hora  DATETIME      NOT NULL,
    medio_pago  ENUM('EFECTIVO','TARJETA','TRANSFERENCIA') NOT NULL,
    referencia  VARCHAR(40)   NULL,
    CONSTRAINT chk_pago_importe CHECK (importe > 0),
    CONSTRAINT fk_pago_venta FOREIGN KEY (venta_id)
        REFERENCES venta (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE devolucion (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    linea_venta_id      INT           NOT NULL,
    cantidad            DECIMAL(12,3) NOT NULL,
    motivo              VARCHAR(200)  NOT NULL,
    fecha_hora          DATETIME      NOT NULL,
    empleado_id         INT           NOT NULL,
    importe_reintegrado DECIMAL(12,2) NOT NULL,
    apta_para_venta     BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT chk_devolucion_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_devolucion_importe CHECK (importe_reintegrado > 0),
    CONSTRAINT fk_devolucion_linea FOREIGN KEY (linea_venta_id)
        REFERENCES linea_venta (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_devolucion_empleado FOREIGN KEY (empleado_id)
        REFERENCES empleado (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- Incidencias: solo se conserva el responsable actual.
-- ----------------------------------------------------------------------------
CREATE TABLE incidencia (
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    referencia              VARCHAR(20)  NOT NULL UNIQUE,
    estacion_id             INT          NOT NULL,
    descripcion             VARCHAR(250) NOT NULL,
    fecha_apertura          DATETIME     NOT NULL,
    estado                  ENUM('ABIERTA','EN_CURSO','RESUELTA','CERRADA') NOT NULL DEFAULT 'ABIERTA',
    empleado_responsable_id INT          NULL,
    CONSTRAINT fk_incidencia_estacion FOREIGN KEY (estacion_id)
        REFERENCES estacion (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_incidencia_responsable FOREIGN KEY (empleado_responsable_id)
        REFERENCES empleado (id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;
