--  + + + + + + + + + + + + + + + + + + + + + + + + + + + USUARIOS + + + + + + + + + + + + + + + + + + + + + + + + + + + 

-- 1 administrador
INSERT INTO usuario (rol, nombre, correo, contrasena, activo)
VALUES (4, 'Admin Principal', 'admin@ecommercegt.com', '$2a$10$ZLEjDNg5cNhf/JFJjB6J0ejz3ROepN4N.awtQlUTF5pZ9sEE8R8jW', TRUE);
-- admin123

-- 5 moderadores
INSERT INTO usuario (rol, nombre, correo, contrasena, activo) VALUES
(2, 'Moderador 1', 'mod1@ecommercegt.com', '$2a$10$9e6HTOc4c/k6Sh6PLgqVGeuVt.mBscs0ZvhDPOOfSlFTlL61U67KG', TRUE),
(2, 'Moderador 2', 'mod2@ecommercegt.com', '$2a$10$9e6HTOc4c/k6Sh6PLgqVGeuVt.mBscs0ZvhDPOOfSlFTlL61U67KG', TRUE),
(2, 'Moderador 3', 'mod3@ecommercegt.com', '$2a$10$9e6HTOc4c/k6Sh6PLgqVGeuVt.mBscs0ZvhDPOOfSlFTlL61U67KG', TRUE),
(2, 'Moderador 4', 'mod4@ecommercegt.com', '$2a$10$9e6HTOc4c/k6Sh6PLgqVGeuVt.mBscs0ZvhDPOOfSlFTlL61U67KG', TRUE),
(2, 'Moderador 5', 'mod5@ecommercegt.com', '$2a$10$9e6HTOc4c/k6Sh6PLgqVGeuVt.mBscs0ZvhDPOOfSlFTlL61U67KG', TRUE);
-- mod123

-- 3 logistica
INSERT INTO usuario (rol, nombre, correo, contrasena, activo) VALUES
(3, 'Logistica 1', 'log1@ecommercegt.com', '$2a$10$dW7AzEMtwFA/rs8YMfkR/uOCdjTinE3AK0VXnDugZYMKHNrCgnYU2', TRUE),
(3, 'Logistica 2', 'log2@ecommercegt.com', '$2a$10$dW7AzEMtwFA/rs8YMfkR/uOCdjTinE3AK0VXnDugZYMKHNrCgnYU2', TRUE),
(3, 'Logistica 3', 'log3@ecommercegt.com', '$2a$10$dW7AzEMtwFA/rs8YMfkR/uOCdjTinE3AK0VXnDugZYMKHNrCgnYU2', TRUE);
-- log123

-- 10 usuarios comunes
INSERT INTO usuario (rol, nombre, correo, contrasena, activo) VALUES
(1, 'Juan Perez'        , 'juanperez@correo.com'        , '$2a$10$o7IoAyJG64s8kvVUZdZRTO0kknL1XDz5G0AkgLyV9Q9CuOxtrnvni', TRUE),
(1, 'Maria Gomez'       , 'mariagomez@correo.com'       , '$2a$10$o7IoAyJG64s8kvVUZdZRTO0kknL1XDz5G0AkgLyV9Q9CuOxtrnvni', TRUE),
(1, 'Carlos Hernandez'  , 'carloshernandez@correo.com'  , '$2a$10$o7IoAyJG64s8kvVUZdZRTO0kknL1XDz5G0AkgLyV9Q9CuOxtrnvni', TRUE),
(1, 'Lucia Fernandez'   ,  'luciafernandez@correo.com'  , '$2a$10$o7IoAyJG64s8kvVUZdZRTO0kknL1XDz5G0AkgLyV9Q9CuOxtrnvni', TRUE),
(1, 'Jose Ramirez'      , 'joseramirez@correo.com'      , '$2a$10$o7IoAyJG64s8kvVUZdZRTO0kknL1XDz5G0AkgLyV9Q9CuOxtrnvni', TRUE),
(1, 'Ana Torres'        , 'anatorres@correo.com'        , '$2a$10$o7IoAyJG64s8kvVUZdZRTO0kknL1XDz5G0AkgLyV9Q9CuOxtrnvni', TRUE),
(1, 'Pedro Lopez'       , 'pedrolopez@correo.com'       , '$2a$10$o7IoAyJG64s8kvVUZdZRTO0kknL1XDz5G0AkgLyV9Q9CuOxtrnvni', TRUE),
(1, 'Sofia Morales'     , 'sofiamorales@correo.com'     , '$2a$10$o7IoAyJG64s8kvVUZdZRTO0kknL1XDz5G0AkgLyV9Q9CuOxtrnvni', TRUE),
(1, 'Luis Castillo'     , 'luiscastillo@correo.com'     , '$2a$10$o7IoAyJG64s8kvVUZdZRTO0kknL1XDz5G0AkgLyV9Q9CuOxtrnvni', TRUE),
(1, 'Daniela Ruiz'      , 'danielaruiz@correo.com'      , '$2a$10$o7IoAyJG64s8kvVUZdZRTO0kknL1XDz5G0AkgLyV9Q9CuOxtrnvni', TRUE);
-- user123


