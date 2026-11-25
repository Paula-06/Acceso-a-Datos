

USE gasolinera_ampliacion;

DELIMITER //

CREATE TRIGGER trg_producto_precio_cambiado
AFTER UPDATE ON producto
FOR EACH ROW
BEGIN

    IF NEW.precio_venta <> OLD.precio_venta THEN
        INSERT INTO historial_precio
            (producto_id, precio_anterior, precio_nuevo, fecha_cambio)
        VALUES
            (OLD.id, OLD.precio_venta, NEW.precio_venta, NOW());
    END IF;
END//

DELIMITER ;

