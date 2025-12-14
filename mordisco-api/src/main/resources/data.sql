
-- data.sql (ordered for FK integrity) --

-- 1) roles
INSERT INTO `roles` VALUES (1,'ROLE_ADMIN'),(2,'ROLE_CLIENTE'),(3,'ROLE_RESTAURANTE'),(4,'ROLE_REPARTIDOR');

-- 2) configuracion_sistema
INSERT INTO configuracion_sistema (id, porcentaje_ganancias_restaurante, costo_base_delivery, costo_por_kilometro, modo_mantenimiento, monto_minimo_pedido, porcentaje_ganancias_repartidor, radio_maximo_entrega, tiempo_maximo_entrega,fecha_actualizacion)
VALUES (1, 80.0, 2000.0, 500.0, false, 5000.0, 80.0, 10.0, 45,'2025-11-28 08:28:49.000000');

-- 3) usuarios
INSERT INTO usuarios (id, nombre, apellido, telefono, email, password, baja_logica,rol_id)
VALUES
 -- Admin user --
 -- Contraseña: Admin123! --
 (1,'Admin','Sistema','+54 223 6543897','mordiscoapp@gmail.com','$2a$10$HPIs5sZdqMjFtU/9wQOy/eSACUFYgTdmVoVMv9kq794q9Uyvsqv2S',false,1),

 -- Cliente user --
  -- Contraseña: Mordisco123! --
 (2,'Juan','Pérez','+54 911 12345678','usuario1@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,2),
 (3,'María','Gómez','+54 911 87654321','usuario2@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,2),
 (4,'Carlos','López','+54 911 11223344','usuario3@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,2),
 (5,'Ana','Martínez','+54 911 44332211','usuario4@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,2),
 (6,'Luis','Rodríguez','+54 911 55667788','usuario5@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,2),
 (7,'Sofía','Fernández','+54 911 88776655','usuario6@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,2),
 (8,'Diego','García','+54 911 99887766','usuario7@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,2),
 (9,'Valentina','Sánchez','+54 911 66554433','usuario8@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,2),
 (10,'Javier','Ramírez','+54 911 33445566','usuario9@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,2),
 (11,'Camila','Torres','+54 911 77665544','usuario10@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,2),
 (12,'Matías','Flores','+54) 911 22334455','usuario11@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,2),
 (13,'Lucía','Rivera','+54 911 55443322','usuario12@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,2),
 (14,'Andrés','Vega','+54 911 66778899','usuario13@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,2),
 (15,'Natalia','Cruz','+54 911 88990011','usuario14@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,2),
 (16,'Fernando','Morales','+54 911 44556677','usuario15@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,2),

  -- Restaurante users --
  -- Contraseña: Mordisco123! --
 (17,'Lionel','Juarez','+54 11 23456789','restaurante1@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,3),
 (18,'Martina','Silva','+54 11 98765432','restaurante2@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,3),
 (19,'Gonzalo','Rojas','+54 11 11223344','restaurante3@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,3),
 (20,'Florencia','Molina','+54 11 44332211','restaurante4@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,3),
 (21,'Agustín','Herrera','+54 11 55667788','restaurante5@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,3),
 (22,'Luciana','Castro','+54 11 88776655','restaurante6@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,3),
 (23,'Diego','Ortiz','+54 11 99887766','restaurante7@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,3),
 (24,'Jimena','Suárez','+54 11 66554433','restaurante8@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,3),
 (25,'Santiago','Gutiérrez','+54 11 33445566','restaurante9@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,3),
 (26,'Victoria','Ramos','+54 11 77665544','restaurante10@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,3),
 (27,'Federico','Alvarez','+54 11 22334455','restaurante11@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,3),
 (28,'Marina','Romero','+54 11 55443322','restaurante12@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,3),
 (29,'Alejandro','Navarro','+54 11 66778899','restaurante13@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,3),
 (30,'Carolina','Torres','+54 11 88990011','restaurante14@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,3),
 (31,'Joaquín','Domínguez','+54 11 44556677','restaurante15@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,3),

  -- Repartidor users --
  -- Contraseña: Mordisco123! --
 (32,'Marcos','Silva','+54 9 11 23456789','repartidor1@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,4),
 (33,'Elena','Vargas','+54 9 11 98765432','repartidor2@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,4),
 (34,'Pablo','Cabrera','+54 9 11 11223344','repartidor3@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,4),
 (35,'Elisa','Méndez','+54 9 11 44332211','repartidor4@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,4),
 (36,'Raúl','Silva','+54 9 11 55667788','repartidor5@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,4),
 (37,'Marta','Ponce','+54 9 11 88776655','repartidor6@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,4),
 (38,'Javier','Campos','+54 9 11 99887766','repartidor7@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,4),
 (39,'Silvia','Leiva','+54 9 11 66554433','repartidor8@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,4),
 (40,'Fernando','Vega','+54 9 11 33445566','repartidor9@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,4),
 (41,'Lorena','Silva','+54 9 11 77665544','repartidor10@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,4),
 (42,'Ricardo','Mora','+54 9 11 22334455','repartidor11@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,4),
 (43,'Gabriela','Silva','+54 9 11 55443322','repartidor12@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,4),
 (44,'Oscar','Ríos','+54 9 11 66778899','repartidor13@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,4),
 (45,'Patricia','Silva','+54 9 11 88990011','repartidor14@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,4),
 (46,'Jorge','Benítez','+54 9 11 44556677','repartidor15@gmail.com','$2a$10$XTQa.9OhSsAIARXUdmqm2ew7jdSqyWYI5T7aegCX808X.KCJOVkfS',false,4);

INSERT INTO `imagenes` VALUES (1,'McDonald\'s Logo','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTTpdnCaNKupk2YSbUXA2tNGPGcNA3jTj2QyA&s'),(2,'Doble Cuarto de Libra','https://www.mcdonalds.com.py/rails/active_storage/blobs/redirect/eyJfcmFpbHMiOnsibWVzc2FnZSI6IkJBaHBBcnc0IiwiZXhwIjpudWxsLCJwdXIiOiJibG9iX2lkIn19--554055bbfd01767f6ef5c8262e2c1e41d26aa621/Banner%201000x1000%20-%20McCombo%20Doble%20Cuarto%20de%20Libra%20-%20Imagen%20de%20producto.png'),(3,'Grand Doble Tasty','https://www.mcdonalds.com.py/rails/active_storage/blobs/redirect/eyJfcmFpbHMiOnsibWVzc2FnZSI6IkJBaHBBcjA0IiwiZXhwIjpudWxsLCJwdXIiOiJibG9iX2lkIn19--bdbfbedaa1c8f818a6ff658255d54511f668bac5/Banner%201000x1000%20-%20McCombo%20Doble%20Premium%20Tasty%20-%20Imagen%20de%20producto.png'),(4,'Big Mac','https://www.mcdonalds.com.py/rails/active_storage/blobs/redirect/eyJfcmFpbHMiOnsibWVzc2FnZSI6IkJBaHBBcm80IiwiZXhwIjpudWxsLCJwdXIiOiJibG9iX2lkIn19--807707b94ce5ea235a775342af84a95985353fd6/Banner%201000x1000%20-%20McCombo%20Big%20Mac%20-%20Imagen%20de%20producto.png'),(5,'Doble Bacon Cheedar McMelt','https://www.mcdonalds.com.py/rails/active_storage/blobs/redirect/eyJfcmFpbHMiOnsibWVzc2FnZSI6IkJBaHBBc2s0IiwiZXhwIjpudWxsLCJwdXIiOiJibG9iX2lkIn19--5fdc6c542eefe55fcff6ab8083586ee581a9963c/Banner%201000x1000%20-%20McCombo%20McMelt%20-%20Imagen%20de%20producto.png'),(6,'McNuggets X10','https://www.mcdonalds.com.py/rails/active_storage/blobs/redirect/eyJfcmFpbHMiOnsibWVzc2FnZSI6IkJBaHBBcUlCIiwiZXhwIjpudWxsLCJwdXIiOiJibG9iX2lkIn19--628560b4e2b8d44afc35fa882c97f24cb641965d/Mcnuggets-Imagen%20Productos%20varios%201000x10000.png'),(7,'McNífica','https://www.mcdonalds.com.py/rails/active_storage/blobs/redirect/eyJfcmFpbHMiOnsibWVzc2FnZSI6IkJBaHBBbmMrIiwiZXhwIjpudWxsLCJwdXIiOiJibG9iX2lkIn19--026ec31438f6ac51bc96138d0aad94baff698686/Banner%201000x1000%20-%20McCombo%20McN%C3%ADfica%20-%20Imagen%20de%20producto.png'),(8,'Mostaza Logo','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRPUPuEjfpPoD_l48bGboU0qbV4Nomd2ucEnA&s'),(9,'Homo Argentum','https://www.mostazaweb.com.ar/wp-content/uploads/2025/08/20-7-copia.png'),(10,'TR1 X Trueno','https://www.mostazaweb.com.ar/wp-content/uploads/2025/05/20-7-copia-2.png'),(11,'Doble Cuarto','https://www.mostazaweb.com.ar/wp-content/uploads/2025/05/00-4.png'),(12,'Ensalada Royal Crispy','https://www.mostazasocial.com.uy/wp-content/uploads/sites/2/2025/06/ENSALADAS_0-5.png'),(13,'Cero Carne Veggie','https://www.mostazaweb.com.ar/wp-content/uploads/2025/05/0-1-1.png'),(14,'Ensalada Palta y Huevo Grill','https://www.mostazaweb.com.ar/wp-content/uploads/2025/05/20-6-1.png'),(15,'El Club de la Milanesa Logo','https://dondecomequilmes.com/wp-content/uploads/2019/03/logo.jpg'),(16,'Suprema del Club','https://scontent.fmdq7-1.fna.fbcdn.net/v/t51.82787-15/583040046_18563363824032649_472755857585726408_n.jpg?_nc_cat=111&ccb=1-7&_nc_sid=127cfc&_nc_ohc=9_rAI6YL7mcQ7kNvwGfcmcA&_nc_oc=AdlQJK5kkNXNs3vduOAc9bD7Akc4QREGAVqNg5vnIA4s63UpIqzRuxClreunotVhMsg&_nc_zt=23&_nc_ht=scontent.fmdq7-1.fna&_nc_gid=NND3PbjwjXnz1MucWrsiWg&oh=00_AfmaTVamzeJ-144Y49Z3cQK3-qa_c1sJjvR9x6yfHuXi3w&oe=69412355'),(17,'Milanesa a caballo','https://scontent.fmdq7-1.fna.fbcdn.net/v/t51.75761-15/511431949_18531779632032649_7417832614516008872_n.jpg?_nc_cat=100&ccb=1-7&_nc_sid=127cfc&_nc_ohc=8yDbEVI7WvMQ7kNvwHoaD_f&_nc_oc=Admt3xjBfLkO1pS-h3QKzV0MCKbe1oV1WLlJWEjGI1kOiHodtjomdnO9Jn8ROZey2l8&_nc_zt=23&_nc_ht=scontent.fmdq7-1.fna&_nc_gid=cRx9p9ssWPQjaAvzMV-77A&oh=00_Afmr7irriRz3_C3VCgXVPQQJMeVFIyZ-Qi-8prr32F9PcA&oe=694129AC'),(18,'Milanesa Sweet Spicy','https://scontent.fmdq6-1.fna.fbcdn.net/v/t51.82787-15/514831863_18533438923032649_8687170621484706710_n.jpg?_nc_cat=102&ccb=1-7&_nc_sid=127cfc&_nc_ohc=UbPBttE4UCwQ7kNvwGI4CC6&_nc_oc=AdnH0QfmKCmYuDsyYqOuuGP0yQSulCGN6BozaS4GqRFQ6dO9-7VU6o_lRUyxcdjZie4&_nc_zt=23&_nc_ht=scontent.fmdq6-1.fna&_nc_gid=A2QYy7rFKbogLEl5MGtKIQ&oh=00_Afm7HqNI8WMv_Y1ji7pCTHUc-4UpPcdmDy3WI_Gk8wcKnQ&oe=69410A5A'),(19,'Milanesa Gringa','https://scontent.fmdq6-1.fna.fbcdn.net/v/t51.75761-15/491440793_18517281253032649_5281516675184626462_n.jpg?_nc_cat=108&ccb=1-7&_nc_sid=127cfc&_nc_ohc=87mNkWxmYzcQ7kNvwGZc_Sf&_nc_oc=Adl7eGDnz8HPfBwnbU1mEofV6u_O9-X0fAFdP5-YKJKPRy-Xh243EDc7ukkc5icVCPU&_nc_zt=23&_nc_ht=scontent.fmdq6-1.fna&_nc_gid=-uh9z-an9t1WM7dEsFhiRg&oh=00_AfkCwat82j-NDqWkjtuLilK-uslQXERv7fOT3fasIFKq2w&oe=694118F9'),(20,'Estilo Sushi Logo','https://scontent.fmdq7-1.fna.fbcdn.net/v/t39.30808-6/313284929_492519206254853_8209950250594367177_n.jpg?_nc_cat=103&ccb=1-7&_nc_sid=6ee11a&_nc_ohc=GCHI6xq5gRMQ7kNvwHcNyw3&_nc_oc=AdmziEUCulI7elRcO5WV_3Nl3RwAV6uTjxY94Xez6sEKsMyWtyiz9ieYhbG_srMTp2I&_nc_zt=23&_nc_ht=scontent.fmdq7-1.fna&_nc_gid=sMIvFHBx3zvYmWW7huNA-A&oh=00_AfmMKHhpPai4ZI4LF2THcpvD09Bnm3PVGf8F660BkL14cQ&oe=69410D19'),(21,'Combinado N1 - 12 Piezas Mixto','https://images.rappi.com.ar/products/tmp911394311232801324695338325.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/tmp911394311232801324695338325.png?e=webp&q=50&d=400x400'),(22,'Combinado N1 - 15 Piezas Mixto','https://images.rappi.com.ar/products/tmp91139398298270544455798564.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/tmp91139398298270544455798564.png?e=webp&q=50&d=400x400'),(23,'Combinado N2 - 12 Piezas Salmon','https://images.rappi.com.ar/products/tmp911394914825836158351684845.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/tmp911394914825836158351684845.png?e=webp&q=50&d=400x400'),(24,'Combinado 2/3 - 12p Salmon y Langostino','https://images.rappi.com.ar/products/tmp911394411082821145458312041.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/tmp911394411082821145458312041.png?e=webp&q=50&d=400x400'),(25,'Ahumada Burger','https://images.rappi.com.ar/products/tmp92122869025539825163825221.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/tmp92122869025539825163825221.png?e=webp&q=50&d=400x400'),(26,'Del Trono Burger','https://images.rappi.com.ar/products/tmp921228513027226887303055928.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/tmp921228513027226887303055928.png?e=webp&q=50&d=400x400'),(27,'Poke Londres','https://images.rappi.com.ar/products/tmp966382412877056433733981163.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/tmp966382412877056433733981163.png?e=webp&q=50&d=400x400'),(28,'Lebron Cervecería Logo','https://scontent.fmdq6-1.fna.fbcdn.net/v/t1.6435-9/48380921_2559666054058369_57297586737905664_n.jpg?_nc_cat=107&ccb=1-7&_nc_sid=6ee11a&_nc_ohc=9Qmxc801jzkQ7kNvwGwLQVP&_nc_oc=AdlEac2_gMrnPyfSpQelfLj44lvHxlHXhGHNMyj-jbiMnZD-gq3mfmPDR4mfXSDuPqA&_nc_zt=23&_nc_ht=scontent.fmdq6-1.fna&_nc_gid=7F6bUZM8VPU-Gxq1vFE5eA&oh=00_AfmFZ8SEze7sclZ96VFoCsPdVwja2Dnlh550SFOmhmbD0g&oe=6962B1C3'),(29,'2 Cheese burger + papas','https://images.rappi.com.ar/products/960e6e22-34b6-4250-9fa2-7c057240d604.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/960e6e22-34b6-4250-9fa2-7c057240d604.png?e=webp&q=50&d=400x400'),(30,'Combo 2 classic bacon 1 lt cerveza','https://images.rappi.com.ar/products/1adcf3a6-a365-46af-bd6e-a98ca8d89aed.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/1adcf3a6-a365-46af-bd6e-a98ca8d89aed.png?e=webp&q=50&d=400x400'),(31,'Papas con cheddar','https://images.rappi.com.ar/products/f7b5181a-d06f-413f-86d2-b5b7561ea627.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/f7b5181a-d06f-413f-86d2-b5b7561ea627.png?e=webp&q=50&d=400x400'),(32,'Papas pork','https://images.rappi.com.ar/products/046ede90-6b1d-4b45-8bd4-542b50fa7981.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/046ede90-6b1d-4b45-8bd4-542b50fa7981.png?e=webp&q=50&d=400x400'),(33,'Chicken fingers','https://images.rappi.com.ar/products/675f7efe-f46a-4ba7-a981-828237e33129.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/675f7efe-f46a-4ba7-a981-828237e33129.png?e=webp&q=50&d=400x400'),(34,'Bacon patty melt burger','https://images.rappi.com.ar/products/43ab9a68-b925-47c2-8b4e-2caac4a19af6.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/43ab9a68-b925-47c2-8b4e-2caac4a19af6.png?e=webp&q=50&d=400x400'),(35,'Pepsi regular 354 ml','https://images.rappi.com.ar/products/56ff4207-9cfc-4604-87e1-e76b6013b45c.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/56ff4207-9cfc-4604-87e1-e76b6013b45c.png?e=webp&q=50&d=400x400'),(36,'Lebron honey 1 litro','https://images.rappi.com.ar/products/22b608bd-9755-4ed1-a29f-21b27adf382d.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/22b608bd-9755-4ed1-a29f-21b27adf382d.png?e=webp&q=50&d=400x400'),(37,'Hell\'s Pizza Logo','https://eventopremium.com.ar/uploads/3/5/3/4/35343135/logo-franquicia-hells-pizza_orig.png'),(38,'Lincoln grande','https://images.rappi.com.ar/products/421a8818-423c-4e53-94a0-baf633f465e3-1603204158245_hq.jpeg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/421a8818-423c-4e53-94a0-baf633f465e3-1603204158245_hq.jpeg?e=webp&q=50&d=400x400'),(39,'Obama grande','https://images.rappi.com.ar/products/aefcb159-6a69-4f0d-9f1b-3529ec2c2bac-1603204256098_hq.jpeg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/aefcb159-6a69-4f0d-9f1b-3529ec2c2bac-1603204256098_hq.jpeg?e=webp&q=50&d=400x400'),(40,'Chick norris xxl','https://images.rappi.com.ar/products/570004-1567613744.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/570004-1567613744.png?e=webp&q=50&d=400x400'),(41,'Hawaiana grande','https://images.rappi.com.ar/products/6e29cf6f-a3b2-4680-b8c3-75815a27d7bb.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/6e29cf6f-a3b2-4680-b8c3-75815a27d7bb.png?e=webp&q=50&d=400x400'),(42,'Napoletta xxl','https://images.rappi.com.ar/products/c35aadf7-4f3c-4ba5-8bdc-9d890efa5864-1675202970211.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/c35aadf7-4f3c-4ba5-8bdc-9d890efa5864-1675202970211.png?e=webp&q=50&d=400x400'),(43,'Spaguetti meatballs','https://images.rappi.com.ar/products/fc72e532-9ada-4639-ba6e-97bc9e05dc9d-1750174627004.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/fc72e532-9ada-4639-ba6e-97bc9e05dc9d-1750174627004.png?e=webp&q=50&d=400x400'),(44,'Burger King Logo','https://www.pixartprinting.it/blog/wp-content/uploads/2021/03/logo-1994-min.jpg'),(45,'Doble Carne Doble Queso + Papas Regular','https://images.rappi.com.ar/products/6679435e-3ea0-4a5f-8098-0cbdfaa89a53.jpeg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/6679435e-3ea0-4a5f-8098-0cbdfaa89a53.jpeg?e=webp&q=50&d=400x400'),(46,'Combo Stacker Doble XL Muzarella','https://images.rappi.com.ar/products/30b8a2c2-f4db-4baf-953a-bd72ec7ba424.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/30b8a2c2-f4db-4baf-953a-bd72ec7ba424.png?e=webp&q=50&d=400x400'),(47,'Combo BBQ Bacon','https://images.rappi.com.ar/products/cc474b39-e4a8-45f8-8bc0-197669dd98a4.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/cc474b39-e4a8-45f8-8bc0-197669dd98a4.png?e=webp&q=50&d=400x400'),(48,'2x1 Combo Cheese Onion XL','https://images.rappi.com.ar/products/3e5f7583-c497-4750-a4c5-bdb772bdae49.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/3e5f7583-c497-4750-a4c5-bdb772bdae49.png?e=webp&q=50&d=400x400'),(49,'Combo Guacamole King Simple','https://images.rappi.com.ar/products/cace6f76-fb27-4b41-8d92-499513cd5d96.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/cace6f76-fb27-4b41-8d92-499513cd5d96.png?e=webp&q=50&d=400x400'),(50,'Wrap Atun + bebida','https://images.rappi.com.ar/products/563dbb6a-3e4a-46a1-a63c-da0287101aa8.jpeg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/563dbb6a-3e4a-46a1-a63c-da0287101aa8.jpeg?e=webp&q=50&d=400x400'),(51,'Combo Ensalada Caesar con pollo','https://images.rappi.com.ar/products/59d0eedc-ec4f-4f3b-b4ad-8341b8f627c2.jpg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/59d0eedc-ec4f-4f3b-b4ad-8341b8f627c2.jpg?e=webp&q=50&d=400x400'),(52,'Combo Triple Onion Cheddar','https://images.rappi.com.ar/products/87624fce-5aa0-421e-add2-5b54e62d44ee.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/87624fce-5aa0-421e-add2-5b54e62d44ee.png?e=webp&q=50&d=400x400'),(53,'Combo Whopper + Franui','https://images.rappi.com.ar/products/29d9ae28-9be2-4a50-a6aa-0c5c983f0902.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/29d9ae28-9be2-4a50-a6aa-0c5c983f0902.png?e=webp&q=50&d=400x400'),(54,'Combo King de Pollo','https://images.rappi.com.ar/products/c378328b-2a0a-42c2-9abb-0e4fe83d0108.jpg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/c378328b-2a0a-42c2-9abb-0e4fe83d0108.jpg?e=webp&q=50&d=400x400'),(55,'La Reina del Parque Logo','https://images.rappi.com.ar/restaurants_logo/lareinadelparque122658-1575647268468.png'),(56,'Budín de limón','https://images.rappi.com.ar/products/38e6d898-39e2-4f54-aeb9-e7d0e12044be.jpeg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/38e6d898-39e2-4f54-aeb9-e7d0e12044be.jpeg?e=webp&q=50&d=400x400'),(57,'1Kg de pan surtido','https://images.rappi.com.ar/products/df5b8913-f3a6-48ad-91c1-9299da8237c3.jpeg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/df5b8913-f3a6-48ad-91c1-9299da8237c3.jpeg?e=webp&q=50&d=400x400'),(58,'Tarteleta picos blancos','https://images.rappi.com.ar/products/1dadc8a5-8524-407d-9bb2-50511b4f7cd4.jpeg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/1dadc8a5-8524-407d-9bb2-50511b4f7cd4.jpeg?e=webp&q=50&d=400x400'),(59,'Tarteleta de cerezas','https://images.rappi.com.ar/products/bac390bc-9d1e-40b4-aacf-84509692a974.jpeg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/bac390bc-9d1e-40b4-aacf-84509692a974.jpeg?e=webp&q=50&d=400x400'),(60,'1/4 Kilo de bizcochitos de grasa','https://images.rappi.com.ar/products/9f7ed4dc-e98a-404c-92a6-027d4f88547e.jpeg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/9f7ed4dc-e98a-404c-92a6-027d4f88547e.jpeg?e=webp&q=50&d=400x400'),(61,'Tarteleta picos negros','https://images.rappi.com.ar/products/5342c43b-2dc2-4ff4-a421-9d7cb92fa1b3.jpeg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/5342c43b-2dc2-4ff4-a421-9d7cb92fa1b3.jpeg?e=webp&q=50&d=400x400'),(62,'Pasta frola de membrillo','https://images.rappi.com.ar/products/6dc330a7-7be0-4228-b334-667d89cb08e3.jpeg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/6dc330a7-7be0-4228-b334-667d89cb08e3.jpeg?e=webp&q=50&d=400x400'),(63,'Coco Café Logo','https://scontent.fmdq6-1.fna.fbcdn.net/v/t1.6435-9/69634816_103498181032925_2621810520386174976_n.jpg?_nc_cat=109&ccb=1-7&_nc_sid=6ee11a&_nc_ohc=a00vA_H1z8UQ7kNvwHhfRan&_nc_oc=AdmZYJFUbO1nx7mDMwHdWf-YZmtAOOUDbPMwu07LsKB8SVzW8TtClie7K0MM_zrVNIY&_nc_zt=23&_nc_ht=scontent.fmdq6-1.fna&_nc_gid=1sTNWmKSYeBEdx9XWTrmoA&oh=00_AfkM2N0hGCsQUTAyQN7Zxp_gUIVQhV9yovBdLlrHEtwWaw&oe=696391D4'),(64,'Desayuno Tradicional','https://images.rappi.com.ar/products/1145340-1756592038482.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/1145340-1756592038482.png?e=webp&q=50&d=400x400'),(65,'Tostado Gratten','https://images.rappi.com.ar/products/d1c50111-71f2-4d57-bf18-4f2bdc9c06a7-1749210117112.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/d1c50111-71f2-4d57-bf18-4f2bdc9c06a7-1749210117112.png?e=webp&q=50&d=400x400'),(66,'Desayuno Continental','https://images.rappi.com.ar/products/884e5fb0-d952-4e9d-926b-a1095656352c-1749990369373.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/884e5fb0-d952-4e9d-926b-a1095656352c-1749990369373.png?e=webp&q=50&d=400x400'),(67,'Cool Box','https://images.rappi.com.ar/products/9090799a-77b3-4dd6-95c4-0ab554d0dbd3-1749987802457.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/9090799a-77b3-4dd6-95c4-0ab554d0dbd3-1749987802457.png?e=webp&q=50&d=400x400'),(68,'Docena de Medialunas','https://images.rappi.com.ar/products/57e1ca66-88c2-4974-9841-bf3bf03cb612.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/57e1ca66-88c2-4974-9841-bf3bf03cb612.png?e=webp&q=50&d=400x400'),(69,'Granola Bowl con Cafe con Leche','https://images.rappi.com.ar/products/ee7bb25e-e82e-4d2e-bf37-8711009175fa-1749990226376.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/ee7bb25e-e82e-4d2e-bf37-8711009175fa-1749990226376.png?e=webp&q=50&d=400x400'),(70,'Che Sushi Logo','https://pbs.twimg.com/profile_images/1455231623/logo_400x400.jpg'),(71,'Poke Langostinos','https://images.rappi.com.ar/products/535ac6fd-3434-403d-b5b6-b6a75eee5356.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/535ac6fd-3434-403d-b5b6-b6a75eee5356.png?e=webp&q=50&d=400x400'),(72,'Poke Salad Mex','https://images.rappi.com.ar/products/e5710c92-0580-4edf-a691-5ae8c4ef978b.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/e5710c92-0580-4edf-a691-5ae8c4ef978b.png?e=webp&q=50&d=400x400'),(73,'Spring Roll Verdura x 2','https://images.rappi.com.ar/products/97b8b604-d5a3-469b-b998-c04dcb2f95a1.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/97b8b604-d5a3-469b-b998-c04dcb2f95a1.png?e=webp&q=50&d=400x400'),(74,'Roll Sublime','https://images.rappi.com.ar/products/446424b5-1f85-440a-b48d-693a7e3d9bf1.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/446424b5-1f85-440a-b48d-693a7e3d9bf1.png?e=webp&q=50&d=400x400'),(75,'Sashimi Salmon','https://images.rappi.com.ar/products/9b269416-cdf1-4f8b-845f-813661496adb.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/9b269416-cdf1-4f8b-845f-813661496adb.png?e=webp&q=50&d=400x400'),(76,'Dean & Dennys Logo','https://deananddennys.com/stage/contenidos/1611864738.png'),(77,'Super Bacon Burger Doble & Papas Grandes','https://images.rappi.com.ar/products/d3f4e2bb-c1b8-42f5-b032-24dcd0fa35e4-1728060959977.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/d3f4e2bb-c1b8-42f5-b032-24dcd0fa35e4-1728060959977.png?e=webp&q=50&d=400x400'),(78,'Combo Cheeseburger Simple (120g) & Papas','https://images.rappi.com.ar/products/c80d6c9e-3de3-4e0c-9749-88f6680a0cd5-1728060868655.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/c80d6c9e-3de3-4e0c-9749-88f6680a0cd5-1728060868655.png?e=webp&q=50&d=400x400'),(79,'Combo Burger Crispy Chicken & Papas','https://images.rappi.com.ar/products/771c6843-8cb3-4006-b103-1f5b8ee2138b-1728059990078.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/771c6843-8cb3-4006-b103-1f5b8ee2138b-1728059990078.png?e=webp&q=50&d=400x400'),(80,'2 Cheeseburger Clásicas Dobles & 1 Papas','https://images.rappi.com.ar/products/f94c0973-d67f-4d5b-9e58-b1ee991f1325-1728060630821.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/f94c0973-d67f-4d5b-9e58-b1ee991f1325-1728060630821.png?e=webp&q=50&d=400x400'),(81,'Chiqui Empanadas Logo','https://scontent.fmdq6-1.fna.fbcdn.net/v/t39.30808-6/294976626_730837431603792_8674573165695544802_n.jpg?_nc_cat=109&ccb=1-7&_nc_sid=6ee11a&_nc_ohc=-oE362R4ASQQ7kNvwH5S6M_&_nc_oc=Adl2hUcTRrdVyRWSz5z8eZ-5FuVT2qtp7tUjaDVf-0ovQzn3cwAptolB1YUEVT4kBW8&_nc_zt=23&_nc_ht=scontent.fmdq6-1.fna&_nc_gid=fWcdi7VqU8VlRbpYf4uOWw&oh=00_AfncZ6bvd0eYbj3mMIruJUWGZ7SXyQu6OuUU3qFJHV387w&oe=6941F783'),(82,'Carne Cortada a Cuchillo','https://images.rappi.com.ar/products/1521524-1657656344111.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/1521524-1657656344111.png?e=webp&q=50&d=400x400'),(83,'Jamón y Mozarella','https://images.rappi.com.ar/products/1521526-1630509868622.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/1521526-1630509868622.png?e=webp&q=50&d=400x400'),(84,'Empanada de Humita','https://images.rappi.com.ar/products/1521530-1630509652795.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/1521530-1630509652795.png?e=webp&q=50&d=400x400'),(85,'Empanada Capresse','https://images.rappi.com.ar/products/05cc936d-b836-4b6b-996e-df7f5bef3177-1637161902289.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/05cc936d-b836-4b6b-996e-df7f5bef3177-1637161902289.png?e=webp&q=50&d=400x400'),(86,'Empanada de Roquefort','https://images.rappi.com.ar/products/1552109-1622570942196.jpg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/1552109-1622570942196.jpg?e=webp&q=50&d=400x400'),(87,'Sao Medialunas Logo','https://sao.com.ar/wp-content/uploads/2022/05/logo-800x800-1-e1653060285579.png'),(88,'Docena De Medialunas Mixtas','https://images.rappi.com.ar/products/tmpImg4b0c4363-9685-40c2-9612-cd41af5bc8bd.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/tmpImg4b0c4363-9685-40c2-9612-cd41af5bc8bd.png?e=webp&q=50&d=400x400'),(89,'Cookie Rellena De Chocolate','https://images.rappi.com.ar/products/tmpImg1927a932-22dc-4d71-a1f4-371f2db28d66.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/tmpImg1927a932-22dc-4d71-a1f4-371f2db28d66.png?e=webp&q=50&d=400x400'),(90,'1/2 Doc. Med. Mix Pastelera','https://images.rappi.com.ar/products/tmpImge536961a-4e1d-447a-bbb6-1994b80c7ac0.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/tmpImge536961a-4e1d-447a-bbb6-1994b80c7ac0.png?e=webp&q=50&d=400x400'),(91,'São 1','https://images.rappi.com.ar/products/tmpImg2c02c7c2-648d-44d8-870d-60977d6b9531.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/tmpImg2c02c7c2-648d-44d8-870d-60977d6b9531.png?e=webp&q=50&d=400x400'),(97,'Docena De Medialunas','https://images.rappi.com.ar/products/tmpImgf8d3919c-4121-46b1-8533-2aa93b509ffd.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/tmpImgf8d3919c-4121-46b1-8533-2aa93b509ffd.png?e=webp&q=50&d=400x400'),(98,'Poke Pop Logo','https://scontent.fmdq7-1.fna.fbcdn.net/v/t39.30808-6/294662981_379477297623535_9190722977736384522_n.jpg?_nc_cat=104&ccb=1-7&_nc_sid=6ee11a&_nc_ohc=87tKfohxqtkQ7kNvwFOM_nm&_nc_oc=Adn5WM-Qnuepg7ipy6JqHq5jZQJROP3Fmk7-irNJiKZCpPCQ3fP8nGYopb5lG87q50w&_nc_zt=23&_nc_ht=scontent.fmdq7-1.fna&_nc_gid=Lunz27pazmFouleidw68nA&oh=00_AfkTIkBCiye2duVbBMIAAfzfm6kWRFXkw3XEuYhd19H5uw&oe=69420B8C'),(99,'Langos rebozados','https://images.rappi.com.ar/products/3592226e-7758-4e99-85dc-8007a51273d5.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/3592226e-7758-4e99-85dc-8007a51273d5.png?e=webp&q=50&d=400x400'),(100,'Sushi Salad Salmango','https://images.rappi.com.ar/products/3958d57b-8538-48e4-8607-9c43c79d77eb.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/3958d57b-8538-48e4-8607-9c43c79d77eb.png?e=webp&q=50&d=400x400'),(101,'Poke Salmón','https://images.rappi.com.ar/products/c85045dd-253f-4279-a95d-04615fbe1d7f.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/c85045dd-253f-4279-a95d-04615fbe1d7f.png?e=webp&q=50&d=400x400'),(102,'Salmon power Poke Bowl','https://images.rappi.com.ar/products/230b2df3-6dd5-42de-a72b-bab4c0dbb6bd.png?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/230b2df3-6dd5-42de-a72b-bab4c0dbb6bd.png?e=webp&q=50&d=400x400'),(103,'Alaska 12','https://images.rappi.com.ar/products/03c13027-a5e5-4ed2-b1bf-dafc70bc631c.jpeg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/03c13027-a5e5-4ed2-b1bf-dafc70bc631c.jpeg?e=webp&q=50&d=400x400'),(104,'Los Toldos Viejos Logo','https://scontent.fmdq6-1.fna.fbcdn.net/v/t39.30808-6/294897321_448293083973184_7885429536528877976_n.png?_nc_cat=105&ccb=1-7&_nc_sid=6ee11a&_nc_ohc=Ib51CW7Y7P8Q7kNvwGyq7k2&_nc_oc=AdkGIwH6tpdA4DoqaR6C4jxlmtWxuNkO5tarbdRC9q-BpM4FNLs67uY1ERvuCCBOuO4&_nc_zt=23&_nc_ht=scontent.fmdq6-1.fna&_nc_gid=sfBlhtPjF4itZaD2oHRfFw&oh=00_AfkEWPiCH0m50zIX6oCmRhb5UyltkHGJ6PngsaVe17qGfQ&oe=6941F045'),(105,'Bife de chorizo mariposa','https://images.rappi.com.ar/products/bfe9bfc0-abfd-4108-82c5-426667eba424.jpeg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/bfe9bfc0-abfd-4108-82c5-426667eba424.jpeg?e=webp&q=50&d=400x400'),(106,'Vacío','https://images.rappi.com.ar/products/ae4cea60-8536-472f-ace2-a1b4864ed407.jpeg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/ae4cea60-8536-472f-ace2-a1b4864ed407.jpeg?e=webp&q=50&d=400x400'),(107,'Ensalada Primaveral','https://images.rappi.com.ar/products/9d7344ad-718c-433d-8a46-11e37bce2cd6.jpeg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/9d7344ad-718c-433d-8a46-11e37bce2cd6.jpeg?e=webp&q=50&d=400x400'),(108,'Papas fritas','https://images.rappi.com.ar/products/d046105b-fe02-41a9-a01a-299ee5572775.jpeg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/d046105b-fe02-41a9-a01a-299ee5572775.jpeg?e=webp&q=50&d=400x400'),(109,'Vigilante','https://images.rappi.com.ar/products/7708243b-12ca-4455-9477-50079997eb15.jpeg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/7708243b-12ca-4455-9477-50079997eb15.jpeg?e=webp&q=50&d=400x400'),(110,'Chorizo puro cerdo','https://images.rappi.com.ar/products/034528cc-45e6-4b75-908d-016fb17c3122.jpeg?e=webp&q=50&d=400x400.com/https://images.rappi.com.ar/products/034528cc-45e6-4b75-908d-016fb17c3122.jpeg?e=webp&q=50&d=400x400');

INSERT INTO `menus` VALUES (1,'Menú Principal'),(2,'Menú Principal'),(3,'Menú Principal'),(4,'Menú Principal'),(5,'Menú Principal'),(6,'Menú Principal'),(7,'Menú Principal'),(8,'Menú Principal'),(9,'Menú Principal'),(10,'Menú Principal'),(11,'Menú Principal'),(12,'Menú Principal'),(13,'Menú Principal'),(14,'Menú Principal'),(15,'Menú Principal');

INSERT INTO `direcciones` VALUES (1,NULL,'Sarmiento','Mar del Plata','7600',NULL,-38.0118484,-57.5433272,'2685',NULL,NULL,17),(2,NULL,'Belgrano','Mar del Plata','7600',NULL,-37.9987569,-57.5528615,'3050',NULL,NULL,18),(3,NULL,'Alvarado','Mar del Plata','7600',NULL,-38.0160491,-57.5427236,'1355',NULL,NULL,19),(4,NULL,'Alvarado','Mar del Plata','7600',NULL,-38.0113619,-57.5520497,'2291',NULL,NULL,20),(5,NULL,'Rawson','Mar del Plata','7600',NULL,-38.0131362,-57.5420087,'1471',NULL,NULL,21),(6,NULL,'Castelli','Mar del Plata','7600',NULL,-38.0152993,-57.5425564,'1398',NULL,NULL,22),(7,NULL,'Rivadavia','Mar del Plata','7600',NULL,-37.9986932,-57.5522603,'3050',NULL,'Shopping Los Gallegos',23),(8,NULL,'Av Independencia','Mar del Plata','7600',NULL,-38.0035458,-57.558609,'2598',NULL,NULL,24),(9,NULL,'Matheu','Mar del Plata','7600',NULL,-38.0275134,-57.5384907,'390',NULL,NULL,25),(10,NULL,'Av Independencia','Mar del Plata','7600',NULL,-38.0096882,-57.5639174,'3383',NULL,NULL,26),(11,NULL,'Sarmiento','Mar del Plata','7600',NULL,-38.0118484,-57.5433272,'2685',NULL,NULL,27),(12,NULL,'Cordoba','Mar del Plata','7600',NULL,-38.0124649,-57.557297,'3318',NULL,NULL,28),(13,NULL,'Av Juan Jose Paso','Mar del Plata','7600',NULL,-38.0174962,-57.5672754,'3004',NULL,NULL,29),(14,NULL,'Av Independencia','Mar del Plata','7600',NULL,-38.0096882,-57.5639174,'3383',NULL,NULL,30),(15,NULL,'Leandro N Alem','Mar del Plata','7600',NULL,-38.0291438,-57.5373737,'3913',NULL,NULL,31);

INSERT INTO `restaurantes` VALUES (1,_binary '','McDonald\'s',1,1,1,17),(2,_binary '','Mostaza',2,8,2,18),(3,_binary '','El Club de la Milanesa',3,15,3,19),(4,_binary '','Estilo Sushi',4,20,4,20),(5,_binary '','Lebron Cervecería',5,28,5,21),(6,_binary '','Hell\'s Pizza',6,37,6,22),(7,_binary '','Burger King',7,44,7,23),(8,_binary '','La Reina del Parque',8,55,8,24),(9,_binary '','Coco Café',9,63,9,25),(10,_binary '','Che Sushi',10,70,10,26),(11,_binary '','Dean & Dennys',11,76,11,27),(12,_binary '','Chiqui Empanadas',12,81,12,28),(13,_binary '','Sao Medialunas',13,87,13,29),(14,_binary '','Poke Pop',14,98,14,30),(15,_binary '','Los Toldos Viejos',15,104,15,31);

INSERT INTO `horarios_atencion` VALUES (1,_binary '\0','MONDAY','10:00:00.000000','23:30:00.000000',1),(2,_binary '\0','TUESDAY','10:00:00.000000','23:30:00.000000',1),(3,_binary '\0','WEDNESDAY','10:00:00.000000','23:30:00.000000',1),(4,_binary '\0','THURSDAY','10:00:00.000000','23:30:00.000000',1),(5,_binary '','FRIDAY','10:00:00.000000','00:30:00.000000',1),(6,_binary '','SATURDAY','10:00:00.000000','00:30:00.000000',1),(7,_binary '\0','SUNDAY','10:00:00.000000','23:30:00.000000',1),(8,_binary '\0','MONDAY','10:00:00.000000','23:00:00.000000',2),(9,_binary '\0','TUESDAY','10:00:00.000000','23:00:00.000000',2),(10,_binary '\0','WEDNESDAY','10:00:00.000000','23:00:00.000000',2),(11,_binary '\0','THURSDAY','10:00:00.000000','23:00:00.000000',2),(12,_binary '','FRIDAY','10:00:00.000000','00:00:00.000000',2),(13,_binary '','SATURDAY','10:00:00.000000','00:00:00.000000',2),(14,_binary '\0','SUNDAY','10:00:00.000000','23:00:00.000000',2),(15,_binary '\0','MONDAY','11:30:00.000000','23:45:00.000000',3),(16,_binary '\0','TUESDAY','11:30:00.000000','23:45:00.000000',3),(17,_binary '\0','WEDNESDAY','11:30:00.000000','23:45:00.000000',3),(18,_binary '\0','THURSDAY','11:30:00.000000','23:45:00.000000',3),(20,_binary '\0','SATURDAY','11:30:00.000000','23:45:00.000000',3),(21,_binary '\0','SUNDAY','11:30:00.000000','23:45:00.000000',3),(22,_binary '\0','TUESDAY','10:00:00.000000','23:00:00.000000',4),(23,_binary '\0','WEDNESDAY','10:00:00.000000','23:00:00.000000',4),(24,_binary '\0','THURSDAY','10:00:00.000000','23:00:00.000000',4),(25,_binary '\0','FRIDAY','10:00:00.000000','23:00:00.000000',4),(26,_binary '\0','SATURDAY','10:00:00.000000','23:00:00.000000',4),(27,_binary '\0','SUNDAY','10:00:00.000000','23:00:00.000000',4),(28,_binary '','WEDNESDAY','18:00:00.000000','01:00:00.000000',5),(29,_binary '','THURSDAY','18:00:00.000000','01:30:00.000000',5),(30,_binary '','FRIDAY','18:00:00.000000','02:00:00.000000',5),(31,_binary '','SATURDAY','18:00:00.000000','02:00:00.000000',5),(32,_binary '','SUNDAY','18:00:00.000000','00:30:00.000000',5),(33,_binary '\0','TUESDAY','11:00:00.000000','23:30:00.000000',6),(34,_binary '\0','WEDNESDAY','11:00:00.000000','23:30:00.000000',6),(35,_binary '\0','THURSDAY','11:00:00.000000','23:30:00.000000',6),(36,_binary '','FRIDAY','12:30:00.000000','00:00:00.000000',6),(37,_binary '','SATURDAY','12:30:00.000000','00:00:00.000000',6),(38,_binary '\0','SUNDAY','12:00:00.000000','23:30:00.000000',6),(39,_binary '\0','FRIDAY','11:30:00.000000','23:45:00.000000',3),(40,_binary '\0','MONDAY','07:00:00.000000','11:00:00.000000',3),(41,_binary '','FRIDAY','09:00:00.000000','01:00:00.000000',7),(42,_binary '','SATURDAY','09:00:00.000000','01:00:00.000000',7),(43,_binary '','SUNDAY','10:00:00.000000','01:00:00.000000',7),(44,_binary '\0','MONDAY','09:00:00.000000','23:30:00.000000',7),(45,_binary '\0','TUESDAY','09:00:00.000000','23:30:00.000000',7),(46,_binary '\0','WEDNESDAY','09:00:00.000000','23:30:00.000000',7),(47,_binary '\0','THURSDAY','09:00:00.000000','23:30:00.000000',7),(48,_binary '\0','MONDAY','07:00:00.000000','21:00:00.000000',8),(49,_binary '\0','TUESDAY','07:00:00.000000','21:00:00.000000',8),(50,_binary '\0','WEDNESDAY','07:00:00.000000','21:00:00.000000',8),(51,_binary '\0','THURSDAY','07:00:00.000000','21:00:00.000000',8),(52,_binary '\0','FRIDAY','07:00:00.000000','21:00:00.000000',8),(53,_binary '\0','SATURDAY','07:00:00.000000','21:00:00.000000',8),(54,_binary '\0','SUNDAY','07:00:00.000000','21:00:00.000000',8),(55,_binary '\0','TUESDAY','07:00:00.000000','23:30:00.000000',9),(56,_binary '\0','MONDAY','07:00:00.000000','23:30:00.000000',9),(57,_binary '\0','WEDNESDAY','07:00:00.000000','23:30:00.000000',9),(58,_binary '\0','THURSDAY','07:00:00.000000','23:30:00.000000',9),(59,_binary '\0','FRIDAY','07:00:00.000000','23:30:00.000000',9),(60,_binary '\0','SATURDAY','07:00:00.000000','23:30:00.000000',9),(61,_binary '\0','SUNDAY','07:00:00.000000','23:30:00.000000',9),(62,_binary '\0','WEDNESDAY','19:00:00.000000','23:30:00.000000',10),(63,_binary '\0','THURSDAY','19:00:00.000000','23:30:00.000000',10),(64,_binary '','FRIDAY','19:00:00.000000','01:00:00.000000',10),(65,_binary '','SATURDAY','19:00:00.000000','01:00:00.000000',10),(66,_binary '\0','SUNDAY','19:00:00.000000','23:30:00.000000',10),(67,_binary '\0','MONDAY','10:00:00.000000','23:30:00.000000',11),(68,_binary '\0','TUESDAY','10:00:00.000000','23:30:00.000000',11),(69,_binary '\0','WEDNESDAY','10:00:00.000000','23:30:00.000000',11),(70,_binary '\0','THURSDAY','10:00:00.000000','23:30:00.000000',11),(71,_binary '','FRIDAY','10:00:00.000000','00:00:00.000000',11),(72,_binary '','SATURDAY','10:00:00.000000','00:00:00.000000',11),(73,_binary '','SUNDAY','10:00:00.000000','00:30:00.000000',11),(74,_binary '\0','MONDAY','11:30:00.000000','14:30:00.000000',12),(75,_binary '\0','MONDAY','19:00:00.000000','23:00:00.000000',12),(76,_binary '\0','TUESDAY','11:30:00.000000','14:30:00.000000',12),(77,_binary '\0','TUESDAY','19:00:00.000000','23:00:00.000000',12),(78,_binary '\0','WEDNESDAY','11:30:00.000000','14:30:00.000000',12),(79,_binary '\0','WEDNESDAY','19:00:00.000000','23:00:00.000000',12),(80,_binary '\0','THURSDAY','11:30:00.000000','14:30:00.000000',12),(81,_binary '\0','THURSDAY','19:00:00.000000','23:30:00.000000',12),(82,_binary '\0','FRIDAY','11:30:00.000000','14:30:00.000000',12),(83,_binary '','FRIDAY','19:00:00.000000','00:30:00.000000',12),(84,_binary '','SATURDAY','11:30:00.000000','00:30:00.000000',12),(85,_binary '','SUNDAY','19:00:00.000000','00:30:00.000000',12),(86,_binary '\0','MONDAY','07:00:00.000000','20:00:00.000000',13),(87,_binary '\0','TUESDAY','07:00:00.000000','20:00:00.000000',13),(88,_binary '\0','WEDNESDAY','07:00:00.000000','20:00:00.000000',13),(89,_binary '\0','THURSDAY','07:00:00.000000','20:00:00.000000',13),(90,_binary '\0','FRIDAY','07:00:00.000000','20:00:00.000000',13),(91,_binary '\0','SATURDAY','07:00:00.000000','20:00:00.000000',13),(92,_binary '\0','SUNDAY','07:00:00.000000','20:00:00.000000',13),(93,_binary '\0','MONDAY','07:00:00.000000','23:30:00.000000',14),(94,_binary '\0','TUESDAY','07:00:00.000000','23:30:00.000000',14),(95,_binary '\0','WEDNESDAY','07:00:00.000000','23:30:00.000000',14),(96,_binary '\0','THURSDAY','07:00:00.000000','23:30:00.000000',14),(97,_binary '\0','FRIDAY','07:00:00.000000','23:30:00.000000',14),(98,_binary '\0','SATURDAY','07:00:00.000000','23:30:00.000000',14),(99,_binary '\0','SUNDAY','07:00:00.000000','23:30:00.000000',14),(100,_binary '\0','MONDAY','09:30:00.000000','15:00:00.000000',15),(101,_binary '','MONDAY','19:00:00.000000','00:00:00.000000',15),(102,_binary '\0','TUESDAY','09:30:00.000000','14:00:00.000000',15),(103,_binary '','TUESDAY','19:00:00.000000','00:00:00.000000',15),(104,_binary '','THURSDAY','19:00:00.000000','00:00:00.000000',15),(105,_binary '\0','THURSDAY','09:30:00.000000','14:00:00.000000',15),(106,_binary '\0','FRIDAY','09:30:00.000000','14:00:00.000000',15),(107,_binary '','FRIDAY','19:00:00.000000','01:00:00.000000',15),(108,_binary '\0','SATURDAY','09:00:00.000000','15:00:00.000000',15),(109,_binary '','SATURDAY','19:00:00.000000','01:00:00.000000',15),(110,_binary '\0','SUNDAY','09:00:00.000000','15:00:00.000000',15),(111,_binary '','SUNDAY','19:00:00.000000','01:00:00.000000',15);

INSERT INTO `productos` VALUES (1,'Hamburguesa doble carne 100% vacuna, dos fetas de queso cheddar, kétchup, mostaza y cebolla. Acompañamiento y bebida mediana a elección.',_binary '','Doble Cuarto de Libra',19200.00,2,1),(2,'Hamburguesa con doble carne 100% vacuna, salsa tasty, 3 fetas de queso cheddar, lechuga, tomate, cebolla, en un pan con semillas de sésamo. Acompañamiento y bebida a elección.',_binary '','Grand Doble Tasty',20900.00,3,1),(3,'Hamburguesa con doble carne 100% vacuna, salsa Big Mac, queso derretido, cebolla, lechuga y pepino. Acompañamiento y bebida a elección.',_binary '','Big Mac',15600.00,4,1),(4,'Hamburguesa doble con queso cheddar, dos tiras de bacon enteras, cebolla grillada en pan con semillas de sésamo. Acompañamiento y bebida a elección.',_binary '','Doble Bacon Cheedar McMelt',20700.00,5,1),(5,'10 McNuggets de pollo. Acompañamiento y bebida a elección',_binary '','McNuggets X10',19500.00,6,1),(6,'Hamburguesa de carne 100% vacuna, queso cheddar, lechuga, tomate, kétchup, mostaza, mayonesa y cebolla. Acompañamiento y bebida a elección',_binary '','McNífica',17000.00,7,1),(7,'Mega Hamburguesa de carne con queso provolone, bacon, papitas, cebolla morada y salsa chiminesa, acompañada de papas coated regulares y bebida a elección.',_binary '','Homo Argentum',20500.00,9,2),(8,'Mega Hamburguesa de carne con queso cheddar, bacon, pepino, cebolla crispy y barbacoa, acompañada de papas Coated regulares y bebida a elección.',_binary '','TR1 X Trueno',20900.00,10,2),(9,'Mega Hamburguesa con doble carne, queso cheddar, kétchup, mostaza y cebollita, acompañada de papas coated regulares y bebida a elección.',_binary '','Doble Cuarto',20500.00,11,2),(10,'Ensalada con pollo crispy o grill, lechuga, tomate, cebollita, queso en hebras y croutons.',_binary '','Ensalada Royal Crispy',12900.00,12,2),(11,'Hamburguesa 100% a base de vegetales, con lechuga, tomate, pepino, mostaza, kétchup y cebollita, acompañada de papas coated regulares y bebida a elección.',_binary '','Cero Carne Veggie',16900.00,13,2),(12,'Ensalada con lechuga, tomate, salsa de palta, huevo y cebolla, acompañada de pollo y bebida a elección.',_binary '','Ensalada Palta y Huevo Grill',16700.00,14,2),(13,'Pechuga de pollo rebozada (280 g) con muzzarella, jamón, tomate y huevo frito. Incluye guarnición a elección.',_binary '','Suprema del Club',25900.00,16,3),(14,'Milanesa de ternera Angus con huevo frito y un toque de perejil fresco. Incluye guarnición a elección.',_binary '','Milanesa a caballo',23700.00,17,3),(15,'Milanesa de ternera Angus, con muzza, morrón asado, nachos, salsa criolla, sriracha y verdeo. Viene con guarnición.',_binary '','Milanesa Sweet Spicy',25900.00,18,3),(16,'Milanesa de ternera Angus, con salsa barbacoa, queso cheddar, panceta y huevo frito. Viene con guarnición.',_binary '','Milanesa Gringa',25900.00,19,3),(17,'Rolls: california roll, drago roll, fresh roll, chicken roll, futurama roll,soul roll, vegetariano roll, gunkans de tamago, makis de atún.',_binary '','Combinado N1 - 12 Piezas Mixto',23560.00,21,4),(18,'Rolls: california roll, drago roll, fresh roll, chicken roll, futurama roll,soul roll, vegetariano roll, gunkans de tamago, makis de atún.',_binary '','Combinado N1 - 15 Piezas Mixto',27550.00,22,4),(19,'Rolls: bs as roll, fresh roll ,bremen roll , mdp roll, guncans de tamago, maracuyá y munich, niguiris y geishas y maki de salmon.',_binary '','Combinado N2 - 12 Piezas Salmon',32520.00,23,4),(20,'Rolls: eby, serrano, roma, liverpool, kira, fresh, bremen, mdp. gunkans: futumango, tamago, maracuyá, munich y nigiris y geishas. maki: salmón , langostino',_binary '','Combinado 2/3 - 12p Salmon y Langostino',25250.00,24,4),(21,'Tapas de arroz blanco con semillas de sésamo, rellena de salmón ahumado, palta hass o durazno, queso philadelphia y verdeo. salsa teriyaki ahumada para compañar',_binary '','Ahumada Burger',22940.00,25,4),(22,'Tapas de arroz blanco con semillas de sesamo, rellena de salmon, langostinos rebozados, palta hass, queso philadelphia y verdeo, salsa buenos aires para acompañar',_binary '','Del Trono Burger',20900.00,26,4),(23,'Ensalada de base de arroz japonés, salmón ahumado, durazno o palta y queso philadelphia con sésamo mixto.',_binary '','Poke Londres',25230.00,27,4),(24,'2 Hamburguesas simples con queso cheddar + papas',_binary '','2 Cheese burger + papas',18000.00,29,5),(25,'2 Hamburguesas de 120 gr de carne, queso cheddar, sweet bacón, salsa bbq con guarnición de papas fritas y 1 lt de cerveza artesanal.',_binary '','Combo 2 classic bacon 1 lt cerveza',30000.00,30,5),(26,'Papas con cheddar y verdeo.',_binary '','Papas con cheddar',14800.00,31,5),(27,'Papas fritas cubiertas con bondiola desmenuzada en salsa bbq.',_binary '','Papas pork',17900.00,32,5),(28,'Pechuguitas de pollo rebozadas en cereales con dip de mostaza honey.',_binary '','Chicken fingers',13900.00,33,5),(29,'En pan de molde, medallon de carne de 120 grs, cheddar x 4, bacon, cebolla caramelizada y salsa alioli.',_binary '','Bacon patty melt burger',14900.00,34,5),(30,'Lata gaseosa pepsi.',_binary '','Pepsi regular 354 ml',2500.00,35,5),(31,'Envase pet descartable.',_binary '','Lebron honey 1 litro',6000.00,36,5),(32,'Pizza grande tradicional de 33cm de diámetro de mozzarella.',_binary '','Lincoln grande',21700.00,38,6),(33,'Pizza grande tradicional de 33cm de diámetro de pepperoni y mozzarella.',_binary '','Obama grande',25100.00,39,6),(34,'Pizza xxl de pollo, cheddar, bbq, cebolla morada. p/4.',_binary '','Chick norris xxl',43200.00,40,6),(35,'Mozzarella, jamón , ananá grillado, salsa ranch y ciboulette.',_binary '','Hawaiana grande',34000.00,41,6),(36,'Pizza xxl de tomate, provenzal y crema de burrata. p/4.',_binary '','Napoletta xxl',42600.00,42,6),(37,'Spaguetti con albondigas',_binary '\0','Spaguetti meatballs',14900.00,43,6),(38,'Doble carne a la parrilla, queso, ketchup, mostaza y pan. Acompañado con papas fritas regulares.',_binary '','Doble Carne Doble Queso + Papas Regular',15831.00,45,7),(39,'Doble carne XL a la parrilla, pan, Muzarellitas salsa stacker, panceta, queso cheddar, bebida y papas regulares',_binary '','Combo Stacker Doble XL Muzarella',24700.00,46,7),(40,'Carne a la parrilla, pan, queso cheddar, barbacoa y panceta. Acompañado con papas regulares y gaseosa 500ml.',_binary '','Combo BBQ Bacon',21000.00,47,7),(41,'Cada Combo: 1 Carne a la parrilla XL, queso Cheddar ,cebolla caramelizada y pan, acompañado con papas regulares y gaseosa 500ml.',_binary '','2x1 Combo Cheese Onion XL',37000.00,48,7),(42,'Hamburguesa a la parrilla con alioli, guacamole, queso cheddar, lechuga y cebolla, servida en pan de papa. Acompañada con papas regulares y bebida de 500ml',_binary '','Combo Guacamole King Simple',22000.00,49,7),(43,'Wrap relleno con atún, rúcula, tomate, cebolla y mayonesa, acompañado por una bebida de 500ml',_binary '','Wrap Atun + bebida',13500.00,50,7),(44,'Lechuga, queso en hebras, croutons, pollo crispy y agua mineral 500ml.',_binary '\0','Combo Ensalada Caesar con pollo',12600.00,51,7),(45,'Triple carne a la parrilla, triple queso cheddar, cebolla rehogada, ketchup y mostaza, papas regulares y gaseosa 500ml.',_binary '\0','Combo Triple Onion Cheddar',15000.00,52,7),(46,'Carne a la parrilla, pan, mayonesa, ketchup, cebolla, tomate, pepinos y lechuga, papas regulares y gaseosa 500ml.',_binary '','Combo Whopper + Franui',22500.00,53,7),(47,'Pollo crispy, pan especial, lechuga, mayonesa, papas regulares y gaseosa 500ml.',_binary '','Combo King de Pollo',17500.00,54,7),(48,'Budín con ralladura de limón y cobertura de glasé',_binary '','Budín de limón',9500.00,56,8),(49,'1 Kg de pan surtido: común, casero o negro.',_binary '','1Kg de pan surtido',4800.00,57,8),(50,'Tarteleta de dulce de leche bañada en chocolate blanco.',_binary '','Tarteleta picos blancos',3400.00,58,8),(51,'Tarteleta de cereza con dulce de leche.',_binary '\0','Tarteleta de cerezas',3400.00,59,8),(52,'Un cuarto kilo de bizcochitos de grasa.',_binary '','1/4 Kilo de bizcochitos de grasa',7000.00,60,8),(53,'Tarteleta de dulce de leche bañada en chocolate.',_binary '','Tarteleta picos negros',3400.00,61,8),(54,'20 Cm diametro.',_binary '','Pasta frola de membrillo',20500.00,62,8),(55,'Desayuno con café con leche, 3 medialunas.',_binary '','Desayuno Tradicional',8000.00,64,9),(56,'Tostado en pan casero relleno de jamon y queso y gratinado arriba',_binary '','Tostado Gratten',8000.00,65,9),(57,'Desayuno con café con leche, jugo de naranja, medialunas, tostadas, manteca, mermelada.',_binary '','Desayuno Continental',16000.00,66,9),(58,'Nuestro desayuno para regalar incluye botellita de jugo de naranja - granola con frutas y yogurt - green toast  - tostadas casera queso y mermelada- monoporcion de torta - 3 medialunas',_binary '','Cool Box',55000.00,67,9),(59,'Docena de medialuna de manteca',_binary '','Docena de Medialunas',12000.00,68,9),(60,'Café con leche, granola casera, yogurt, frutas de estación.',_binary '','Granola Bowl con Cafe con Leche',13000.00,69,9),(61,'Fresca ensalada de langostinos rebozados, palta, queso phila, repollo y zanahoria sobre arroz.',_binary '','Poke Langostinos',12940.00,71,10),(62,'Fresca ensalada de pollo rebozado, salsa teriyaki, guacamole y queso phila, sobre arroz gohan Incluye 1 Salsa Soja',_binary '','Poke Salad Mex',12940.00,72,10),(63,'2 unidades. Empanaditas chinas de verdura',_binary '','Spring Roll Verdura x 2',5999.00,73,10),(64,'5 piezas. Relleno de salmón , palta y queso philadelphia, envuelto en tamago con topping de praline de mani. Incluye palitos, soja y wasabi.',_binary '','Roll Sublime',6450.00,74,10),(65,'5 piezas. Deliciosa lonjas del más fresco salmón rosado.',_binary '','Sashimi Salmon',8450.00,75,10),(66,'Hamburguesa doble de carne vacuna de 120g, cheddar, lechuga, tomate, panceta, barbacoa y salsa dennys con papas grandes.',_binary '','Super Bacon Burger Doble & Papas Grandes',18800.00,77,11),(67,'Hamburguesa de carne vacuna de 120g, queso cheddar, salsa dennys o magic y porción de papas fritas grandes.',_binary '','Combo Cheeseburger Simple (120g) & Papas',13200.00,78,11),(68,'100% Pechuga de pollo crujiente, lechuga, salsa dennys & papas.',_binary '','Combo Burger Crispy Chicken & Papas',13700.00,79,11),(69,'2 Hamburguesas dobles de carne vacuna de 120g c/u, queso cheddar, salsa a elección y una porción de papas fritas grandes',_binary '','2 Cheeseburger Clásicas Dobles & 1 Papas',21999.00,80,11),(70,'Carne secreta cocida al horno de barro, cebolla salteada, tomate y verdeo. Cocido en fondo de cocción durante 2hs',_binary '','Carne Cortada a Cuchillo',2600.00,82,12),(71,'Jamón natural y queso mozzarella.',_binary '','Jamón y Mozarella',2600.00,83,12),(72,'Choclo natural, salsa blanca, cebolla salteada, queso sardo y queso cremoso.',_binary '','Empanada de Humita',2600.00,84,12),(73,'Tomates asados, pesto de albahaca, mozzarella.',_binary '','Empanada Capresse',2600.00,85,12),(74,'Roquefort en su punto justo, queso cremoso, pesto espinaca y almendras.',_binary '','Empanada de Roquefort',2700.00,86,12),(75,'6 Medialunas clásicas más 3 medialunas rellenas de crema pastelera y 3 medialunas rellenas de dulce de leche.',_binary '','Docena De Medialunas Mixtas',28000.00,88,13),(76,'Cookie rellena de chocolate semiamargo y trozos de chocolate blanco.',_binary '','Cookie Rellena De Chocolate',4800.00,89,13),(77,'3 Medialunas clásicas más 3 medialunas rellenas de crema pastelera.',_binary '','1/2 Doc. Med. Mix Pastelera',14000.00,90,13),(78,'Café con leche en vaso de 14oz más 3 medialunas dulces clásicas.',_binary '','São 1',11700.00,91,13),(84,'12 Medialunas clásicas',_binary '','Docena De Medialunas',23200.00,97,13),(85,'Riquisimos langostinos rebozados en panko, acompañados con salsa tártara.',_binary '','Langos rebozados',8900.00,99,14),(86,'Ensalada de salmón mango, salsa maracuyá, cebolla morada brunoise y crocante de batata palta, queso phila, sobre arroz gohan. Incluye salsa de soja.',_binary '','Sushi Salad Salmango',12499.00,100,14),(87,'425 gr - Fresca ensalada de salmón palta, queso phila, repollo y zanahoria sobre arroz. + 1 soja',_binary '','Poke Salmón',12999.00,101,14),(88,'Ensalada de arroz, salmón, palta, repollo curado, pickle de pepino, tomates cherry, batata crispy, mayonesa de wasabi y salsa nikkei.',_binary '','Salmon power Poke Bowl',14999.00,102,14),(89,'12 piezas de sushi con 3 NY Phila (salmón, phila, palta), 3 Hot NY Phila con guacamole (salmón, rebozado), 3 Paris (salmón, phila), 3 Clerc (salmón cocido, phila). Incluye 1 soja, 1 wasabi y 1 palito.',_binary '','Alaska 12',14284.50,103,14),(90,'Bife de chorizo corte mariposa, 900gr para compartir .',_binary '','Bife de chorizo mariposa',52500.00,105,15),(91,'Porción de vacio de ternero.',_binary '','Vacío',45800.00,106,15),(92,'Ensalada de palta, tomates cherry, choclo, zanahoria, rúcula y láminas de queso.',_binary '','Ensalada Primaveral',12320.00,107,15),(93,'Ración de papas bastón cortadas a cuchillo.',_binary '','Papas fritas',7600.00,108,15),(94,'Queso fresco y dulce de membrillo.',_binary '','Vigilante',5700.00,109,15),(95,'Chorizo de puro cerdo de la nona para compartir',_binary '','Chorizo puro cerdo',5700.00,110,15);

INSERT INTO `promociones` VALUES (1,_binary '','PRODUCTOS_ESPECIFICOS','Descuento increible.',11,'2025-12-18','2025-12-11','PORCENTAJE',1),(2,_binary '','PRODUCTOS_ESPECIFICOS','Descuento',30,'2025-12-19','2025-12-11','PORCENTAJE',3),(3,_binary '','PRODUCTOS_ESPECIFICOS','Descuento',1000,'2025-12-18','2025-12-11','MONTO_FIJO',5),(4,_binary '','TODO_MENU','Descuento',10,'2025-12-20','2025-12-11','PORCENTAJE',6),(5,_binary '','PRODUCTOS_ESPECIFICOS','Descuento 5%',5,'2025-12-27','2025-12-12','PORCENTAJE',9),(7,_binary '','TODO_MENU','Descuento fijo $500',500,'2025-12-12','2025-12-12','MONTO_FIJO',9);

INSERT INTO `promocion_productos` VALUES (1,2),(2,13),(3,31),(5,58);


-- =====================================================
-- DATOS ADICIONALES PARA TESIS
-- =====================================================

-- 10) Direcciones de clientes (usuarios 2-16, IDs 16-30)
INSERT INTO `direcciones` VALUES
(16,NULL,'San Martin','Mar del Plata','7600',NULL,-38.0055,-57.5426,'2150','Casa','Frente a plaza principal',2),
(17,'2','Luro','Mar del Plata','7600','B',-38.0012,-57.5498,'3200','Departamento','Edificio Torres del Sol',3),
(18,NULL,'Guemes','Mar del Plata','7600',NULL,-38.0089,-57.5512,'1850',NULL,NULL,4),
(19,'5','Brown','Mar del Plata','7600','A',-37.9945,-57.5478,'2780','Oficina','Entrada por cochera',5),
(20,NULL,'Mitre','Mar del Plata','7600',NULL,-38.0134,-57.5445,'1920','Casa de verano',NULL,6),
(21,NULL,'Moreno','Mar del Plata','7600',NULL,-38.0078,-57.5534,'2340',NULL,'Timbre roto, golpear',7),
(22,'8','Colon','Mar del Plata','7600','C',-38.0023,-57.5467,'2890','Depto centro',NULL,8),
(23,NULL,'Bolivar','Mar del Plata','7600',NULL,-38.0156,-57.5389,'1650',NULL,NULL,9),
(24,NULL,'San Juan','Mar del Plata','7600',NULL,-38.0067,-57.5556,'3100','Mi casa',NULL,10),
(25,'3','Alem','Mar del Plata','7600','D',-38.0098,-57.5423,'2450','Trabajo',NULL,11),
(26,NULL,'Falucho','Mar del Plata','7600',NULL,-38.0045,-57.5501,'1780',NULL,'Portero electronico',12),
(27,NULL,'Santiago del Estero','Mar del Plata','7600',NULL,-38.0112,-57.5478,'2560','Casa',NULL,13),
(28,'1','Catamarca','Mar del Plata','7600','E',-38.0034,-57.5534,'1890',NULL,NULL,14),
(29,NULL,'La Rioja','Mar del Plata','7600',NULL,-38.0089,-57.5412,'2230','Depto nuevo',NULL,15),
(30,NULL,'Entre Rios','Mar del Plata','7600',NULL,-38.0056,-57.5489,'2670',NULL,'Casa verde con rejas',16);

-- 11) Pedidos historicos (distribuidos en los ultimos 6 meses)
-- Clientes: 2-16, Restaurantes: 1-15, Repartidores: 32-46
-- Estados: PENDIENTE, EN_PREPARACION, LISTO_PARA_RETIRAR, LISTO_PARA_ENTREGAR, ASIGNADO_A_REPARTIDOR, EN_CAMINO, COMPLETADO, CANCELADO
-- Tipos: DELIVERY, RETIRO_POR_LOCAL

INSERT INTO `pedidos` (id, tipo_entrega, fecha_hora, estado, total, subtotal_productos, costo_delivery, distancia_km, baja_logica, motivo_baja, fecha_baja, estado_antes_de_cancelado, usuario_id, restaurante_id, repartidor_id, fecha_aceptacion_repartidor, pin, fecha_entrega, direccion_id, direccion_snapshot) VALUES
-- JUNIO 2025 - Pedidos completados
(1,'DELIVERY','2025-06-15 12:30:00','COMPLETADO',24200.00,19200.00,5000.00,3.2,false,NULL,NULL,NULL,2,1,32,'2025-06-15 12:45:00','1234','2025-06-15 13:15:00',16,'San Martin 2150, Mar del Plata (7600)'),
(2,'DELIVERY','2025-06-16 19:45:00','COMPLETADO',27400.00,20900.00,6500.00,4.5,false,NULL,NULL,NULL,3,1,33,'2025-06-16 20:00:00','5678','2025-06-16 20:35:00',17,'Luro 3200, Piso 2 B, Mar del Plata (7600)'),
(3,'RETIRO_POR_LOCAL','2025-06-18 13:00:00','COMPLETADO',25900.00,25900.00,NULL,NULL,false,NULL,NULL,NULL,4,3,NULL,NULL,NULL,'2025-06-18 13:30:00',NULL,NULL),
(4,'DELIVERY','2025-06-20 20:15:00','COMPLETADO',28560.00,23560.00,5000.00,2.8,false,NULL,NULL,NULL,5,4,34,'2025-06-20 20:30:00','9012','2025-06-20 21:00:00',19,'Brown 2780, Piso 5 A, Mar del Plata (7600)'),
(5,'DELIVERY','2025-06-22 21:00:00','COMPLETADO',35700.00,21700.00,14000.00,8.0,false,NULL,NULL,NULL,6,6,35,'2025-06-22 21:20:00','3456','2025-06-22 22:00:00',20,'Mitre 1920, Mar del Plata (7600)'),
(6,'RETIRO_POR_LOCAL','2025-06-25 12:00:00','COMPLETADO',18000.00,18000.00,NULL,NULL,false,NULL,NULL,NULL,7,5,NULL,NULL,NULL,'2025-06-25 12:25:00',NULL,NULL),
(7,'DELIVERY','2025-06-28 19:30:00','COMPLETADO',22500.00,15831.00,6669.00,4.2,false,NULL,NULL,NULL,8,7,36,'2025-06-28 19:45:00','7890','2025-06-28 20:20:00',22,'Colon 2890, Piso 8 C, Mar del Plata (7600)'),

-- JULIO 2025
(8,'DELIVERY','2025-07-02 13:15:00','COMPLETADO',20600.00,15600.00,5000.00,2.5,false,NULL,NULL,NULL,9,1,37,'2025-07-02 13:30:00','2345','2025-07-02 14:00:00',23,'Bolivar 1650, Mar del Plata (7600)'),
(9,'DELIVERY','2025-07-05 20:00:00','COMPLETADO',30500.00,20500.00,10000.00,6.0,false,NULL,NULL,NULL,10,2,38,'2025-07-05 20:20:00','6789','2025-07-05 20:55:00',24,'San Juan 3100, Mar del Plata (7600)'),
(10,'RETIRO_POR_LOCAL','2025-07-08 12:30:00','COMPLETADO',47400.00,47400.00,NULL,NULL,false,NULL,NULL,NULL,11,3,NULL,NULL,NULL,'2025-07-08 13:00:00',NULL,NULL),
(11,'DELIVERY','2025-07-10 19:00:00','COMPLETADO',37520.00,32520.00,5000.00,2.2,false,NULL,NULL,NULL,12,4,39,'2025-07-10 19:15:00','0123','2025-07-10 19:45:00',26,'Falucho 1780, Mar del Plata (7600)'),
(12,'DELIVERY','2025-07-12 21:30:00','COMPLETADO',37000.00,30000.00,7000.00,5.0,false,NULL,NULL,NULL,13,5,40,'2025-07-12 21:45:00','4567','2025-07-12 22:20:00',27,'Santiago del Estero 2560, Mar del Plata (7600)'),
(13,'RETIRO_POR_LOCAL','2025-07-15 13:00:00','COMPLETADO',25100.00,25100.00,NULL,NULL,false,NULL,NULL,NULL,14,6,NULL,NULL,NULL,'2025-07-15 13:25:00',NULL,NULL),
(14,'DELIVERY','2025-07-18 20:30:00','COMPLETADO',29700.00,24700.00,5000.00,2.5,false,NULL,NULL,NULL,15,7,41,'2025-07-18 20:45:00','8901','2025-07-18 21:15:00',29,'La Rioja 2230, Mar del Plata (7600)'),
(15,'DELIVERY','2025-07-20 19:15:00','COMPLETADO',14300.00,9500.00,4800.00,3.0,false,NULL,NULL,NULL,16,8,42,'2025-07-20 19:30:00','2345','2025-07-20 20:00:00',30,'Entre Rios 2670, Mar del Plata (7600)'),
(16,'RETIRO_POR_LOCAL','2025-07-22 10:00:00','COMPLETADO',8000.00,8000.00,NULL,NULL,false,NULL,NULL,NULL,2,9,NULL,NULL,NULL,'2025-07-22 10:20:00',NULL,NULL),
(17,'DELIVERY','2025-07-25 20:45:00','COMPLETADO',19440.00,12940.00,6500.00,4.5,false,NULL,NULL,NULL,3,10,43,'2025-07-25 21:00:00','6789','2025-07-25 21:35:00',17,'Luro 3200, Piso 2 B, Mar del Plata (7600)'),
(18,'DELIVERY','2025-07-28 19:00:00','COMPLETADO',23800.00,18800.00,5000.00,2.8,false,NULL,NULL,NULL,4,11,44,'2025-07-28 19:15:00','0123','2025-07-28 19:45:00',18,'Guemes 1850, Mar del Plata (7600)'),

-- AGOSTO 2025
(19,'DELIVERY','2025-08-01 12:00:00','COMPLETADO',24200.00,19200.00,5000.00,2.5,false,NULL,NULL,NULL,5,1,45,'2025-08-01 12:15:00','4567','2025-08-01 12:45:00',19,'Brown 2780, Piso 5 A, Mar del Plata (7600)'),
(20,'RETIRO_POR_LOCAL','2025-08-03 13:30:00','COMPLETADO',20900.00,20900.00,NULL,NULL,false,NULL,NULL,NULL,6,2,NULL,NULL,NULL,'2025-08-03 14:00:00',NULL,NULL),
(21,'DELIVERY','2025-08-05 20:00:00','COMPLETADO',30900.00,25900.00,5000.00,2.2,false,NULL,NULL,NULL,7,3,46,'2025-08-05 20:15:00','8901','2025-08-05 20:45:00',21,'Moreno 2340, Mar del Plata (7600)'),
(22,'DELIVERY','2025-08-08 19:30:00','COMPLETADO',30060.00,23560.00,6500.00,4.5,false,NULL,NULL,NULL,8,4,32,'2025-08-08 19:45:00','2345','2025-08-08 20:20:00',22,'Colon 2890, Piso 8 C, Mar del Plata (7600)'),
(23,'RETIRO_POR_LOCAL','2025-08-10 21:00:00','COMPLETADO',18000.00,18000.00,NULL,NULL,false,NULL,NULL,NULL,9,5,NULL,NULL,NULL,'2025-08-10 21:25:00',NULL,NULL),
(24,'DELIVERY','2025-08-12 20:15:00','COMPLETADO',32600.00,43200.00,-10600.00,6.0,false,NULL,NULL,NULL,10,6,33,'2025-08-12 20:30:00','6789','2025-08-12 21:05:00',24,'San Juan 3100, Mar del Plata (7600)'),
(25,'DELIVERY','2025-08-15 19:45:00','COMPLETADO',28000.00,21000.00,7000.00,5.0,false,NULL,NULL,NULL,11,7,34,'2025-08-15 20:00:00','0123','2025-08-15 20:35:00',25,'Alem 2450, Piso 3 D, Mar del Plata (7600)'),
(26,'RETIRO_POR_LOCAL','2025-08-18 12:00:00','COMPLETADO',3400.00,3400.00,NULL,NULL,false,NULL,NULL,NULL,12,8,NULL,NULL,NULL,'2025-08-18 12:15:00',NULL,NULL),
(27,'DELIVERY','2025-08-20 10:30:00','COMPLETADO',13000.00,8000.00,5000.00,2.8,false,NULL,NULL,NULL,13,9,35,'2025-08-20 10:45:00','4567','2025-08-20 11:15:00',27,'Santiago del Estero 2560, Mar del Plata (7600)'),
(28,'DELIVERY','2025-08-22 20:00:00','COMPLETADO',20390.00,12940.00,7450.00,5.5,false,NULL,NULL,NULL,14,10,36,'2025-08-22 20:15:00','8901','2025-08-22 20:50:00',28,'Catamarca 1890, Piso 1 E, Mar del Plata (7600)'),
(29,'RETIRO_POR_LOCAL','2025-08-25 19:00:00','COMPLETADO',13200.00,13200.00,NULL,NULL,false,NULL,NULL,NULL,15,11,NULL,NULL,NULL,'2025-08-25 19:20:00',NULL,NULL),
(30,'DELIVERY','2025-08-28 12:30:00','COMPLETADO',10200.00,5200.00,5000.00,2.5,false,NULL,NULL,NULL,16,12,37,'2025-08-28 12:45:00','2345','2025-08-28 13:10:00',30,'Entre Rios 2670, Mar del Plata (7600)'),

-- SEPTIEMBRE 2025
(31,'DELIVERY','2025-09-01 13:00:00','COMPLETADO',38000.00,28000.00,10000.00,6.0,false,NULL,NULL,NULL,2,13,38,'2025-09-01 13:15:00','6789','2025-09-01 13:50:00',16,'San Martin 2150, Mar del Plata (7600)'),
(32,'RETIRO_POR_LOCAL','2025-09-03 20:30:00','COMPLETADO',12999.00,12999.00,NULL,NULL,false,NULL,NULL,NULL,3,14,NULL,NULL,NULL,'2025-09-03 20:50:00',NULL,NULL),
(33,'DELIVERY','2025-09-05 19:00:00','COMPLETADO',57500.00,52500.00,5000.00,2.5,false,NULL,NULL,NULL,4,15,39,'2025-09-05 19:15:00','0123','2025-09-05 19:45:00',18,'Guemes 1850, Mar del Plata (7600)'),
(34,'DELIVERY','2025-09-08 12:15:00','COMPLETADO',25200.00,19200.00,6000.00,4.0,false,NULL,NULL,NULL,5,1,40,'2025-09-08 12:30:00','4567','2025-09-08 13:00:00',19,'Brown 2780, Piso 5 A, Mar del Plata (7600)'),
(35,'RETIRO_POR_LOCAL','2025-09-10 13:30:00','COMPLETADO',20500.00,20500.00,NULL,NULL,false,NULL,NULL,NULL,6,2,NULL,NULL,NULL,'2025-09-10 13:55:00',NULL,NULL),
(36,'DELIVERY','2025-09-12 20:45:00','COMPLETADO',33400.00,25900.00,7500.00,5.5,false,NULL,NULL,NULL,7,3,41,'2025-09-12 21:00:00','8901','2025-09-12 21:35:00',21,'Moreno 2340, Mar del Plata (7600)'),
(37,'DELIVERY','2025-09-15 19:15:00','COMPLETADO',30060.00,23560.00,6500.00,4.5,false,NULL,NULL,NULL,8,4,42,'2025-09-15 19:30:00','2345','2025-09-15 20:05:00',22,'Colon 2890, Piso 8 C, Mar del Plata (7600)'),
(38,'RETIRO_POR_LOCAL','2025-09-18 21:30:00','COMPLETADO',32700.00,32700.00,NULL,NULL,false,NULL,NULL,NULL,9,5,NULL,NULL,NULL,'2025-09-18 21:55:00',NULL,NULL),
(39,'DELIVERY','2025-09-20 20:00:00','COMPLETADO',30700.00,21700.00,9000.00,5.5,false,NULL,NULL,NULL,10,6,43,'2025-09-20 20:15:00','6789','2025-09-20 20:50:00',24,'San Juan 3100, Mar del Plata (7600)'),
(40,'DELIVERY','2025-09-22 19:30:00','COMPLETADO',29700.00,24700.00,5000.00,2.5,false,NULL,NULL,NULL,11,7,44,'2025-09-22 19:45:00','0123','2025-09-22 20:15:00',25,'Alem 2450, Piso 3 D, Mar del Plata (7600)'),

-- OCTUBRE 2025
(41,'RETIRO_POR_LOCAL','2025-10-01 12:00:00','COMPLETADO',9500.00,9500.00,NULL,NULL,false,NULL,NULL,NULL,12,8,NULL,NULL,NULL,'2025-10-01 12:20:00',NULL,NULL),
(42,'DELIVERY','2025-10-03 10:00:00','COMPLETADO',21000.00,16000.00,5000.00,2.8,false,NULL,NULL,NULL,13,9,45,'2025-10-03 10:15:00','4567','2025-10-03 10:45:00',27,'Santiago del Estero 2560, Mar del Plata (7600)'),
(43,'DELIVERY','2025-10-05 20:15:00','COMPLETADO',18889.00,12940.00,5949.00,3.8,false,NULL,NULL,NULL,14,10,46,'2025-10-05 20:30:00','8901','2025-10-05 21:00:00',28,'Catamarca 1890, Piso 1 E, Mar del Plata (7600)'),
(44,'RETIRO_POR_LOCAL','2025-10-08 19:00:00','COMPLETADO',21999.00,21999.00,NULL,NULL,false,NULL,NULL,NULL,15,11,NULL,NULL,NULL,'2025-10-08 19:25:00',NULL,NULL),
(45,'DELIVERY','2025-10-10 12:30:00','COMPLETADO',10200.00,5200.00,5000.00,2.5,false,NULL,NULL,NULL,16,12,32,'2025-10-10 12:45:00','2345','2025-10-10 13:10:00',30,'Entre Rios 2670, Mar del Plata (7600)'),
(46,'DELIVERY','2025-10-12 13:00:00','COMPLETADO',33000.00,23200.00,9800.00,6.2,false,NULL,NULL,NULL,2,13,33,'2025-10-12 13:15:00','6789','2025-10-12 13:55:00',16,'San Martin 2150, Mar del Plata (7600)'),
(47,'RETIRO_POR_LOCAL','2025-10-15 20:00:00','COMPLETADO',14999.00,14999.00,NULL,NULL,false,NULL,NULL,NULL,3,14,NULL,NULL,NULL,'2025-10-15 20:20:00',NULL,NULL),
(48,'DELIVERY','2025-10-18 19:30:00','COMPLETADO',50800.00,45800.00,5000.00,2.5,false,NULL,NULL,NULL,4,15,34,'2025-10-18 19:45:00','0123','2025-10-18 20:15:00',18,'Guemes 1850, Mar del Plata (7600)'),
(49,'DELIVERY','2025-10-20 12:00:00','COMPLETADO',24200.00,19200.00,5000.00,2.5,false,NULL,NULL,NULL,5,1,35,'2025-10-20 12:15:00','4567','2025-10-20 12:45:00',19,'Brown 2780, Piso 5 A, Mar del Plata (7600)'),
(50,'RETIRO_POR_LOCAL','2025-10-22 13:15:00','COMPLETADO',20900.00,20900.00,NULL,NULL,false,NULL,NULL,NULL,6,2,NULL,NULL,NULL,'2025-10-22 13:40:00',NULL,NULL),

-- NOVIEMBRE 2025
(51,'DELIVERY','2025-11-01 20:00:00','COMPLETADO',30900.00,25900.00,5000.00,2.2,false,NULL,NULL,NULL,7,3,36,'2025-11-01 20:15:00','8901','2025-11-01 20:45:00',21,'Moreno 2340, Mar del Plata (7600)'),
(52,'DELIVERY','2025-11-03 19:30:00','COMPLETADO',30060.00,23560.00,6500.00,4.5,false,NULL,NULL,NULL,8,4,37,'2025-11-03 19:45:00','2345','2025-11-03 20:20:00',22,'Colon 2890, Piso 8 C, Mar del Plata (7600)'),
(53,'RETIRO_POR_LOCAL','2025-11-05 21:00:00','COMPLETADO',30000.00,30000.00,NULL,NULL,false,NULL,NULL,NULL,9,5,NULL,NULL,NULL,'2025-11-05 21:25:00',NULL,NULL),
(54,'DELIVERY','2025-11-08 20:15:00','COMPLETADO',35700.00,25100.00,10600.00,6.5,false,NULL,NULL,NULL,10,6,38,'2025-11-08 20:30:00','6789','2025-11-08 21:10:00',24,'San Juan 3100, Mar del Plata (7600)'),
(55,'DELIVERY','2025-11-10 19:45:00','COMPLETADO',29000.00,22000.00,7000.00,5.0,false,NULL,NULL,NULL,11,7,39,'2025-11-10 20:00:00','0123','2025-11-10 20:35:00',25,'Alem 2450, Piso 3 D, Mar del Plata (7600)'),
(56,'RETIRO_POR_LOCAL','2025-11-12 12:00:00','COMPLETADO',7000.00,7000.00,NULL,NULL,false,NULL,NULL,NULL,12,8,NULL,NULL,NULL,'2025-11-12 12:15:00',NULL,NULL),
(57,'DELIVERY','2025-11-15 10:30:00','COMPLETADO',18000.00,13000.00,5000.00,2.8,false,NULL,NULL,NULL,13,9,40,'2025-11-15 10:45:00','4567','2025-11-15 11:15:00',27,'Santiago del Estero 2560, Mar del Plata (7600)'),
(58,'DELIVERY','2025-11-18 20:00:00','COMPLETADO',19389.00,12940.00,6449.00,4.3,false,NULL,NULL,NULL,14,10,41,'2025-11-18 20:15:00','8901','2025-11-18 20:50:00',28,'Catamarca 1890, Piso 1 E, Mar del Plata (7600)'),
(59,'RETIRO_POR_LOCAL','2025-11-20 19:00:00','COMPLETADO',18800.00,18800.00,NULL,NULL,false,NULL,NULL,NULL,15,11,NULL,NULL,NULL,'2025-11-20 19:25:00',NULL,NULL),
(60,'DELIVERY','2025-11-22 12:30:00','COMPLETADO',12800.00,7800.00,5000.00,2.5,false,NULL,NULL,NULL,16,12,42,'2025-11-22 12:45:00','2345','2025-11-22 13:10:00',30,'Entre Rios 2670, Mar del Plata (7600)'),
(61,'DELIVERY','2025-11-25 13:00:00','COMPLETADO',38000.00,28000.00,10000.00,6.0,false,NULL,NULL,NULL,2,13,43,'2025-11-25 13:15:00','6789','2025-11-25 13:50:00',16,'San Martin 2150, Mar del Plata (7600)'),
(62,'RETIRO_POR_LOCAL','2025-11-27 20:30:00','COMPLETADO',14284.50,14284.50,NULL,NULL,false,NULL,NULL,NULL,3,14,NULL,NULL,NULL,'2025-11-27 20:50:00',NULL,NULL),
(63,'DELIVERY','2025-11-29 19:00:00','COMPLETADO',57500.00,52500.00,5000.00,2.5,false,NULL,NULL,NULL,4,15,44,'2025-11-29 19:15:00','0123','2025-11-29 19:45:00',18,'Guemes 1850, Mar del Plata (7600)'),

-- DICIEMBRE 2025 - Pedidos variados (completados, en progreso, cancelados)
(64,'DELIVERY','2025-12-01 12:15:00','COMPLETADO',24700.00,19700.00,5000.00,2.5,false,NULL,NULL,NULL,5,1,45,'2025-12-01 12:30:00','4567','2025-12-01 13:00:00',19,'Brown 2780, Piso 5 A, Mar del Plata (7600)'),
(65,'RETIRO_POR_LOCAL','2025-12-02 13:30:00','COMPLETADO',16900.00,16900.00,NULL,NULL,false,NULL,NULL,NULL,6,2,NULL,NULL,NULL,'2025-12-02 13:55:00',NULL,NULL),
(66,'DELIVERY','2025-12-03 20:45:00','COMPLETADO',30900.00,25900.00,5000.00,2.2,false,NULL,NULL,NULL,7,3,46,'2025-12-03 21:00:00','8901','2025-12-03 21:35:00',21,'Moreno 2340, Mar del Plata (7600)'),
(67,'DELIVERY','2025-12-05 19:15:00','COMPLETADO',28060.00,23560.00,4500.00,3.0,false,NULL,NULL,NULL,8,4,32,'2025-12-05 19:30:00','2345','2025-12-05 20:05:00',22,'Colon 2890, Piso 8 C, Mar del Plata (7600)'),
(68,'RETIRO_POR_LOCAL','2025-12-06 21:30:00','COMPLETADO',26800.00,26800.00,NULL,NULL,false,NULL,NULL,NULL,9,5,NULL,NULL,NULL,'2025-12-06 21:55:00',NULL,NULL),
(69,'DELIVERY','2025-12-07 20:00:00','COMPLETADO',34000.00,25100.00,8900.00,5.8,false,NULL,NULL,NULL,10,6,33,'2025-12-07 20:15:00','6789','2025-12-07 20:55:00',24,'San Juan 3100, Mar del Plata (7600)'),
(70,'DELIVERY','2025-12-08 19:30:00','COMPLETADO',28500.00,22500.00,6000.00,4.0,false,NULL,NULL,NULL,11,7,34,'2025-12-08 19:45:00','0123','2025-12-08 20:20:00',25,'Alem 2450, Piso 3 D, Mar del Plata (7600)'),

-- Pedidos cancelados (para mostrar variedad en estadisticas)
(71,'DELIVERY','2025-07-12 19:00:00','CANCELADO',24200.00,19200.00,5000.00,2.5,true,'Cliente solicito cancelacion','2025-07-12 19:10:00','EN_PREPARACION',5,1,NULL,NULL,NULL,NULL,19,NULL),
(72,'RETIRO_POR_LOCAL','2025-08-20 20:00:00','CANCELADO',25900.00,25900.00,NULL,NULL,true,'Restaurante cerrado por emergencia','2025-08-20 20:05:00','PENDIENTE',8,3,NULL,NULL,NULL,NULL,NULL,NULL),
(73,'DELIVERY','2025-09-15 12:00:00','CANCELADO',32520.00,32520.00,6500.00,4.5,true,'Tiempo de espera excedido','2025-09-15 12:45:00','LISTO_PARA_ENTREGAR',10,4,NULL,NULL,NULL,NULL,24,NULL),
(74,'DELIVERY','2025-10-28 19:30:00','CANCELADO',21700.00,21700.00,8000.00,5.5,true,'Cliente no disponible para recibir','2025-10-28 20:15:00','EN_CAMINO',12,6,35,NULL,'1234',NULL,26,NULL),
(75,'RETIRO_POR_LOCAL','2025-11-10 13:00:00','CANCELADO',18000.00,18000.00,NULL,NULL,true,'Productos no disponibles','2025-11-10 13:05:00','PENDIENTE',14,5,NULL,NULL,NULL,NULL,NULL,NULL),

-- Pedidos recientes en diferentes estados (para mostrar flujo actual)
(76,'DELIVERY','2025-12-10 12:30:00','COMPLETADO',19700.00,15600.00,4100.00,2.7,false,NULL,NULL,NULL,2,1,36,'2025-12-10 12:45:00','5678','2025-12-10 13:15:00',16,'San Martin 2150, Mar del Plata (7600)'),
(77,'DELIVERY','2025-12-10 19:00:00','COMPLETADO',25900.00,20900.00,5000.00,2.5,false,NULL,NULL,NULL,3,2,37,'2025-12-10 19:15:00','9012','2025-12-10 19:45:00',17,'Luro 3200, Piso 2 B, Mar del Plata (7600)'),
(78,'RETIRO_POR_LOCAL','2025-12-11 12:00:00','COMPLETADO',23700.00,23700.00,NULL,NULL,false,NULL,NULL,NULL,4,3,NULL,NULL,NULL,'2025-12-11 12:25:00',NULL,NULL),
(79,'DELIVERY','2025-12-11 20:00:00','COMPLETADO',30060.00,23560.00,6500.00,4.5,false,NULL,NULL,NULL,5,4,38,'2025-12-11 20:15:00','3456','2025-12-11 20:50:00',19,'Brown 2780, Piso 5 A, Mar del Plata (7600)'),
(80,'DELIVERY','2025-12-12 12:00:00','EN_CAMINO',35000.00,30000.00,5000.00,2.5,false,NULL,NULL,NULL,6,5,39,'2025-12-12 12:15:00','7890',NULL,20,'Mitre 1920, Mar del Plata (7600)'),
(81,'DELIVERY','2025-12-12 12:30:00','ASIGNADO_A_REPARTIDOR',31700.00,21700.00,10000.00,6.0,false,NULL,NULL,NULL,7,6,40,'2025-12-12 12:45:00','2345',NULL,21,'Moreno 2340, Mar del Plata (7600)'),
(82,'DELIVERY','2025-12-12 12:45:00','LISTO_PARA_ENTREGAR',24200.00,19200.00,5000.00,2.8,false,NULL,NULL,NULL,8,1,NULL,NULL,NULL,NULL,22,'Colon 2890, Piso 8 C, Mar del Plata (7600)'),
(83,'RETIRO_POR_LOCAL','2025-12-12 13:00:00','EN_PREPARACION',25900.00,25900.00,NULL,NULL,false,NULL,NULL,NULL,9,3,NULL,NULL,NULL,NULL,NULL,NULL),
(84,'DELIVERY','2025-12-12 13:15:00','EN_PREPARACION',28560.00,23560.00,5000.00,2.5,false,NULL,NULL,NULL,10,4,NULL,NULL,NULL,NULL,24,'San Juan 3100, Mar del Plata (7600)'),
(85,'RETIRO_POR_LOCAL','2025-12-12 13:30:00','PENDIENTE',18000.00,18000.00,NULL,NULL,false,NULL,NULL,NULL,11,5,NULL,NULL,NULL,NULL,NULL,NULL);

-- 12) Productos de cada pedido (productos_pedidos)
-- formato: (id, cantidad, precio_unitario, producto_id, pedido_id)
INSERT INTO `productos_pedidos` (id, cantidad, precio_unitario, producto_id, pedido_id) VALUES
-- Pedido 1 - McDonalds
(1,1,19200.00,1,1),
-- Pedido 2 - McDonalds
(2,1,20900.00,2,2),
-- Pedido 3 - El Club de la Milanesa
(3,1,25900.00,13,3),
-- Pedido 4 - Estilo Sushi
(4,1,23560.00,17,4),
-- Pedido 5 - Hells Pizza
(5,1,21700.00,32,5),
-- Pedido 6 - Lebron
(6,1,18000.00,24,6),
-- Pedido 7 - Burger King
(7,1,15831.00,38,7),
-- Pedido 8 - McDonalds
(8,1,15600.00,3,8),
-- Pedido 9 - Mostaza
(9,1,20500.00,7,9),
-- Pedido 10 - El Club de la Milanesa
(10,1,25900.00,13,10),
(11,1,21500.00,14,10),
-- Pedido 11 - Estilo Sushi
(12,1,32520.00,19,11),
-- Pedido 12 - Lebron
(13,1,30000.00,25,12),
-- Pedido 13 - Hells Pizza
(14,1,25100.00,33,13),
-- Pedido 14 - Burger King
(15,1,24700.00,39,14),
-- Pedido 15 - La Reina del Parque
(16,1,9500.00,48,15),
-- Pedido 16 - Coco Cafe
(17,1,8000.00,55,16),
-- Pedido 17 - Che Sushi
(18,1,12940.00,61,17),
-- Pedido 18 - Dean & Dennys
(19,1,18800.00,66,18),
-- Pedido 19 - McDonalds
(20,1,19200.00,1,19),
-- Pedido 20 - Mostaza
(21,1,20900.00,8,20),
-- Pedido 21 - El Club de la Milanesa
(22,1,25900.00,15,21),
-- Pedido 22 - Estilo Sushi
(23,1,23560.00,17,22),
-- Pedido 23 - Lebron
(24,1,18000.00,24,23),
-- Pedido 24 - Hells Pizza
(25,1,43200.00,34,24),
-- Pedido 25 - Burger King
(26,1,21000.00,40,25),
-- Pedido 26 - La Reina del Parque
(27,1,3400.00,50,26),
-- Pedido 27 - Coco Cafe
(28,1,8000.00,55,27),
-- Pedido 28 - Che Sushi
(29,1,12940.00,62,28),
-- Pedido 29 - Dean & Dennys
(30,1,13200.00,67,29),
-- Pedido 30 - Chiqui Empanadas
(31,2,2600.00,70,30),
-- Pedido 31 - Sao Medialunas
(32,1,28000.00,75,31),
-- Pedido 32 - Poke Pop
(33,1,12999.00,87,32),
-- Pedido 33 - Los Toldos Viejos
(34,1,52500.00,90,33),
-- Pedido 34 - McDonalds
(35,1,19200.00,1,34),
-- Pedido 35 - Mostaza
(36,1,20500.00,9,35),
-- Pedido 36 - El Club de la Milanesa
(37,1,25900.00,16,36),
-- Pedido 37 - Estilo Sushi
(38,1,23560.00,17,37),
-- Pedido 38 - Lebron
(39,1,14800.00,26,38),
(40,1,17900.00,27,38),
-- Pedido 39 - Hells Pizza
(41,1,21700.00,32,39),
-- Pedido 40 - Burger King
(42,1,24700.00,39,40),
-- Pedido 41 - La Reina del Parque
(43,1,9500.00,48,41),
-- Pedido 42 - Coco Cafe
(44,1,16000.00,57,42),
-- Pedido 43 - Che Sushi
(45,1,12940.00,61,43),
-- Pedido 44 - Dean & Dennys
(46,1,21999.00,69,44),
-- Pedido 45 - Chiqui Empanadas
(47,2,2600.00,71,45),
-- Pedido 46 - Sao Medialunas
(48,1,23200.00,84,46),
-- Pedido 47 - Poke Pop
(49,1,14999.00,88,47),
-- Pedido 48 - Los Toldos Viejos
(50,1,45800.00,91,48),
-- Pedido 49 - McDonalds
(51,1,19200.00,1,49),
-- Pedido 50 - Mostaza
(52,1,20900.00,8,50),
-- Pedido 51 - El Club de la Milanesa
(53,1,25900.00,13,51),
-- Pedido 52 - Estilo Sushi
(54,1,23560.00,17,52),
-- Pedido 53 - Lebron
(55,1,30000.00,25,53),
-- Pedido 54 - Hells Pizza
(56,1,25100.00,33,54),
-- Pedido 55 - Burger King
(57,1,22000.00,42,55),
-- Pedido 56 - La Reina del Parque
(58,2,3500.00,52,56),
-- Pedido 57 - Coco Cafe
(59,1,13000.00,60,57),
-- Pedido 58 - Che Sushi
(60,1,12940.00,62,58),
-- Pedido 59 - Dean & Dennys
(61,1,18800.00,66,59),
-- Pedido 60 - Chiqui Empanadas
(62,3,2600.00,72,60),
-- Pedido 61 - Sao Medialunas
(63,1,28000.00,75,61),
-- Pedido 62 - Poke Pop
(64,1,14284.50,89,62),
-- Pedido 63 - Los Toldos Viejos
(65,1,52500.00,90,63),
-- Pedido 64 - McDonalds
(66,1,19700.00,5,64),
-- Pedido 65 - Mostaza
(67,1,16900.00,11,65),
-- Pedido 66 - El Club de la Milanesa
(68,1,25900.00,15,66),
-- Pedido 67 - Estilo Sushi
(69,1,23560.00,17,67),
-- Pedido 68 - Lebron
(70,1,14800.00,26,68),
(71,1,12000.00,28,68),
-- Pedido 69 - Hells Pizza
(72,1,25100.00,33,69),
-- Pedido 70 - Burger King
(73,1,22500.00,46,70),
-- Pedidos cancelados (71-75)
(74,1,19200.00,1,71),
(75,1,25900.00,13,72),
(76,1,32520.00,19,73),
(77,1,21700.00,32,74),
(78,1,18000.00,24,75),
-- Pedido 76 - McDonalds
(79,1,15600.00,3,76),
-- Pedido 77 - Mostaza
(80,1,20900.00,8,77),
-- Pedido 78 - El Club de la Milanesa
(81,1,23700.00,14,78),
-- Pedido 79 - Estilo Sushi
(82,1,23560.00,17,79),
-- Pedido 80 - Lebron
(83,1,30000.00,25,80),
-- Pedido 81 - Hells Pizza
(84,1,21700.00,32,81),
-- Pedido 82 - McDonalds
(85,1,19200.00,1,82),
-- Pedido 83 - El Club de la Milanesa
(86,1,25900.00,13,83),
-- Pedido 84 - Estilo Sushi
(87,1,23560.00,17,84),
-- Pedido 85 - Lebron
(88,1,18000.00,24,85);

-- 13) Pagos para cada pedido
-- formato: (id, pedido_id, metodo_pago, monto, estado, mercadopago_payment_id, mercadopago_preference_id, mercadopago_status, mercadopago_status_detail, mercadopago_payment_type, fecha_creacion, fecha_actualizacion)
INSERT INTO `pagos` (id, pedido_id, metodo_pago, monto, estado, mercadopago_payment_id, mercadopago_preference_id, mercadopago_status, mercadopago_status_detail, mercadopago_payment_type, fecha_creacion, fecha_actualizacion) VALUES
-- Junio 2025
(1,1,'MERCADO_PAGO',24200.00,'APROBADO','MP001','PREF001','approved','accredited','credit_card','2025-06-15 12:30:00','2025-06-15 12:31:00'),
(2,2,'EFECTIVO',27400.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-06-16 19:45:00','2025-06-16 20:35:00'),
(3,3,'MERCADO_PAGO',25900.00,'APROBADO','MP002','PREF002','approved','accredited','debit_card','2025-06-18 13:00:00','2025-06-18 13:01:00'),
(4,4,'EFECTIVO',28560.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-06-20 20:15:00','2025-06-20 20:20:00'),
(5,5,'MERCADO_PAGO',35700.00,'APROBADO','MP003','PREF003','approved','accredited','credit_card','2025-06-22 21:00:00','2025-06-22 21:02:00'),
(6,6,'EFECTIVO',18000.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-06-25 12:00:00','2025-06-25 12:25:00'),
(7,7,'MERCADO_PAGO',22500.00,'APROBADO','MP004','PREF004','approved','accredited','account_money','2025-06-28 19:30:00','2025-06-28 19:31:00'),
-- Julio 2025
(8,8,'EFECTIVO',20600.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-07-02 13:15:00','2025-07-02 14:00:00'),
(9,9,'MERCADO_PAGO',30500.00,'APROBADO','MP005','PREF005','approved','accredited','credit_card','2025-07-05 20:00:00','2025-07-05 20:01:00'),
(10,10,'EFECTIVO',47400.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-07-08 12:30:00','2025-07-08 12:35:00'),
(11,11,'MERCADO_PAGO',37520.00,'APROBADO','MP006','PREF006','approved','accredited','debit_card','2025-07-10 19:00:00','2025-07-10 19:02:00'),
(12,12,'EFECTIVO',37000.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-07-12 21:30:00','2025-07-12 22:20:00'),
(13,13,'MERCADO_PAGO',25100.00,'APROBADO','MP007','PREF007','approved','accredited','credit_card','2025-07-15 13:00:00','2025-07-15 13:01:00'),
(14,14,'EFECTIVO',29700.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-07-18 20:30:00','2025-07-18 20:35:00'),
(15,15,'MERCADO_PAGO',14300.00,'APROBADO','MP008','PREF008','approved','accredited','account_money','2025-07-20 19:15:00','2025-07-20 19:16:00'),
(16,16,'EFECTIVO',8000.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-07-22 10:00:00','2025-07-22 10:20:00'),
(17,17,'MERCADO_PAGO',19440.00,'APROBADO','MP009','PREF009','approved','accredited','credit_card','2025-07-25 20:45:00','2025-07-25 20:46:00'),
(18,18,'EFECTIVO',23800.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-07-28 19:00:00','2025-07-28 19:45:00'),
-- Agosto 2025
(19,19,'MERCADO_PAGO',24200.00,'APROBADO','MP010','PREF010','approved','accredited','debit_card','2025-08-01 12:00:00','2025-08-01 12:01:00'),
(20,20,'EFECTIVO',20900.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-08-03 13:30:00','2025-08-03 13:35:00'),
(21,21,'MERCADO_PAGO',30900.00,'APROBADO','MP011','PREF011','approved','accredited','credit_card','2025-08-05 20:00:00','2025-08-05 20:02:00'),
(22,22,'EFECTIVO',30060.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-08-08 19:30:00','2025-08-08 20:20:00'),
(23,23,'MERCADO_PAGO',18000.00,'APROBADO','MP012','PREF012','approved','accredited','account_money','2025-08-10 21:00:00','2025-08-10 21:01:00'),
(24,24,'EFECTIVO',32600.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-08-12 20:15:00','2025-08-12 20:20:00'),
(25,25,'MERCADO_PAGO',28000.00,'APROBADO','MP013','PREF013','approved','accredited','credit_card','2025-08-15 19:45:00','2025-08-15 19:46:00'),
(26,26,'EFECTIVO',3400.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-08-18 12:00:00','2025-08-18 12:15:00'),
(27,27,'MERCADO_PAGO',13000.00,'APROBADO','MP014','PREF014','approved','accredited','debit_card','2025-08-20 10:30:00','2025-08-20 10:31:00'),
(28,28,'EFECTIVO',20390.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-08-22 20:00:00','2025-08-22 20:50:00'),
(29,29,'EFECTIVO',13200.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-08-25 19:00:00','2025-08-25 19:05:00'),
(30,30,'MERCADO_PAGO',10200.00,'APROBADO','MP015','PREF015','approved','accredited','credit_card','2025-08-28 12:30:00','2025-08-28 12:31:00'),
-- Septiembre 2025
(31,31,'EFECTIVO',38000.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-09-01 13:00:00','2025-09-01 13:50:00'),
(32,32,'MERCADO_PAGO',12999.00,'APROBADO','MP016','PREF016','approved','accredited','account_money','2025-09-03 20:30:00','2025-09-03 20:31:00'),
(33,33,'EFECTIVO',57500.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-09-05 19:00:00','2025-09-05 19:10:00'),
(34,34,'MERCADO_PAGO',25200.00,'APROBADO','MP017','PREF017','approved','accredited','credit_card','2025-09-08 12:15:00','2025-09-08 12:16:00'),
(35,35,'EFECTIVO',20500.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-09-10 13:30:00','2025-09-10 13:55:00'),
(36,36,'MERCADO_PAGO',33400.00,'APROBADO','MP018','PREF018','approved','accredited','debit_card','2025-09-12 20:45:00','2025-09-12 20:46:00'),
(37,37,'EFECTIVO',30060.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-09-15 19:15:00','2025-09-15 20:05:00'),
(38,38,'EFECTIVO',32700.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-09-18 21:30:00','2025-09-18 21:35:00'),
(39,39,'MERCADO_PAGO',30700.00,'APROBADO','MP019','PREF019','approved','accredited','credit_card','2025-09-20 20:00:00','2025-09-20 20:01:00'),
(40,40,'EFECTIVO',29700.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-09-22 19:30:00','2025-09-22 20:15:00'),
-- Octubre 2025
(41,41,'MERCADO_PAGO',9500.00,'APROBADO','MP020','PREF020','approved','accredited','account_money','2025-10-01 12:00:00','2025-10-01 12:01:00'),
(42,42,'EFECTIVO',21000.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-10-03 10:00:00','2025-10-03 10:45:00'),
(43,43,'MERCADO_PAGO',18889.00,'APROBADO','MP021','PREF021','approved','accredited','credit_card','2025-10-05 20:15:00','2025-10-05 20:16:00'),
(44,44,'EFECTIVO',21999.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-10-08 19:00:00','2025-10-08 19:05:00'),
(45,45,'MERCADO_PAGO',10200.00,'APROBADO','MP022','PREF022','approved','accredited','debit_card','2025-10-10 12:30:00','2025-10-10 12:31:00'),
(46,46,'EFECTIVO',33000.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-10-12 13:00:00','2025-10-12 13:55:00'),
(47,47,'MERCADO_PAGO',14999.00,'APROBADO','MP023','PREF023','approved','accredited','credit_card','2025-10-15 20:00:00','2025-10-15 20:01:00'),
(48,48,'EFECTIVO',50800.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-10-18 19:30:00','2025-10-18 19:35:00'),
(49,49,'MERCADO_PAGO',24200.00,'APROBADO','MP024','PREF024','approved','accredited','account_money','2025-10-20 12:00:00','2025-10-20 12:01:00'),
(50,50,'EFECTIVO',20900.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-10-22 13:15:00','2025-10-22 13:40:00'),
-- Noviembre 2025
(51,51,'MERCADO_PAGO',30900.00,'APROBADO','MP025','PREF025','approved','accredited','credit_card','2025-11-01 20:00:00','2025-11-01 20:01:00'),
(52,52,'EFECTIVO',30060.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-11-03 19:30:00','2025-11-03 20:20:00'),
(53,53,'EFECTIVO',30000.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-11-05 21:00:00','2025-11-05 21:05:00'),
(54,54,'MERCADO_PAGO',35700.00,'APROBADO','MP026','PREF026','approved','accredited','debit_card','2025-11-08 20:15:00','2025-11-08 20:16:00'),
(55,55,'EFECTIVO',29000.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-11-10 19:45:00','2025-11-10 20:35:00'),
(56,56,'MERCADO_PAGO',7000.00,'APROBADO','MP027','PREF027','approved','accredited','account_money','2025-11-12 12:00:00','2025-11-12 12:01:00'),
(57,57,'EFECTIVO',18000.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-11-15 10:30:00','2025-11-15 11:15:00'),
(58,58,'MERCADO_PAGO',19389.00,'APROBADO','MP028','PREF028','approved','accredited','credit_card','2025-11-18 20:00:00','2025-11-18 20:01:00'),
(59,59,'EFECTIVO',18800.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-11-20 19:00:00','2025-11-20 19:05:00'),
(60,60,'MERCADO_PAGO',12800.00,'APROBADO','MP029','PREF029','approved','accredited','debit_card','2025-11-22 12:30:00','2025-11-22 12:31:00'),
(61,61,'EFECTIVO',38000.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-11-25 13:00:00','2025-11-25 13:50:00'),
(62,62,'MERCADO_PAGO',14284.50,'APROBADO','MP030','PREF030','approved','accredited','credit_card','2025-11-27 20:30:00','2025-11-27 20:31:00'),
(63,63,'EFECTIVO',57500.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-11-29 19:00:00','2025-11-29 19:05:00'),
-- Diciembre 2025
(64,64,'MERCADO_PAGO',24700.00,'APROBADO','MP031','PREF031','approved','accredited','credit_card','2025-12-01 12:15:00','2025-12-01 12:16:00'),
(65,65,'EFECTIVO',16900.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-12-02 13:30:00','2025-12-02 13:55:00'),
(66,66,'MERCADO_PAGO',30900.00,'APROBADO','MP032','PREF032','approved','accredited','debit_card','2025-12-03 20:45:00','2025-12-03 20:46:00'),
(67,67,'EFECTIVO',28060.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-12-05 19:15:00','2025-12-05 20:05:00'),
(68,68,'EFECTIVO',26800.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-12-06 21:30:00','2025-12-06 21:35:00'),
(69,69,'MERCADO_PAGO',34000.00,'APROBADO','MP033','PREF033','approved','accredited','credit_card','2025-12-07 20:00:00','2025-12-07 20:01:00'),
(70,70,'EFECTIVO',28500.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-12-08 19:30:00','2025-12-08 20:20:00'),
-- Pagos cancelados/reembolsados
(71,71,'MERCADO_PAGO',24200.00,'REEMBOLSADO','MP034','PREF034','refunded','refunded','credit_card','2025-07-12 19:00:00','2025-07-12 19:15:00'),
(72,72,'EFECTIVO',25900.00,'CANCELADO',NULL,NULL,NULL,NULL,NULL,'2025-08-20 20:00:00','2025-08-20 20:05:00'),
(73,73,'MERCADO_PAGO',32520.00,'REEMBOLSADO','MP035','PREF035','refunded','refunded','debit_card','2025-09-15 12:00:00','2025-09-15 12:50:00'),
(74,74,'EFECTIVO',21700.00,'CANCELADO',NULL,NULL,NULL,NULL,NULL,'2025-10-28 19:30:00','2025-10-28 20:15:00'),
(75,75,'EFECTIVO',18000.00,'CANCELADO',NULL,NULL,NULL,NULL,NULL,'2025-11-10 13:00:00','2025-11-10 13:05:00'),
-- Pagos recientes
(76,76,'MERCADO_PAGO',19700.00,'APROBADO','MP036','PREF036','approved','accredited','credit_card','2025-12-10 12:30:00','2025-12-10 12:31:00'),
(77,77,'EFECTIVO',25900.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-12-10 19:00:00','2025-12-10 19:45:00'),
(78,78,'MERCADO_PAGO',23700.00,'APROBADO','MP037','PREF037','approved','accredited','debit_card','2025-12-11 12:00:00','2025-12-11 12:01:00'),
(79,79,'EFECTIVO',30060.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-12-11 20:00:00','2025-12-11 20:05:00'),
(80,80,'MERCADO_PAGO',35000.00,'APROBADO','MP038','PREF038','approved','accredited','credit_card','2025-12-12 12:00:00','2025-12-12 12:01:00'),
(81,81,'EFECTIVO',31700.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-12-12 12:30:00',NULL),
(82,82,'MERCADO_PAGO',24200.00,'APROBADO','MP039','PREF039','approved','accredited','account_money','2025-12-12 12:45:00','2025-12-12 12:46:00'),
(83,83,'EFECTIVO',25900.00,'APROBADO',NULL,NULL,NULL,NULL,NULL,'2025-12-12 13:00:00','2025-12-12 13:05:00'),
(84,84,'MERCADO_PAGO',28560.00,'APROBADO','MP040','PREF040','approved','accredited','credit_card','2025-12-12 13:15:00','2025-12-12 13:16:00'),
(85,85,'EFECTIVO',18000.00,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,'2025-12-12 13:30:00',NULL);

-- 14) Calificaciones de pedidos (solo para pedidos COMPLETADOS)
-- formato: (id, puntaje_comida, puntaje_tiempo, puntaje_packaging, comentario, fecha_hora, pedido_id, usuario_id)
INSERT INTO `calificaciones_pedido` (id, puntaje_comida, puntaje_tiempo, puntaje_packaging, comentario, fecha_hora, pedido_id, usuario_id) VALUES
-- Junio 2025
(1,5,5,5,'Excelente hamburguesa, llegó caliente y bien empaquetada','2025-06-15 14:00:00',1,2),
(2,4,4,5,'Muy rica la comida, el tiempo fue aceptable','2025-06-16 21:00:00',2,3),
(3,5,5,4,'La milanesa estaba espectacular, muy rápido','2025-06-18 14:00:00',3,4),
(4,5,4,5,'Sushi fresco y bien presentado','2025-06-20 21:30:00',4,5),
(5,4,3,4,'Pizza rica pero tardó un poco más de lo esperado','2025-06-22 22:30:00',5,6),
(6,5,5,5,'Perfecto para el almuerzo, muy bueno todo','2025-06-25 13:00:00',6,7),
(7,4,4,4,'Hamburguesa bien lograda, buen servicio','2025-06-28 20:45:00',7,8),
-- Julio 2025
(8,5,5,5,'McDonalds siempre cumple, excelente','2025-07-02 14:30:00',8,9),
(9,4,4,5,'Buena hamburguesa de Mostaza','2025-07-05 21:30:00',9,10),
(10,5,5,4,'Las milanesas son increíbles','2025-07-08 13:30:00',10,11),
(11,5,4,5,'Combinado de sushi excelente','2025-07-10 20:15:00',11,12),
(12,4,4,4,'Buen combo de hamburguesas','2025-07-12 22:45:00',12,13),
(13,5,5,5,'Pizza deliciosa, muy recomendable','2025-07-15 13:50:00',13,14),
(14,4,5,4,'Burger King nunca falla','2025-07-18 21:45:00',14,15),
(15,5,4,5,'Excelente pastelería','2025-07-20 20:30:00',15,16),
(16,5,5,5,'Café y medialunas perfectos','2025-07-22 10:45:00',16,2),
(17,4,4,4,'Poke muy fresco','2025-07-25 22:00:00',17,3),
(18,5,5,5,'Hamburguesa espectacular','2025-07-28 20:15:00',18,4),
-- Agosto 2025
(19,4,5,4,'Muy bueno el combo','2025-08-01 13:15:00',19,5),
(20,5,4,5,'Mostaza muy rico','2025-08-03 14:30:00',20,6),
(21,5,5,5,'La mejor milanesa','2025-08-05 21:15:00',21,7),
(22,4,4,4,'Sushi fresco y sabroso','2025-08-08 20:45:00',22,8),
(23,5,5,5,'Excelente cervecería','2025-08-10 22:00:00',23,9),
(24,4,3,4,'Pizza grande pero tardó','2025-08-12 21:30:00',24,10),
(25,5,4,5,'Muy buena la hamburguesa','2025-08-15 21:00:00',25,11),
(26,5,5,5,'Tarteleta deliciosa','2025-08-18 12:30:00',26,12),
(27,4,4,4,'Desayuno completo y rico','2025-08-20 11:45:00',27,13),
(28,5,4,5,'Poke muy bueno','2025-08-22 21:15:00',28,14),
(29,4,5,4,'Hamburguesa bien','2025-08-25 19:45:00',29,15),
(30,5,5,5,'Empanadas caseras excelentes','2025-08-28 13:30:00',30,16),
-- Septiembre 2025
(31,5,4,5,'Medialunas perfectas','2025-09-01 14:15:00',31,2),
(32,4,5,4,'Poke muy fresco','2025-09-03 21:15:00',32,3),
(33,5,5,5,'Asado espectacular','2025-09-05 20:15:00',33,4),
(34,4,4,4,'Hamburguesa clásica bien','2025-09-08 13:30:00',34,5),
(35,5,5,5,'Mostaza siempre cumple','2025-09-10 14:15:00',35,6),
(36,5,4,5,'Milanesa riquísima','2025-09-12 22:00:00',36,7),
(37,4,4,4,'Buen sushi','2025-09-15 20:30:00',37,8),
(38,5,5,5,'Cerveza y papas excelentes','2025-09-18 22:30:00',38,9),
(39,4,4,5,'Pizza muy buena','2025-09-20 21:15:00',39,10),
(40,5,5,4,'Burger King rico','2025-09-22 20:45:00',40,11),
-- Octubre 2025
(41,5,5,5,'Budín espectacular','2025-10-01 12:45:00',41,12),
(42,4,4,4,'Desayuno completo','2025-10-03 11:15:00',42,13),
(43,5,4,5,'Poke delicioso','2025-10-05 21:30:00',43,14),
(44,4,5,4,'Hamburguesa bien','2025-10-08 19:45:00',44,15),
(45,5,5,5,'Empanadas caseras 10/10','2025-10-10 13:30:00',45,16),
(46,4,4,4,'Medialunas muy buenas','2025-10-12 14:15:00',46,2),
(47,5,5,5,'Sushi fresco','2025-10-15 20:45:00',47,3),
(48,5,4,5,'Carne excelente','2025-10-18 20:45:00',48,4),
(49,4,5,4,'Big Mac siempre','2025-10-20 13:15:00',49,5),
(50,5,5,5,'Mostaza muy rico','2025-10-22 14:00:00',50,6),
-- Noviembre 2025
(51,5,5,4,'Milanesa 10 puntos','2025-11-01 21:15:00',51,7),
(52,4,4,5,'Sushi muy bueno','2025-11-03 20:45:00',52,8),
(53,5,5,5,'Combo perfecto','2025-11-05 22:00:00',53,9),
(54,4,4,4,'Pizza rica','2025-11-08 21:30:00',54,10),
(55,5,4,5,'Burger bien lograda','2025-11-10 21:00:00',55,11),
(56,5,5,5,'Pan fresco','2025-11-12 12:30:00',56,12),
(57,4,5,4,'Café rico','2025-11-15 11:45:00',57,13),
(58,5,4,5,'Sushi excelente','2025-11-18 21:15:00',58,14),
(59,4,4,4,'Hamburguesa bien','2025-11-20 19:45:00',59,15),
(60,5,5,5,'Empanadas caseras','2025-11-22 13:30:00',60,16),
(61,5,4,5,'Medialunas perfectas','2025-11-25 14:15:00',61,2),
(62,4,5,4,'Poke fresco','2025-11-27 21:15:00',62,3),
(63,5,5,5,'Asado espectacular','2025-11-29 20:15:00',63,4),
-- Diciembre 2025
(64,4,4,5,'McNuggets muy buenos','2025-12-01 13:30:00',64,5),
(65,5,5,4,'Mostaza cumple','2025-12-02 14:15:00',65,6),
(66,5,4,5,'Milanesa riquísima','2025-12-03 22:00:00',66,7),
(67,4,5,4,'Sushi fresco','2025-12-05 20:30:00',67,8),
(68,5,5,5,'Cervecería top','2025-12-06 22:15:00',68,9),
(69,4,4,4,'Pizza muy buena','2025-12-07 21:15:00',69,10),
(70,5,5,5,'Whopper excelente','2025-12-08 20:45:00',70,11),
(71,5,4,5,'McDonalds siempre bien','2025-12-10 13:45:00',76,2),
(72,4,5,4,'Mostaza rico','2025-12-10 20:15:00',77,3),
(73,5,5,5,'Milanesa a caballo perfecta','2025-12-11 12:45:00',78,4),
(74,4,4,4,'Combinado muy bueno','2025-12-11 21:15:00',79,5);

-- 15) Calificaciones de repartidores (solo para pedidos DELIVERY COMPLETADOS)
-- formato: (id, puntaje_atencion, puntaje_comunicacion, puntaje_profesionalismo, comentario, fecha_hora, pedido_id, repartidor_id, usuario_id)
INSERT INTO `calificaciones_repartidor` (id, puntaje_atencion, puntaje_comunicacion, puntaje_profesionalismo, comentario, fecha_hora, pedido_id, repartidor_id, usuario_id) VALUES
-- Junio 2025
(1,5,5,5,'Muy amable y puntual','2025-06-15 14:00:00',1,32,2),
(2,4,5,4,'Buena comunicación durante el viaje','2025-06-16 21:00:00',2,33,3),
(3,5,4,5,'Profesional y cuidadoso con el pedido','2025-06-20 21:30:00',4,34,5),
(4,4,4,4,'Llegó bien, todo correcto','2025-06-22 22:30:00',5,35,6),
(5,5,5,5,'Excelente repartidor','2025-06-28 20:45:00',7,36,8),
-- Julio 2025
(6,5,4,5,'Muy atento y rápido','2025-07-02 14:30:00',8,37,9),
(7,4,5,4,'Buena comunicación','2025-07-05 21:30:00',9,38,10),
(8,5,5,5,'Perfecto el servicio','2025-07-10 20:15:00',11,39,12),
(9,4,4,5,'Profesional','2025-07-12 22:45:00',12,40,13),
(10,5,5,4,'Muy amable','2025-07-18 21:45:00',14,41,15),
(11,4,5,5,'Excelente atención','2025-07-20 20:30:00',15,42,16),
(12,5,4,4,'Buen servicio','2025-07-25 22:00:00',17,43,3),
(13,4,5,5,'Muy profesional','2025-07-28 20:15:00',18,44,4),
-- Agosto 2025
(14,5,5,5,'Repartidor top','2025-08-01 13:15:00',19,45,5),
(15,4,4,4,'Todo bien','2025-08-05 21:15:00',21,46,7),
(16,5,5,5,'Excelente','2025-08-08 20:45:00',22,32,8),
(17,4,5,4,'Muy buena comunicación','2025-08-12 21:30:00',24,33,10),
(18,5,4,5,'Cuidadoso con el pedido','2025-08-15 21:00:00',25,34,11),
(19,4,4,5,'Profesional','2025-08-20 11:45:00',27,35,13),
(20,5,5,4,'Amable','2025-08-22 21:15:00',28,36,14),
(21,4,5,5,'Buen servicio','2025-08-28 13:30:00',30,37,16),
-- Septiembre 2025
(22,5,4,5,'Muy puntual','2025-09-01 14:15:00',31,38,2),
(23,4,5,4,'Excelente comunicación','2025-09-05 20:15:00',33,39,4),
(24,5,5,5,'Top','2025-09-08 13:30:00',34,40,5),
(25,4,4,4,'Correcto','2025-09-12 22:00:00',36,41,7),
(26,5,5,5,'Muy profesional','2025-09-15 20:30:00',37,42,8),
(27,4,4,5,'Buena atención','2025-09-20 21:15:00',39,43,10),
(28,5,5,4,'Amable y rápido','2025-09-22 20:45:00',40,44,11),
-- Octubre 2025
(29,4,5,5,'Excelente','2025-10-03 11:15:00',42,45,13),
(30,5,4,4,'Buen servicio','2025-10-05 21:30:00',43,46,14),
(31,4,5,5,'Muy profesional','2025-10-10 13:30:00',45,32,16),
(32,5,5,4,'Amable','2025-10-12 14:15:00',46,33,2),
(33,4,4,5,'Cuidadoso','2025-10-18 20:45:00',48,34,4),
(34,5,5,5,'Top','2025-10-20 13:15:00',49,35,5),
-- Noviembre 2025
(35,4,5,4,'Buena comunicación','2025-11-01 21:15:00',51,36,7),
(36,5,4,5,'Profesional','2025-11-03 20:45:00',52,37,8),
(37,4,5,5,'Muy atento','2025-11-08 21:30:00',54,38,10),
(38,5,5,4,'Excelente','2025-11-10 21:00:00',55,39,11),
(39,4,4,5,'Correcto','2025-11-15 11:45:00',57,40,13),
(40,5,5,5,'Top repartidor','2025-11-18 21:15:00',58,41,14),
(41,4,5,4,'Buen servicio','2025-11-22 13:30:00',60,42,16),
(42,5,4,5,'Muy profesional','2025-11-25 14:15:00',61,43,2),
(43,4,5,5,'Amable','2025-11-29 20:15:00',63,44,4),
-- Diciembre 2025
(44,5,5,4,'Excelente atención','2025-12-01 13:30:00',64,45,5),
(45,4,4,5,'Profesional','2025-12-03 22:00:00',66,46,7),
(46,5,5,5,'Top','2025-12-05 20:30:00',67,32,8),
(47,4,5,4,'Buena comunicación','2025-12-07 21:15:00',69,33,10),
(48,5,4,5,'Cuidadoso','2025-12-08 20:45:00',70,34,11),
(49,4,5,5,'Muy atento','2025-12-10 13:45:00',76,36,2),
(50,5,5,4,'Excelente','2025-12-10 20:15:00',77,37,3),
(51,4,4,5,'Buen servicio','2025-12-11 21:15:00',79,38,5);

-- 16) Ganancias de repartidores (para pedidos DELIVERY COMPLETADOS)
-- formato: (id, pedido_id, repartidor_id, costo_delivery, comision_plataforma, ganancia_repartidor, porcentaje_aplicado, fecha_registro)
-- porcentaje_aplicado = 80% para el repartidor, 20% comision plataforma
INSERT INTO `ganancias_repartidor` (id, pedido_id, repartidor_id, costo_delivery, comision_plataforma, ganancia_repartidor, porcentaje_aplicado, fecha_registro) VALUES
-- Junio 2025
(1,1,32,5000.00,1000.00,4000.00,80.00,'2025-06-15 13:15:00'),
(2,2,33,6500.00,1300.00,5200.00,80.00,'2025-06-16 20:35:00'),
(3,4,34,5000.00,1000.00,4000.00,80.00,'2025-06-20 21:00:00'),
(4,5,35,14000.00,2800.00,11200.00,80.00,'2025-06-22 22:00:00'),
(5,7,36,6669.00,1333.80,5335.20,80.00,'2025-06-28 20:20:00'),
-- Julio 2025
(6,8,37,5000.00,1000.00,4000.00,80.00,'2025-07-02 14:00:00'),
(7,9,38,10000.00,2000.00,8000.00,80.00,'2025-07-05 20:55:00'),
(8,11,39,5000.00,1000.00,4000.00,80.00,'2025-07-10 19:45:00'),
(9,12,40,7000.00,1400.00,5600.00,80.00,'2025-07-12 22:20:00'),
(10,14,41,5000.00,1000.00,4000.00,80.00,'2025-07-18 21:15:00'),
(11,15,42,4800.00,960.00,3840.00,80.00,'2025-07-20 20:00:00'),
(12,17,43,6500.00,1300.00,5200.00,80.00,'2025-07-25 21:35:00'),
(13,18,44,5000.00,1000.00,4000.00,80.00,'2025-07-28 19:45:00'),
-- Agosto 2025
(14,19,45,5000.00,1000.00,4000.00,80.00,'2025-08-01 12:45:00'),
(15,21,46,5000.00,1000.00,4000.00,80.00,'2025-08-05 20:45:00'),
(16,22,32,6500.00,1300.00,5200.00,80.00,'2025-08-08 20:20:00'),
(17,24,33,10600.00,2120.00,8480.00,80.00,'2025-08-12 21:05:00'),
(18,25,34,7000.00,1400.00,5600.00,80.00,'2025-08-15 20:35:00'),
(19,27,35,5000.00,1000.00,4000.00,80.00,'2025-08-20 11:15:00'),
(20,28,36,7450.00,1490.00,5960.00,80.00,'2025-08-22 20:50:00'),
(21,30,37,5000.00,1000.00,4000.00,80.00,'2025-08-28 13:10:00'),
-- Septiembre 2025
(22,31,38,10000.00,2000.00,8000.00,80.00,'2025-09-01 13:50:00'),
(23,33,39,5000.00,1000.00,4000.00,80.00,'2025-09-05 19:45:00'),
(24,34,40,6000.00,1200.00,4800.00,80.00,'2025-09-08 13:00:00'),
(25,36,41,7500.00,1500.00,6000.00,80.00,'2025-09-12 21:35:00'),
(26,37,42,6500.00,1300.00,5200.00,80.00,'2025-09-15 20:05:00'),
(27,39,43,9000.00,1800.00,7200.00,80.00,'2025-09-20 20:50:00'),
(28,40,44,5000.00,1000.00,4000.00,80.00,'2025-09-22 20:15:00'),
-- Octubre 2025
(29,42,45,5000.00,1000.00,4000.00,80.00,'2025-10-03 10:45:00'),
(30,43,46,5949.00,1189.80,4759.20,80.00,'2025-10-05 21:00:00'),
(31,45,32,5000.00,1000.00,4000.00,80.00,'2025-10-10 13:10:00'),
(32,46,33,9800.00,1960.00,7840.00,80.00,'2025-10-12 13:55:00'),
(33,48,34,5000.00,1000.00,4000.00,80.00,'2025-10-18 20:15:00'),
(34,49,35,5000.00,1000.00,4000.00,80.00,'2025-10-20 12:45:00'),
-- Noviembre 2025
(35,51,36,5000.00,1000.00,4000.00,80.00,'2025-11-01 20:45:00'),
(36,52,37,6500.00,1300.00,5200.00,80.00,'2025-11-03 20:20:00'),
(37,54,38,10600.00,2120.00,8480.00,80.00,'2025-11-08 21:10:00'),
(38,55,39,7000.00,1400.00,5600.00,80.00,'2025-11-10 20:35:00'),
(39,57,40,5000.00,1000.00,4000.00,80.00,'2025-11-15 11:15:00'),
(40,58,41,6449.00,1289.80,5159.20,80.00,'2025-11-18 20:50:00'),
(41,60,42,5000.00,1000.00,4000.00,80.00,'2025-11-22 13:10:00'),
(42,61,43,10000.00,2000.00,8000.00,80.00,'2025-11-25 13:50:00'),
(43,63,44,5000.00,1000.00,4000.00,80.00,'2025-11-29 19:45:00'),
-- Diciembre 2025
(44,64,45,5000.00,1000.00,4000.00,80.00,'2025-12-01 13:00:00'),
(45,66,46,5000.00,1000.00,4000.00,80.00,'2025-12-03 21:35:00'),
(46,67,32,4500.00,900.00,3600.00,80.00,'2025-12-05 20:05:00'),
(47,69,33,8900.00,1780.00,7120.00,80.00,'2025-12-07 20:55:00'),
(48,70,34,6000.00,1200.00,4800.00,80.00,'2025-12-08 20:20:00'),
(49,76,36,4100.00,820.00,3280.00,80.00,'2025-12-10 13:15:00'),
(50,77,37,5000.00,1000.00,4000.00,80.00,'2025-12-10 19:45:00'),
(51,79,38,6500.00,1300.00,5200.00,80.00,'2025-12-11 20:50:00');