-- TARJETAS DE USUARIOS COMUNES

INSERT INTO tarjetas (numero_tarjeta, parte_visible, titular, id_usuario, mes_vencimiento, anio_vencimiento)
VALUES
('4111111111111111', '1111', 'Juan Perez', 10, 12, 2028),
('4000000000000002', '0002', 'Maria Gomez', 11, 5, 2027),
('5555555555554444', '5444', 'Carlos Hernandez', 12, 11, 2026),
('378282246310005', '0005', 'Lucia Fernandez', 13, 9, 2029);








--  + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + PRODUCTOS DE USUARIOS COMUNES + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + 

-- PRODUCTOS DE JUAN PEREZ (id_usuario = 10)
INSERT INTO producto (id_vendedor, nombre_producto, descripcion, imagen, precio, stock, producto_nuevo, categoria, promedio_calificaciones, cantidad_compras, estado_aprobacion)
VALUES
(10, 'Mouse Inalambrico'    , 'Mouse ergonomico con sensor optico de alta precision', 'mouse.jpg', 150.00, 15, TRUE, 1, 4.5, 12, 2),       -- Tecnología
(10, 'Teclado Mecanico RGB' , 'Teclado con switches azules y retroiluminacion', 'teclado.jpg', 350.00, 10, TRUE, 1, 4.8, 25, 2),       -- Tecnología
(10, 'Audifonos Bluetooth'  , 'Audifonos con cancelacion de ruido y microfono integrado', 'audifonos.jpg', 200.00, 20, TRUE, 1, 4.6, 18, 2), -- Tecnología
(10, 'Webcam HD'            , 'Camara web 1080p con microfono', 'webcam.jpg', 250.00, 8, TRUE, 1, 4.3, 8, 2),                                   -- Tecnología
(10, 'Parlante portatil'    , 'Bluetooth, bateria recargable', 'parlante.jpg', 180.00, 10, TRUE, 1, 4.4, 15, 2),                        -- Tecnología
(10, 'Lampara LED'          , 'Lampara de escritorio con luz blanca', 'lampara.jpg', 120.00, 5, TRUE, 2, 4.1, 4, 2),                          -- Hogar
(10, 'Taza decorativa'      , 'Taza de ceramica con diseno personalizado', 'taza.jpg', 80.00, 30, TRUE, 5, 4.7, 10, 2),                     -- Decoración
(10, 'Cuaderno de notas'    , 'Tapa dura, 100 hojas', 'cuaderno.jpg', 60.00, 25, TRUE, 3, 4.5, 9, 2),                                   -- Papelería
(10, 'Camisa de algodon'    , 'Camisa blanca unisex', 'camisa.jpg', 90.00, 18, TRUE, 4, 4.0, 3, 2),                                     -- Ropa
(10, 'Set de cables USB'    , 'Incluye cables tipo C y microUSB', 'cables.jpg', 70.00, 40, TRUE, 1, 4.2, 6, 2);                            -- Tecnología


