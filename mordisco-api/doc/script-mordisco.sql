USE mordisco;

-- Insertar roles
INSERT INTO roles (nombre) VALUES 
('ADMIN'), 
('CLIENTE'), 
('RESTAURANTE');

-- Insertar ADMIN
INSERT INTO usuarios (apellido, email, nombre, password, telefono, rol_id) VALUES
('Pérez', 'admin@mordisco.com', 'Admin', 'admin123', '1111111111', 1);

-- Insertar clientes
INSERT INTO usuarios (apellido, email, nombre, password, telefono, rol_id) VALUES
('Alvarez', 'cliente1@mordisco.com', 'Juan', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333331', 2),
('Benítez', 'cliente2@mordisco.com', 'Carla', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333332', 2),
('Cabrera', 'cliente3@mordisco.com', 'Pedro', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333333', 2),
('Castro', 'cliente4@mordisco.com', 'Ana', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333334', 2),
('González', 'cliente5@mordisco.com', 'Martín', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333335', 2),
('Herrera', 'cliente6@mordisco.com', 'Sandra', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333336', 2),
('Jiménez', 'cliente7@mordisco.com', 'Luis', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333337', 2),
('Luna', 'cliente8@mordisco.com', 'Patricia', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333338', 2),
('Martínez', 'cliente9@mordisco.com', 'Tomás', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333339', 2),
('Moreno', 'cliente10@mordisco.com', 'Ricardo', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333340', 2),
('Navarro', 'cliente11@mordisco.com', 'Marta', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333341', 2),
('Oliva', 'cliente12@mordisco.com', 'Raúl', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333342', 2),
('Pérez', 'cliente13@mordisco.com', 'José', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333343', 2),
('Ramírez', 'cliente14@mordisco.com', 'Verónica', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333344', 2),
('Ríos', 'cliente15@mordisco.com', 'Estela', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333345', 2),
('Rodríguez', 'cliente16@mordisco.com', 'Antonio', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333346', 2),
('Sánchez', 'cliente17@mordisco.com', 'Elena', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333347', 2),
('Torres', 'cliente18@mordisco.com', 'Diego', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333348', 2),
('Vargas', 'cliente19@mordisco.com', 'Joaquín', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333349', 2),
('Álvarez', 'cliente20@mordisco.com', 'Sofía', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333350', 2),
('Bravo', 'cliente21@mordisco.com', 'Gabriela', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333351', 2),
('Cano', 'cliente22@mordisco.com', 'Felipe', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333352', 2),
('Castillo', 'cliente23@mordisco.com', 'Luisana', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333353', 2),
('Domínguez', 'cliente24@mordisco.com', 'Pablo', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333354', 2),
('Gómez', 'cliente25@mordisco.com', 'Nicolás', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333355', 2),
('Gutiérrez', 'cliente26@mordisco.com', 'Marina', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333356', 2),
('López', 'cliente27@mordisco.com', 'Andrés', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333357', 2),
('Méndez', 'cliente28@mordisco.com', 'Juanita', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333358', 2),
('Paredes', 'cliente29@mordisco.com', 'Ricardo', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333359', 2),
('Suárez', 'cliente30@mordisco.com', 'Patricio', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333360', 2);


-- 1. Insertar usuarios dueños
INSERT INTO usuarios (apellido, email, nombre, password, telefono, rol_id) VALUES
('Paredes', 'dueno1@mordisco.com', 'Andrés', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333316', 3),
('Quintero', 'dueno2@mordisco.com', 'Elena', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333317', 3),
('Ramos', 'dueno3@mordisco.com', 'Esteban', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333318', 3),
('Sánchez', 'dueno4@mordisco.com', 'Gabriela', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333319', 3),
('Torres', 'dueno5@mordisco.com', 'Hugo', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333320', 3),
('Urbina', 'dueno6@mordisco.com', 'Isabel', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333321', 3),
('Valdez', 'dueno7@mordisco.com', 'Javier', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333322', 3),
('Vega', 'dueno8@mordisco.com', 'Laura', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333323', 3),
('Zapata', 'dueno9@mordisco.com', 'Marcos', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333324', 3),
('Acosta', 'dueno10@mordisco.com', 'Natalia', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333325', 3),
('Blanco', 'dueno11@mordisco.com', 'Oscar', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333326', 3),
('Campos', 'dueno12@mordisco.com', 'Paula', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333327', 3),
('Díaz', 'dueno13@mordisco.com', 'Ricardo', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333328', 3),
('Estévez', 'dueno14@mordisco.com', 'Sofía', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333329', 3),
('Flores', 'dueno15@mordisco.com', 'Tomás', '$2a$10$6w3FGu7sjm0K9AKWLGgzGOmMs3a.9Tiqmp68QdQtCjNUVrWhSDBN6', '3333333330', 3);

-- 2. Insertar menús
INSERT INTO menus (nombre) VALUES
('Menú Clásico'),
('Menú Vegano'),
('Menú Infantil'),
('Menú Gourmet'),
('Menú Exprés'),
('Menú Sin Gluten'),
('Menú Vegetariano'),
('Menú Orgánico'),
('Menú Mediterráneo'),
('Menú Asiático'),
('Menú Mexicano'),
('Menú Italiano'),
('Menú Americano'),
('Menú Low Carb'),
('Menú Detox');

-- Insertar imágenes
INSERT INTO imagenes (nombre, url) VALUES
('Hamburguesa Clásica', 'https://cdn.example.com/img/hamburguesa_clasica.png'),
('Pizza Margherita', 'https://cdn.example.com/img/pizza_margherita.png'),
('Ensalada César', 'https://cdn.example.com/img/ensalada_cesar.png'),
('Papas Fritas', 'https://cdn.example.com/img/papas_fritas.png'),
('Sushi Variado', 'https://cdn.example.com/img/sushi_variado.png'),
('Tacos al Pastor', 'https://cdn.example.com/img/tacos_al_pastor.png'),
('Lasaña', 'https://cdn.example.com/img/lasana.png'),
('Pasta Bolognesa', 'https://cdn.example.com/img/pasta_bolognesa.png'),
('Croquetas de Pollo', 'https://cdn.example.com/img/croquetas_pollo.png'),
('Bocadillo de Jamón y Queso', 'https://cdn.example.com/img/bocadillo.png'),
('Hot Dog', 'https://cdn.example.com/img/hot_dog.png'),
('Pechuga de Pollo a la Parrilla', 'https://cdn.example.com/img/pechuga_pollo.png'),
('Risotto de Champiñones', 'https://cdn.example.com/img/risotto_champinones.png'),
('Ramen', 'https://cdn.example.com/img/ramen.png'),
('Tarta de Espinaca y Queso', 'https://cdn.example.com/img/tarta_espinaca.png'),
('Sopa de Tomate', 'https://cdn.example.com/img/sopa_tomate.png'),
('Empanadas de Carne', 'https://cdn.example.com/img/empanadas_carne.png'),
('Churrasco', 'https://cdn.example.com/img/churrasco.png'),
('Ensalada Griega', 'https://cdn.example.com/img/ensalada_griega.png'),
('Tartaleta de Frutas', 'https://cdn.example.com/img/tartaleta_frutas.png'),
('Pizza Pepperoni', 'https://cdn.example.com/img/pizza_pepperoni.png'),
('Tequeños', 'https://cdn.example.com/img/tequenos.png'),
('Pasta Alfredo', 'https://cdn.example.com/img/pasta_alfredo.png'),
('Pollo al Curry', 'https://cdn.example.com/img/pollo_curry.png'),
('Paella de Mariscos', 'https://cdn.example.com/img/paella_mariscos.png'),
('Ceviche', 'https://cdn.example.com/img/ceviche.png'),
('Sopa de Mariscos', 'https://cdn.example.com/img/sopa_mariscos.png'),
('Bife de Chorizo', 'https://cdn.example.com/img/bife_chorizo.png'),
('Ceviche de Camarón', 'https://cdn.example.com/img/ceviche_camaron.png'),
('Pasta Pesto', 'https://cdn.example.com/img/pasta_pesto.png'),
('Gnocchi', 'https://cdn.example.com/img/gnocchi.png'),
('Ensalada de Atún', 'https://cdn.example.com/img/ensalada_atun.png'),
('Arroz Frito', 'https://cdn.example.com/img/arroz_frito.png'),
('Pollo a la Parrilla', 'https://cdn.example.com/img/pollo_parrilla.png'),
('Hamburguesa Vegana', 'https://cdn.example.com/img/hamburguesa_vegana.png'),
('Tacos de Pescado', 'https://cdn.example.com/img/tacos_pescado.png'),
('Milanesa de Pollo', 'https://cdn.example.com/img/milanesa_pollo.png'),
('Tarta de Manzana', 'https://cdn.example.com/img/tarta_manzana.png'),
('Sopa de Lentejas', 'https://cdn.example.com/img/sopa_lentejas.png'),
('Pasta Carbonara', 'https://cdn.example.com/img/pasta_carbonara.png'),
('Ensalada Capresse', 'https://cdn.example.com/img/ensalada_capresse.png'),
('Pizza Hawaiana', 'https://cdn.example.com/img/pizza_hawaiana.png'),
('Pollo Asado', 'https://cdn.example.com/img/pollo_asado.png'),
('Tacos Vegetarianos', 'https://cdn.example.com/img/tacos_vegetarianos.png'),
('Tarta de Queso','https://cdn.example.com/img/tarta_queso.png'),
('Restaurante Delicias','https://cdn.example.com/img/resto_nuevo65.png'),
('La Buena Mesa','https://cdn.example.com/img/resto_nuevo66.png'),
('Sabor Natural','https://cdn.example.com/img/resto_nuevo67.png'),
('El Rincón Vegano','https://cdn.example.com/img/resto_nuevo68.png'),
('Casa Tradicional','https://cdn.example.com/img/resto_nuevo69.png'),
('Sabores del Mundo','https://cdn.example.com/img/resto_nuevo70.png'),
('Cocina Urbana','https://cdn.example.com/img/resto_nuevo71.png'),
('La Esquina Feliz','https://cdn.example.com/img/resto_nuevo72.png'),
('Bocados Caseros','https://cdn.example.com/img/resto_nuevo73.png'),
('Delicias al Paso','https://cdn.example.com/img/resto_nuevo74.png'),
('El Fogón de la Abuela','https://cdn.example.com/img/resto_nuevo75.png'),
('El Jardín Secreto','https://cdn.example.com/img/resto_nuevo76.png'),
('Rincón Gourmet','https://cdn.example.com/img/resto_nuevo77.png'),
('Sabores y Aromas','https://cdn.example.com/img/resto_nuevo78.png'),
('Fusión Latina','https://cdn.example.com/img/resto_nuevo79.png');


-- 4. Insertar direcciones
INSERT INTO direcciones (calle, ciudad, codigo_postal, depto, latitud, longitud, numero, piso, referencias, usuario_id) VALUES
('Av. Independencia', 'Mar del Plata', '7600', NULL, -38.0032, -57.5521, '567', NULL, 'Esquina con Rivadavia', 2),
('Calle 12', 'Buenos Aires', '1406', 'A', -34.6037, -58.3816, '245', '5', 'Cerca del parque', 3),
('Calle 45', 'La Plata', '1900', NULL, -34.9205, -57.9536, '789', NULL, 'Al lado de la estación', 4),
('Boulevard Oroño', 'Rosario', '2000', '12', -32.9468, -60.6393, '1122', '1', 'Frente al supermercado', 5),
('Calle San Martín', 'Córdoba', '5000', NULL, -31.4201, -64.1888, '334', NULL, 'Próximo al hospital', 6),
('Avenida Rivadavia', 'Buenos Aires', '1033', '4', -34.6118, -58.4173, '678', '2', 'Edificio moderno', 7),
('Pasaje Güemes', 'Mendoza', '5500', NULL, -32.8908, -68.8272, '987', NULL, 'Zona tranquila', 8),
('Calle Bolívar', 'Salta', '4400', '7', -24.7821, -65.4232, '456', '7', 'Cerca del museo', 9),
('Calle Mitre', 'Santa Fe', '3000', NULL, -31.6333, -60.7000, '321', NULL, 'Vista al río', 10),
('Avenida Libertad', 'Tucumán', '4000', '3', -26.8083, -65.2176, '159', '5', 'Zona comercial', 11),
('Calle Roca', 'Neuquén', '8300', NULL, -38.9517, -68.0597, '753', NULL, 'Frente al colegio', 12),
('Avenida Corrientes', 'Buenos Aires', '1043', '11', -34.6158, -58.4370, '842', '8', 'Cerca del teatro', 13),
('Calle Sarmiento', 'Mar del Plata', '7600', NULL, -38.0078, -57.5445, '215', NULL, 'Zona céntrica', 14),
('Avenida San Juan', 'San Juan', '5400', '5', -31.5375, -68.5364, '174', '4', 'Cerca del centro', 15),
('Calle Moreno', 'Resistencia', '3500', NULL, -27.4517, -59.0317, '653', NULL, 'Edificio antiguo', 16),
('Boulevard Gálvez', 'Santa Fe', '3000', '2', -31.6327, -60.7001, '924', '9', 'Zona residencial', 17),
('Calle Córdoba', 'Córdoba', '5000', NULL, -31.4167, -64.1833, '801', NULL, 'Frente al parque', 18),
('Avenida Pellegrini', 'Rosario', '2000', '10', -32.9465, -60.6398, '482', '1', 'Zona universitaria', 19),
('Calle San Luis', 'Mendoza', '5500', NULL, -32.8899, -68.8287, '315', NULL, 'Frente a la plaza', 20),
('Avenida Belgrano', 'La Plata', '1900', '1', -34.9211, -57.9543, '129', '3', 'Zona histórica', 21),
('Calle España', 'Salta', '4400', NULL, -24.7825, -65.4235, '464', NULL, 'Cerca de la catedral', 22),
('Avenida Uruguay', 'Buenos Aires', '1406', '6', -34.6045, -58.3792, '536', '2', 'Zona comercial', 23),
('Pasaje Mendoza', 'Tucumán', '4000', NULL, -26.8079, -65.2183, '418', NULL, 'Edificio nuevo', 24),
('Calle 9 de Julio', 'Resistencia', '3500', '8', -27.4515, -59.0319, '722', '7', 'Cerca de la plaza', 25),
('Av. Colón', 'Mar del Plata', '7600', NULL, -38.0055, -57.5426, '235', NULL, NULL, 26),
('Av. Independencia', 'Mar del Plata', '7600', 'D', -38.0055, -57.5426, '8455', '8', 'Porton rojo', 27),
('Av. 9 de Julio', 'Buenos Aires', '1406', NULL, -38.0055, -57.5426, '774', NULL, NULL, 28),
('Jujuy', 'Mar del Plata', '7600', NULL, -38.0055, -57.5426, '2130', NULL, NULL, 29),
('Mendoza', 'Resistencia', '3500', 'A', -38.0055, -57.5426, '134', '1', NULL, 30),
('Av. Jorge Newbery', 'Mar del Plata', '7600', '52', -38.0055, -57.5426, '5005', 'Tilos', 'Barrio Rumenco', 31),
-- Direcciones Restaurantes
('San Juan', 'Mar del Plata', '7600', NULL, -38.0055, -57.5426, '628', NULL, 'Rejas Verdes', 32),
('Mitre', 'Mar del Plata', '7600', 'C', -38.0055, -57.5426, '888', NULL, NULL, 33),
('Catamarca', 'Resistencia', '3500', 'B', -38.0055, -57.5426, '3951', '10', NULL, 34),
('Av. Paso', 'Mar del Plata', '7600', NULL, -38.0055, -57.5426, '3575', '1', NULL, 35),
('Guemes', 'Salta', '4400', 'A', -38.0055, -57.5426, '57', '7', 'Edificio frente al banco', 36),
('Olazabal', 'Mar del Plata', '7600', NULL, -38.0055, -57.5426, '689', NULL, NULL, 37),
('Alem','Salta', '4400', 'B', -38.0055, -57.5426, '2588', '3', 'Edificio Dumbledore', 38),
('Tucuman', 'Mar del Plata', '7600', NULL, -38.0055, -57.5426, '9996', NULL, NULL, 39),
('La Rioja', 'Mar del Plata', '7600', NULL, -38.0055, -57.5426, '667', NULL, 'Rejas negras', 40),
('Chaco', 'Mar del Plata', '7600', 'C', -38.0055, -57.5426, '124', NULL, NULL, 41),
('12 de Octubre', 'Salta', '4400', NULL, -38.0055, -57.5426, '8100', '8', NULL, 42),
('Av. Libertad', 'Mar del Plata', '7600', NULL, -38.0055, -57.5426, '901', NULL, NULL, 43),
('Cordoba', 'Mar del Plata', '7600', NULL, -38.0055, -57.5426, '7833', NULL, NULL, 44),
('Av. Mar del Plata', 'Mendoza', '5500', NULL, -38.0055, -57.5426, '1100', NULL, NULL, 45),
('Italia', 'Mendoza', '5500', NULL, -38.0055, -57.5426, '1414', NULL, NULL, 46);

-- 5. Insertar restaurantes
INSERT INTO restaurantes (activo, razon_social, direccion_id, imagen_id, menu_id, usuario_id) VALUES
(b'1', 'Restaurante Delicias', 31, 46, 1, 32),
(b'1', 'La Buena Mesa', 32, 47, 2,33),
(b'1', 'Sabor Natural',33, 48, 3, 34),
(b'1', 'El Rincón Vegano', 34, 49, 4, 35),
(b'1', 'Casa Tradicional', 35, 50, 5, 36),
(b'1', 'Sabores del Mundo', 36, 51,6, 37),
(b'1', 'Cocina Urbana', 37, 52,7, 38),
(b'1', 'La Esquina Feliz', 38, 53, 8, 39),
(b'1', 'Bocados Caseros', 39, 54, 9, 40),
(b'1', 'Delicias al Paso', 40, 55, 10, 41),
(b'1', 'El Fogón de la Abuela', 41, 56, 11, 42),
(b'1', 'El Jardín Secreto', 42, 57, 12, 43),
(b'1', 'Rincón Gourmet', 43, 58, 13, 44),
(b'1', 'Sabores y Aromas', 44, 59, 14, 45),
(b'1', 'Fusión Latina', 45, 60, 15, 46);

-- Insertar horarios de atención para los restaurantes
INSERT INTO horarios_atencion (dia, hora_apertura, hora_cierre, restaurante_id) VALUES
('MONDAY', '18:00:00', '23:00:00', 1),
('WEDNESDAY', '18:00:00', '23:00:00', 1),
('THURSDAY', '18:00:00', '23:00:00', 1),
('FRIDAY', '18:00:00', '23:00:00', 1),
('SATURDAY', '18:00:00', '23:00:00', 1),
('SUNDAY', '18:00:00', '23:00:00', 1),

('TUESDAY', '15:00:00', '23:00:00', 2),
('WEDNESDAY', '15:00:00', '23:00:00', 2),
('THURSDAY', '15:00:00', '23:00:00', 2),
('FRIDAY', '15:00:00', '23:00:00', 2),
('SATURDAY', '15:00:00', '23:00:00', 2),
('SUNDAY', '15:00:00', '23:00:00', 2),

('MONDAY', '11:00:00', '23:00:00', 3),
('TUESDAY', '11:00:00', '23:00:00', 3),
('WEDNESDAY', '11:00:00', '23:00:00', 3),
('THURSDAY', '11:00:00', '23:00:00', 3),
('FRIDAY', '11:00:00', '23:00:00', 3),
('SATURDAY', '11:00:00', '23:00:00', 3),
('SUNDAY', '11:00:00', '23:00:00', 3),

('MONDAY', '11:00:00', '23:00:00', 4),
('TUESDAY', '11:00:00', '23:00:00', 4),
('WEDNESDAY', '11:00:00', '23:00:00', 4),
('THURSDAY', '11:00:00', '23:00:00', 4),
('FRIDAY', '11:00:00', '23:00:00', 4),
('SATURDAY', '11:00:00', '23:00:00', 4),
('SUNDAY', '11:00:00', '23:00:00', 4),

('MONDAY', '12:00:00', '22:00:00', 5),
('TUESDAY', '12:00:00', '22:00:00', 5),
('THURSDAY', '12:00:00', '22:00:00', 5),
('FRIDAY', '12:00:00', '22:00:00', 5),
('SATURDAY', '12:00:00', '22:00:00',5),
('SUNDAY', '12:00:00', '22:00:00', 5),

('MONDAY', '11:00:00', '23:30:00', 6),
('TUESDAY', '11:00:00', '23:30:00', 6),
('WEDNESDAY', '11:00:00', '23:30:00', 6),
('THURSDAY', '11:00:00', '23:30:00', 6),
('FRIDAY', '11:00:00', '23:30:00', 6),
('SATURDAY', '11:00:00', '23:30:00', 6),
('SUNDAY', '11:00:00', '23:30:00', 6),

('MONDAY', '10:00:00', '23:30:00', 7),
('TUESDAY', '10:00:00', '23:30:00', 7),
('WEDNESDAY', '10:00:00', '23:30:00', 7),
('THURSDAY', '10:00:00', '23:30:00', 7),
('FRIDAY', '10:00:00', '23:30:00', 7),
('SATURDAY', '10:00:00', '23:30:00', 7),
('SUNDAY', '10:00:00', '23:30:00', 7),

('TUESDAY', '10:00:00', '23:30:00', 8),
('WEDNESDAY', '10:00:00', '23:30:00',8),
('THURSDAY', '10:00:00', '23:30:00', 8),
('FRIDAY', '10:00:00', '23:30:00', 8),
('SATURDAY', '10:00:00', '23:30:00', 8),
('SUNDAY', '10:00:00', '23:30:00', 8),


('MONDAY', '10:00:00', '23:30:00', 9),
('TUESDAY', '10:00:00', '23:30:00', 9),
('WEDNESDAY', '10:00:00', '23:30:00',9),
('FRIDAY', '10:00:00', '23:30:00', 9),
('SATURDAY', '10:00:00', '23:30:00', 9),
('SUNDAY', '10:00:00', '23:30:00', 9),

('MONDAY', '12:00:00', '23:30:00', 10),
('TUESDAY', '12:00:00', '23:30:00',10),
('WEDNESDAY', '12:00:00', '23:30:00', 10),
('THURSDAY', '12:00:00', '23:30:00', 10),
('FRIDAY', '12:00:00', '23:30:00', 10),
('SATURDAY', '12:00:00', '23:30:00', 10),
('SUNDAY', '12:00:00', '23:30:00', 10),

('MONDAY', '8:00:00', '15:30:00', 11),
('TUESDAY', '8:00:00', '15:30:00',11),
('WEDNESDAY', '8:00:00', '15:30:00', 11),
('THURSDAY', '8:00:00', '15:30:00', 11),
('FRIDAY', '8:00:00', '15:30:00', 11),
('SATURDAY', '8:00:00', '15:30:00', 11),
('SUNDAY', '8:00:00', '15:30:00', 11),


('TUESDAY', '8:00:00', '15:30:00',12),
('WEDNESDAY', '8:00:00', '15:30:00', 12),
('THURSDAY', '8:00:00', '15:30:00', 12),
('FRIDAY', '8:00:00', '15:30:00', 12),
('SATURDAY', '8:00:00', '15:30:00', 12),
('SUNDAY', '8:00:00', '15:30:00', 12),

('MONDAY', '8:00:00', '16:30:00', 13),
('TUESDAY', '8:00:00', '16:30:00',13),
('WEDNESDAY', '8:00:00', '16:30:00', 13),
('THURSDAY', '8:00:00', '16:30:00', 13),
('FRIDAY', '8:00:00', '16:30:00', 13),
('SATURDAY', '8:00:00', '16:30:00', 13),
('SUNDAY', '8:00:00', '16:30:00', 13),

('MONDAY', '8:00:00', '16:30:00', 14),
('TUESDAY', '8:00:00', '16:30:00',14),
('THURSDAY', '8:00:00', '16:30:00', 14),
('FRIDAY', '8:00:00', '16:30:00', 14),
('SATURDAY', '8:00:00', '16:30:00', 14),
('SUNDAY', '8:00:00', '16:30:00', 14),

('MONDAY', '8:00:00', '23:30:00', 15),
('TUESDAY', '8:00:00', '23:30:00',15),
('WEDNESDAY', '8:00:00', '23:30:00', 15),
('THURSDAY', '8:00:00', '23:30:00', 15),
('FRIDAY', '8:00:00', '23:30:00', 15),
('SATURDAY', '8:00:00', '23:30:00', 15),
('SUNDAY', '8:00:00', '23:30:00', 15);

-- Insertar 45 productos
INSERT INTO productos (descripcion, disponible, nombre, precio, imagen_id, menu_id) VALUES
('Hamburguesa Clásica con carne, lechuga, tomate y mayonesa.', b'1', 'Hamburguesa Clásica', 1200.00, 1, 1),
('Pizza Margherita con salsa de tomate, mozzarella y albahaca.', b'1', 'Pizza Margherita', 1500.00, 2, 1),
('Ensalada César con lechuga, pollo, crotones y aderezo César.', b'1', 'Ensalada César', 1000.00, 3, 1),
('Papas Fritas crujientes con sal.', b'1', 'Papas Fritas', 600.00, 4, 2),
('Sushi Variado con pescado fresco y arroz.', b'1', 'Sushi Variado', 1800.00, 5, 2),
('Tacos al Pastor con carne de cerdo, piña, cebolla y cilantro.', b'1', 'Tacos al Pastor', 900.00, 6, 3),
('Lasaña con carne y salsa bechamel.', b'1', 'Lasaña', 1700.00, 7, 3),
('Pasta a la Bolognesa con salsa de carne.', b'1', 'Pasta Bolognesa', 1300.00, 8, 3),
('Croquetas de Pollo con salsa de mostaza.', b'1', 'Croquetas de Pollo', 800.00, 9, 3),
('Bocadillo de Jamón y Queso', b'1', 'Bocadillo', 1100.00, 10, 4),
('Hot Dog con ketchup, mostaza y cebolla.', b'1', 'Hot Dog', 950.00, 11, 5),
('Pechuga de Pollo a la Parrilla con arroz.', b'1', 'Pechuga de Pollo', 1500.00, 12, 5),
('Risotto de Champiñones', b'1', 'Risotto de Champiñones', 1600.00, 13, 5),
('Ramen con caldo de pollo y fideos', b'1', 'Ramen', 1400.00, 14, 6),
('Tarta de Espinaca y Queso', b'1', 'Tarta de Espinaca', 1200.00, 15, 6),
('Sopa de Tomate con pan crujiente', b'1', 'Sopa de Tomate', 950.00, 16, 7),
('Empanadas de Carne', b'1', 'Empanadas', 700.00, 17, 7),
('Churrasco con ensalada de papas', b'1', 'Churrasco', 2100.00, 18, 7),
('Ensalada Griega con aceitunas y feta', b'1', 'Ensalada Griega', 1300.00, 19, 7),
('Tartaleta de Frutas', b'1', 'Tartaleta de Frutas', 800.00, 20, 8),
('Pizza Caliente de Pepperoni', b'1', 'Pizza Pepperoni', 1700.00, 21, 8),
('Tequeños con salsa de guacamole', b'1', 'Tequeños', 950.00, 22, 9),
('Pasta Alfredo con salsa de queso', b'1', 'Pasta Alfredo', 1400.00, 23, 10),
('Pollo al Curry con arroz basmati', b'1', 'Pollo al Curry', 1800.00, 24, 10),
('Paella de Mariscos', b'1', 'Paella de Mariscos', 2500.00, 25, 10),
('Ceviche de Pescado con limón y cilantro', b'1', 'Ceviche', 1300.00, 26, 11),
('Sopa de Mariscos', b'1', 'Sopa de Mariscos', 1600.00, 27, 11),
('Bife de Chorizo con puré de papas', b'1', 'Bife de Chorizo', 2200.00, 28, 12),
('Ceviche de Camarón con aguacate', b'1', 'Ceviche de Camarón', 1700.00, 29, 12),
('Pasta Pesto con albahaca y piñones', b'1', 'Pasta Pesto', 1400.00, 30, 12),
('Gnocchi con salsa de tomate y queso parmesano', b'1', 'Gnocchi', 1200.00, 31, 13),
('Ensalada de Atún con mayonesa y zanahorias', b'1', 'Ensalada de Atún', 1000.00, 32, 13),
('Arroz Frito con Pollo', b'1', 'Arroz Frito', 1200.00, 33, 13),
('Pollo a la Parrilla con puré de papas', b'1', 'Pollo a la Parrilla', 1500.00, 34, 14),
('Burgers Veganas con pan integral', b'1', 'Hamburguesa Vegana', 1300.00, 35, 14),
('Tacos de Pescado con salsa picante', b'1', 'Tacos de Pescado', 1000.00, 36, 14),
('Milanesa de Pollo con ensalada', b'1', 'Milanesa de Pollo', 1400.00, 37, 14),
('Tarta de Manzana con crema pastelera', b'1', 'Tarta de Manzana', 900.00, 38, 15),
('Sopa de Lentejas con verduras', b'1', 'Sopa de Lentejas', 850.00, 39, 15),
('Pasta Carbonara con bacon y crema', b'1', 'Pasta Carbonara', 1500.00, 40, 15),
('Ensalada Capresse con tomate, mozzarella y albahaca', b'1', 'Ensalada Capresse', 1200.00, 41, 15),
('Pizza Hawaiana con jamón y piña', b'1', 'Pizza Hawaiana', 1600.00, 42, 2),
('Pollo Asado con papas a la provenzal', b'1', 'Pollo Asado', 1800.00, 43, 1),
('Tacos Vegetarianos con vegetales y guacamole', b'1', 'Tacos Vegetarianos', 1100.00, 44, 8),
('Tarta de Queso con frutas del bosque', b'1', 'Tarta de Queso', 950.00, 45, 9);

-- Promociones de restaurantes
INSERT INTO promociones (descripcion, descuento, fecha_inicio, fecha_fin, restaurante_id) VALUES
('Descuento del 10% en todos los platos italianos', 0.10, '2025-06-01', '2025-06-30', 1),
('Promoción 15% OFF en pizzas y pastas', 0.15, '2025-06-05', '2025-06-25', 2),
('20% de descuento en ensaladas y platos frescos', 0.20, '2025-06-10', '2025-07-10', 3),
('15% OFF en combos de hamburguesas', 0.15, '2025-05-25', '2025-06-15', 4),
('10% descuento en sushi y productos del mar', 0.10, '2025-06-01', '2025-06-20', 5),
('25% OFF en tacos al pastor', 0.25, '2025-06-07', '2025-06-30', 6),
('Descuento del 5% en lasañas y pastas', 0.05, '2025-06-12', '2025-07-05', 7),
('15% OFF en platos a la parrilla', 0.15, '2025-06-01', '2025-06-30', 8),
('10% descuento en bocadillos y snacks', 0.10, '2025-06-10', '2025-07-01', 9),
('20% OFF en comida rápida y delivery', 0.20, '2025-05-30', '2025-06-30', 10),
('15% descuento en productos vegetarianos', 0.15, '2025-06-05', '2025-06-25', 11),
('10% OFF en platos tradicionales', 0.10, '2025-06-01', '2025-07-01', 12),
('5% de descuento en menús infantiles', 0.05, '2025-06-10', '2025-07-10', 13),
('20% OFF en postres y tartas', 0.20, '2025-06-15', '2025-07-15', 14),
('15% descuento en platos especiales del día', 0.15, '2025-06-01', '2025-06-30', 15);

-- Pedido 1 - En proceso, retiro por local, dirección del restaurante
INSERT INTO pedidos (estado, fecha_hora, tipo_entrega, total, usuario_id, direccion_id, restaurante_id)
VALUES ('EN_PROCESO', NOW(), 'RETIRO_POR_LOCAL', 2400, 2, 36, 6);

INSERT INTO productos_pedidos (pedido_id, producto_id, precio_unitario, cantidad)
VALUES (1, 15, 1200, 2);

-- Pedido 2 - Recibido, delivery, dirección del usuario en la misma ciudad
INSERT INTO pedidos (estado, fecha_hora, tipo_entrega, total, usuario_id, direccion_id, restaurante_id)
VALUES ('RECIBIDO', '2025-06-03 20:05:00', 'DELIVERY', 5000, 14, 13, 10);

INSERT INTO productos_pedidos (pedido_id, producto_id, precio_unitario, cantidad)
VALUES (2, 23, 1400, 1);

INSERT INTO productos_pedidos (pedido_id, producto_id, precio_unitario, cantidad)
VALUES (2, 24, 1800, 2);

-- Insertar calificaciones para los restaurantes
INSERT INTO calificaciones_restaurante (comentario, fecha_hora, puntaje, restaurante_id, usuario_id) VALUES
('El mejor restaurante de la ciudad. Siempre vuelvo.', NOW(), 5, 6, 2),
('Excelente comida y muy rápido el delivery', '2025-06-03 20:45:00', 4, 10, 14);