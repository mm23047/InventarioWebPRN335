-- Eliminar vistas existentes
DROP VIEW IF EXISTS kardex_implementado CASCADE;
DROP VIEW IF EXISTS "Kardex_Implementado" CASCADE;

-- Crear vista con nombre exacto (case-sensitive)
CREATE VIEW "Kardex_Implementado" AS
SELECT 
    k.fecha AS fecha_movimiento,
    k.tipo_movimiento,
    k.referencia_externa AS referencia,
    CAST(k.id_almacen AS varchar) AS almacen_nombre,
    CASE 
        WHEN k.tipo_movimiento = 'ENTRADA' THEN k.cantidad
        ELSE 0
    END AS cantidad_entrada,
    CASE 
        WHEN k.tipo_movimiento = 'SALIDA' THEN k.cantidad
        ELSE 0
    END AS cantidad_salida,
    k.precio AS precio_unitario,
    k.cantidad_actual AS saldo,
    k.precio_actual AS precio_promedio,
    (k.cantidad_actual * k.precio_actual) AS valor_total,
    k.observaciones AS producto_observaciones,
    k.id_producto,
    k.id_almacen
FROM kardex k
INNER JOIN producto p ON k.id_producto = p.id_producto;