-- PRODUCTOS DE MARIA GOMEZ (id_usuario = 11)
INSERT INTO producto (id_vendedor, nombre_producto, descripcion, imagen, precio, stock, producto_nuevo, categoria, promedio_calificaciones, cantidad_compras, estado_aprobacion)
VALUES
(11, 'Cojin decorativo', 'Cojin con diseno floral', 'cojin.jpg', 110.00, 12, TRUE, 5, 4.5, 14, 2),          -- Decoración
(11, 'Organizador de escritorio', 'Madera, con divisiones multiples', 'organizador.jpg', 150.00, 9, TRUE, 2, 4.7, 10, 2), -- Hogar
(11, 'Bolso de cuero', 'Bolso grande color cafe', 'bolso.jpg', 400.00, 5, TRUE, 4, 4.9, 20, 2),            -- Moda
(11, 'Gorra deportiva', 'Material transpirable', 'gorra.jpg', 85.00, 15, TRUE, 4, 4.3, 7, 2),             -- Moda
(11, 'Alfombra pequena', 'Ideal para dormitorio o sala', 'alfombra.jpg', 300.00, 7, TRUE, 2, 4.8, 8, 2), -- Hogar
(11, 'Tetera electrica', 'Capacidad de 1.7L con apagado automatico', 'tetera.jpg', 280.00, 6, TRUE, 2, 4.6, 12, 2), -- Hogar
(11, 'Reloj de pared', 'Diseno minimalista', 'reloj.jpg', 180.00, 9, TRUE, 5, 4.2, 4, 2),                 -- Decoración
(11, 'Cuadro decorativo', 'Pintura abstracta moderna', 'cuadro.jpg', 500.00, 3, TRUE, 5, 4.9, 3, 2),       -- Decoración
(11, 'Libreta artistica', 'Papel especial para acuarela', 'libreta.jpg', 95.00, 15, TRUE, 3, 4.5, 6, 2),   -- Papelería
(11, 'Portarretratos', 'Madera clara, tamano 5x7"', 'portarretratos.jpg', 70.00, 20, TRUE, 5, 4.3, 5, 2); -- Decoración


-- PRODUCTOS DE CARLOS HERNANDEZ (id_usuario = 12)
INSERT INTO producto (id_vendedor, nombre_producto, descripcion, imagen, precio, stock, producto_nuevo, categoria, promedio_calificaciones, cantidad_compras, estado_aprobacion)
VALUES
(12, 'Smartwatch Deportivo', 'Reloj inteligente con monitor de ritmo cardiaco y GPS', 'smartwatch.jpg', 600.00, 12, TRUE, 1, 4.7, 15, 2), -- Tecnología
(12, 'Power Bank 20000mAh', 'Cargador portatil de alta capacidad con salida rapida', 'powerbank.jpg', 250.00, 20, TRUE, 1, 4.5, 22, 2), -- Tecnología
(12, 'Camara de seguridad WiFi', 'Camara IP con vision nocturna y deteccion de movimiento', 'camara_seguridad.jpg', 480.00, 8, TRUE, 1, 4.6, 14, 2), -- Tecnología
(12, 'Soporte para laptop', 'Ajustable en altura y antideslizante', 'soporte_laptop.jpg', 180.00, 18, TRUE, 2, 4.3, 9, 2), -- Hogar
(12, 'Ventilador portatil USB', 'Ventilador silencioso recargable', 'ventilador.jpg', 90.00, 25, TRUE, 2, 4.4, 11, 2), -- Hogar
(12, 'Cable HDMI 4K', 'Cable trenzado de 2 metros compatible con 4K', 'hdmi.jpg', 75.00, 40, TRUE, 1, 4.8, 30, 2), -- Tecnología
(12, 'Mousepad RGB', 'Alfombrilla con luces LED personalizables', 'mousepad.jpg', 130.00, 15, TRUE, 5, 4.6, 8, 2), -- Decoración
(12, 'Altavoz de escritorio', 'Altavoces estereo compactos con excelente sonido', 'altavoz.jpg', 220.00, 10, TRUE, 2, 4.5, 6, 2), -- Hogar
(12, 'Disco SSD 500GB', 'Unidad de estado solido con velocidad de lectura 550MB/s', 'ssd.jpg', 520.00, 7, TRUE, 1, 4.9, 19, 2), -- Tecnología
(12, 'Memoria USB 128GB', 'Pendrive de alta velocidad y diseno metalico', 'usb.jpg', 150.00, 30, TRUE, 1, 4.7, 25, 2); -- Tecnología

