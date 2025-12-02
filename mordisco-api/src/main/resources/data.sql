
-- data.sql (ordered for FK integrity) --

-- 1) roles
INSERT INTO `roles` VALUES (1,'ROLE_ADMIN'),(2,'ROLE_CLIENTE'),(3,'ROLE_RESTAURANTE'),(4,'ROLE_REPARTIDOR');

-- 2) configuracion_sistema
INSERT INTO configuracion_sistema (id, comision_plataforma, costo_base_delivery, costo_por_kilometro, modo_mantenimiento, monto_minimo_pedido, porcentaje_ganancias_repartidor, radio_maximo_entrega, tiempo_maximo_entrega,fecha_actualizacion)
VALUES (1, 15.0, 2000.0, 500.0, false, 5000.0, 80.0, 10.0, 45,'2025-11-28 08:28:49.000000');