-- PRODUCTOS DE LUCIA FERNANDEZ (id_usuario = 13)
INSERT INTO producto (id_vendedor, nombre_producto, descripcion, imagen, precio, stock, producto_nuevo, categoria, promedio_calificaciones, cantidad_compras, estado_aprobacion)
VALUES
(13, 'Collar artesanal', 'Collar hecho a mano con cuentas de madera natural', 'collar.jpg', 120.00, 25, TRUE, 4, 4.9, 12, 2),             -- Moda
(13, 'Pulsera tejida', 'Pulsera de hilo multicolor ajustable', 'pulsera.jpg', 80.00, 40, TRUE, 4, 4.8, 15, 2),                             -- Moda
(13, 'Cuadro floral', 'Pintura al oleo de flores silvestres', 'cuadro_floral.jpg', 450.00, 3, TRUE, 5, 4.9, 6, 2),                           -- Decoración
(13, 'Difusor de aroma', 'Difusor ultrasónico con luz ambiental', 'difusor.jpg', 230.00, 10, TRUE, 2, 4.7, 10, 2),                             -- Hogar
(13, 'Taza ilustrada', 'Taza de ceramica con ilustracion original', 'taza_ilustrada.jpg', 90.00, 20, TRUE, 5, 4.6, 8, 2),                      -- Decoración
(13, 'Cojin bordado', 'Cojin decorativo con bordado artesanal', 'cojin_bordado.jpg', 150.00, 10, TRUE, 5, 4.8, 5, 2),                           -- Decoración
(13, 'Bolsa de tela reutilizable', 'Bolsa ecologica con diseno artistico', 'bolsa_tela.jpg', 110.00, 25, TRUE, 4, 4.7, 9, 2),                     -- Moda
(13, 'Agenda 2025', 'Agenda artesanal con portada de cuero', 'agenda.jpg', 190.00, 8, TRUE, 3, 4.9, 7, 2),                                    -- Papelería
(13, 'Llavero personalizado', 'Llavero de madera grabado con nombre', 'llavero.jpg', 70.00, 50, TRUE, 4, 4.5, 20, 2),                            -- Moda
(13, 'Marco de fotos vintage', 'Marco de madera envejecida 10x15cm', 'marco_vintage.jpg', 130.00, 15, TRUE, 5, 4.6, 11, 2);                      -- Decoración


-- PRODUCTOS DE JOSE RAMIREZ (id_usuario = 14)
INSERT INTO producto (id_vendedor, nombre_producto, descripcion, imagen, precio, stock, producto_nuevo, categoria, promedio_calificaciones, cantidad_compras, estado_aprobacion)
VALUES
(14, 'Silla ergonómica', 'Silla de oficina ajustable con soporte lumbar', 'silla.jpg', 950.00, 5, TRUE, 2, 4.8, 10, 2),                       -- Hogar
(14, 'Escritorio moderno', 'Escritorio de madera con acabado mate', 'escritorio.jpg', 1200.00, 3, TRUE, 2, 4.7, 8, 2),                         -- Hogar
(14, 'Lámpara de pie', 'Lámpara alta con luz cálida regulable', 'lampara_pie.jpg', 400.00, 7, TRUE, 2, 4.6, 5, 2),                               -- Hogar
(14, 'Reloj digital', 'Reloj de pared LED con temperatura y fecha', 'reloj_digital.jpg', 300.00, 10, TRUE, 5, 4.5, 9, 2),                        -- Decoración
(14, 'Cortinas blackout', 'Juego de cortinas opacas color gris', 'cortinas.jpg', 350.00, 6, TRUE, 2, 4.4, 7, 2),                                 -- Hogar
(14, 'Organizador de cables', 'Kit para ordenar cables y adaptadores', 'organizador_cables.jpg', 80.00, 25, TRUE, 1, 4.3, 11, 2),                 -- Tecnología
(14, 'Almohada viscoelástica', 'Almohada ergonómica con espuma de memoria', 'almohada.jpg', 250.00, 15, TRUE, 2, 4.8, 14, 2),                     -- Hogar
(14, 'Silla gamer', 'Silla reclinable con cojines de soporte lumbar', 'silla_gamer.jpg', 1300.00, 4, TRUE, 2, 4.9, 12, 2),                         -- Hogar
(14, 'Ventilador de torre', 'Ventilador silencioso con control remoto', 'ventilador_torre.jpg', 500.00, 6, TRUE, 2, 4.7, 10, 2),                   -- Hogar
(14, 'Luz LED decorativa', 'Tira LED de 5 metros con control remoto', 'tira_led.jpg', 150.00, 20, TRUE, 5, 4.6, 18, 2);                            -- Decoración


-- PRODUCTOS DE ANA TORRES (id_usuario = 15)
INSERT INTO producto (id_vendedor, nombre_producto, descripcion, imagen, precio, stock, producto_nuevo, categoria, promedio_calificaciones, cantidad_compras, estado_aprobacion)
VALUES
(15, 'Set de pinceles', 'Set de 12 pinceles de diferentes tamaños', 'pinceles.jpg', 180.00, 20, TRUE, 3, 4.8, 10, 2),                              -- Papelería
(15, 'Lienzo para pintura', 'Lienzo de algodón 40x50cm', 'lienzo.jpg', 130.00, 15, TRUE, 3, 4.7, 6, 2),                                            -- Papelería
(15, 'Pinturas acrílicas', 'Juego de 24 colores vivos', 'pinturas.jpg', 250.00, 10, TRUE, 3, 4.9, 12, 2),                                         -- Papelería
(15, 'Caballeta de madera', 'Caballeta plegable para artistas', 'caballeta.jpg', 400.00, 8, TRUE, 3, 4.8, 7, 2),                                    -- Papelería
(15, 'Bloc de dibujo', 'Bloc tamaño A4 con papel de 120g', 'bloc.jpg', 110.00, 25, TRUE, 3, 4.6, 9, 2),                                           -- Papelería
(15, 'Paleta para mezclar pintura', 'Paleta de plástico con compartimientos', 'paleta.jpg', 70.00, 30, TRUE, 3, 4.5, 11, 2),                         -- Papelería
(15, 'Bolso para materiales de arte', 'Bolso amplio y resistente', 'bolso_arte.jpg', 300.00, 6, TRUE, 4, 4.8, 5, 2),                                -- Moda
(15, 'Rotuladores de arte', 'Set de 36 rotuladores doble punta', 'rotuladores.jpg', 380.00, 12, TRUE, 3, 4.7, 13, 2),                                 -- Papelería
(15, 'Cinta adhesiva para acuarela', 'Evita filtraciones al pintar', 'cinta_acuarela.jpg', 60.00, 40, TRUE, 3, 4.4, 10, 2),                            -- Papelería
(15, 'Papel para acuarela', 'Papel grueso de alta calidad', 'papel_acuarela.jpg', 150.00, 15, TRUE, 3, 4.9, 8, 2);                                     -- Papelería

-- PRODUCTOS DE USUARIO 16
INSERT INTO producto (id_vendedor, nombre_producto, descripcion, imagen, precio, stock, producto_nuevo, categoria, promedio_calificaciones, cantidad_compras, estado_aprobacion)
VALUES
(16, 'Laptop Gamer', 'Portatil con procesador i7 y tarjeta grafica RTX 3060', 'laptop_gamer.jpg', 1500.00, 5, TRUE, 1, 4.8, 10, 2),
(16, 'Silla de oficina', 'Silla ergonómica con soporte lumbar', 'silla_oficina.jpg', 450.00, 7, TRUE, 2, 4.6, 6, 2),
(16, 'Libro Programacion Python', 'Libro para aprender Python desde cero', 'libro_python.jpg', 120.00, 15, TRUE, 3, 4.9, 8, 2),
(16, 'Auriculares Inalambricos', 'Bluetooth con cancelacion de ruido', 'auriculares.jpg', 200.00, 12, TRUE, 1, 4.7, 9, 2),
(16, 'Mochila Deportiva', 'Resistente al agua con multiples compartimientos', 'mochila.jpg', 180.00, 10, TRUE, 4, 4.5, 5, 2),
(16, 'Cuadro Paisaje', 'Pintura al oleo de paisaje natural', 'cuadro_paisaje.jpg', 350.00, 4, TRUE, 5, 4.8, 3, 2),
(16, 'Licuadora', 'Licuadora de vidrio 1.5L con base potente', 'licuadora.jpg', 220.00, 8, TRUE, 2, 4.6, 7, 2),
(16, 'Pulsera Inteligente', 'Monitorea pasos y frecuencia cardiaca', 'pulsera.jpg', 150.00, 20, TRUE, 1, 4.7, 12, 2),
(16, 'Set de Lapices', 'Lapices de colores profesionales 24 unidades', 'lapices.jpg', 80.00, 25, TRUE, 3, 4.9, 15, 2),
(16, 'Velas Aromaticas', 'Juego de 3 velas con fragancias variadas', 'velas.jpg', 90.00, 18, TRUE, 5, 4.5, 6, 2);

-- PRODUCTOS DE USUARIO 17
INSERT INTO producto (id_vendedor, nombre_producto, descripcion, imagen, precio, stock, producto_nuevo, categoria, promedio_calificaciones, cantidad_compras, estado_aprobacion)
VALUES
(17, 'Tablet 10 pulgadas', 'Tablet con Android 12 y 64GB almacenamiento', 'tablet.jpg', 800.00, 6, TRUE, 1, 4.6, 9, 2),
(17, 'Escritorio Ajustable', 'Escritorio de altura regulable de madera', 'escritorio.jpg', 650.00, 5, TRUE, 2, 4.8, 7, 2),
(17, 'Libro Historia Universal', 'Libro completo sobre historia mundial', 'libro_historia.jpg', 180.00, 12, TRUE, 3, 4.7, 8, 2),
(17, 'Smartwatch', 'Reloj inteligente con GPS y monitoreo de sueño', 'smartwatch.jpg', 300.00, 15, TRUE, 1, 4.5, 10, 2),
(17, 'Bolso de Cuero', 'Bolso grande de cuero sintetico', 'bolso_cuero.jpg', 350.00, 8, TRUE, 4, 4.6, 5, 2),
(17, 'Cuadro Moderno', 'Pintura abstracta moderna sobre lienzo', 'cuadro_moderno.jpg', 400.00, 3, TRUE, 5, 4.9, 4, 2),
(17, 'Licuadora Personal', 'Licuadora compacta de 500W', 'licuadora_personal.jpg', 180.00, 10, TRUE, 2, 4.5, 7, 2),
(17, 'Set de Maquillaje', 'Paleta completa con sombras y rubor', 'maquillaje.jpg', 250.00, 12, TRUE, 4, 4.7, 6, 2),
(17, 'Bloc de Notas', 'Bloc de 100 hojas con tapa dura', 'bloc_notas.jpg', 70.00, 20, TRUE, 3, 4.8, 10, 2),
(17, 'Difusor de Aromas', 'Difusor ultrasónico con luz LED', 'difusor_aromas.jpg', 150.00, 14, TRUE, 5, 4.6, 9, 2);

-- PRODUCTOS DE USUARIO 18
INSERT INTO producto (id_vendedor, nombre_producto, descripcion, imagen, precio, stock, producto_nuevo, categoria, promedio_calificaciones, cantidad_compras, estado_aprobacion)
VALUES
(18, 'Smartphone 128GB', 'Telefono con camara triple y pantalla OLED', 'smartphone.jpg', 1200.00, 7, TRUE, 1, 4.9, 12, 2),
(18, 'Lampara LED de Mesa', 'Lampara con intensidad regulable', 'lampara_led.jpg', 150.00, 10, TRUE, 2, 4.6, 8, 2),
(18, 'Libro Cocina Saludable', 'Recetario de platos saludables', 'libro_cocina.jpg', 100.00, 15, TRUE, 3, 4.7, 9, 2),
(18, 'Auriculares Gamer', 'Audifonos con microfono y sonido 7.1', 'auriculares_gamer.jpg', 350.00, 9, TRUE, 1, 4.8, 10, 2),
(18, 'Mochila Escolar', 'Mochila resistente con varios compartimientos', 'mochila_escolar.jpg', 120.00, 14, TRUE, 4, 4.5, 6, 2),
(18, 'Cuadro Decorativo', 'Lienzo con ilustracion artistica', 'cuadro_decorativo.jpg', 300.00, 5, TRUE, 5, 4.8, 4, 2),
(18, 'Batidora de Mano', 'Batidora ligera y compacta 300W', 'batidora.jpg', 200.00, 12, TRUE, 2, 4.7, 7, 2),
(18, 'Perfume Mujer', 'Fragancia floral de larga duracion', 'perfume.jpg', 180.00, 10, TRUE, 4, 4.6, 5, 2),
(18, 'Cuaderno Universitario', '100 hojas, tapa dura', 'cuaderno_uni.jpg', 80.00, 20, TRUE, 3, 4.8, 8, 2),
(18, 'Velas Decorativas', 'Set de 4 velas aromáticas', 'velas_decorativas.jpg', 90.00, 15, TRUE, 5, 4.5, 6, 2);

-- PRODUCTOS DE USUARIO 19
INSERT INTO producto (id_vendedor, nombre_producto, descripcion, imagen, precio, stock, producto_nuevo, categoria, promedio_calificaciones, cantidad_compras, estado_aprobacion)
VALUES
(19, 'Monitor 27 pulgadas', 'Monitor LED Full HD con altavoces integrados', 'monitor.jpg', 700.00, 6, TRUE, 1, 4.7, 8, 2),
(19, 'Silla Gamer', 'Silla reclinable con cojines ajustables', 'silla_gamer.jpg', 1300.00, 4, TRUE, 2, 4.9, 5, 2),
(19, 'Libro Desarrollo Web', 'Aprende HTML, CSS y JavaScript', 'libro_web.jpg', 150.00, 10, TRUE, 3, 4.8, 9, 2),
(19, 'Teclado Inalambrico', 'Teclado compacto y silencioso', 'teclado_inalambrico.jpg', 220.00, 12, TRUE, 1, 4.6, 7, 2),
(19, 'Bolso Mujer', 'Bolso elegante de cuero sintético', 'bolso_mujer.jpg', 320.00, 8, TRUE, 4, 4.7, 6, 2),
(19, 'Cuadro Abstracto', 'Lienzo con pintura abstracta moderna', 'cuadro_abstracto.jpg', 400.00, 3, TRUE, 5, 4.9, 4, 2),
(19, 'Licuadora Profesional', 'Licuadora de 1000W con vaso de vidrio', 'licuadora_profesional.jpg', 450.00, 5, TRUE, 2, 4.8, 7, 2),
(19, 'Set de Maquillaje', 'Paleta completa para maquillaje profesional', 'set_maquillaje.jpg', 280.00, 10, TRUE, 4, 4.6, 6, 2),
(19, 'Bloc de Dibujo', 'Bloc tamaño A4 con 120 hojas', 'bloc_dibujo.jpg', 90.00, 20, TRUE, 3, 4.7, 8, 2),
(19, 'Velas Aromaticas', 'Set de 3 velas con fragancias variadas', 'velas_aromaticas.jpg', 75.00, 18, TRUE, 5, 4.5, 5, 2);












-- + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + PRUEBAS + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +
-- PEDIDOS DE PRUEBA
INSERT INTO pedido (usuario, monto_total, tarjeta_usada, estado, fecha_realizacion, fecha_entrega_estimada, fecha_entrega_real)
VALUES
(10, 500.00, 1, (SELECT id_estado_pedido FROM estado_pedido WHERE nombre_estado = 'en curso'), NOW(), NOW() + INTERVAL '5 days', NULL),
(11, 750.00, 2, (SELECT id_estado_pedido FROM estado_pedido WHERE nombre_estado = 'entregado'), NOW() - INTERVAL '7 days', NOW() - INTERVAL '2 days', NOW() - INTERVAL '1 day');

INSERT INTO lista_producto_pedido (id_pedido, id_producto, cantidad, precio_unitario, subtotal)
VALUES
(1, 1, 2, 150.00, 300.00),
(1, 2, 1, 200.00, 200.00),
(2, 11, 1, 110.00, 110.00),
(2, 12, 2, 150.00, 300.00),
(2, 13, 1, 400.00, 400.00);



-- CALIFICACIONES DE PRODUCTOS
INSERT INTO calificacion_producto (producto, usuario, calificacion, comentario, fecha)
VALUES
(1, 11, 5, 'Excelente calidad y buen envio.', NOW()),
(2, 11, 4, 'Muy buen teclado, aunque algo ruidoso.', NOW()),
(11, 10, 5, 'Muy suave y bonito diseno.', NOW()),
(12, 10, 5, 'Excelente organizador, lo recomiendo.', NOW());




-- NOTIFICACIONES DE PRUEBA
INSERT INTO notificacion (usuario, titulo, cuerpo_de_la_notificacion, fecha)
VALUES
(10, 'Pedido confirmado', 'Tu pedido #1 ha sido recibido y esta en curso.', NOW()),
(11, 'Pedido entregado', 'Tu pedido #2 ha sido entregado exitosamente.', NOW()),
(10, 'Producto aprobado', 'Tu producto "Mouse Inalambrico" ha sido aprobado por el moderador.', NOW()),
(11, 'Producto aprobado', 'Tu producto "Cojin decorativo" ha sido aprobado por el moderador.', NOW());
