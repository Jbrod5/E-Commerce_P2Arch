--  - - - - - - - - - - - - - - - - - - - -USUARIOS - - - - - - - - - - - - - - - - - - - -
CREATE TABLE rol (
    id_rol SERIAL PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL
);
-- comun, moderador, logistica, administrador
-- solo pueden registrarse usuarios comunes
INSERT INTO rol (nombre_rol) VALUES ('comun'), ('moderador'), ('logistica'), ('administrador');


CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    rol INTEGER REFERENCES rol(id_rol),
    nombre VARCHAR(150),
    correo VARCHAR(255) UNIQUE,
    contrasena VARCHAR(255),
    activo BOOLEAN -- para evitar eliminar usuarios, tambien para ver si un usuario vendedor está sancionado :3
);


CREATE TABLE suspension (
    id_suspension SERIAL PRIMARY KEY,
    usuario INTEGER REFERENCES usuario(id_usuario),
    id_moderador INTEGER REFERENCES usuario(id_usuario), -- moderador que hizo la suspension
    motivo_suspension TEXT,
    fecha_suspension TIMESTAMP,
    fecha_fin TIMESTAMP,
    activa BOOLEAN
);



CREATE TABLE tarjetas (
    id_tarjeta SERIAL PRIMARY KEY,
    numero_tarjeta VARCHAR(50) UNIQUE, -- numero tarjeta completo
    parte_visible VARCHAR(10), -- string con unicamente los ultimos 4 digitos por seguridad :3 
    titular VARCHAR(200), -- string
    id_usuario INTEGER REFERENCES usuario(id_usuario),
    mes_vencimiento INTEGER,
    anio_vencimiento INTEGER
    -- no guardar el numero de seguridad :3
);





--  - - - - - - - - - - - - - - - - - - - - - - - - PRODUCTOS - - - - - - - - - - - - - - - - - - - - - - - - 

CREATE TABLE categoria_producto (
    id_categoria SERIAL PRIMARY KEY,
    nombre_categoria VARCHAR(100)
);
-- tecnologia, hogar, academico, personal, decoracion, etc
INSERT INTO categoria_producto(nombre_categoria) VALUES ('tecnologia'),('hogar'),('academico'),('personal'),('decoracion'),('otro');


CREATE TABLE estado_aprobacion_producto (
    id_estado_aprobacion SERIAL PRIMARY KEY,
    nombre_estado_aprobacion VARCHAR(50)
);
-- pendiente, aprobado, rechazado
INSERT INTO estado_aprobacion_producto(nombre_estado_aprobacion) VALUES ('pendiente'), ('aprobado'), ('rechazado');


CREATE TABLE producto (
    id_producto SERIAL PRIMARY KEY,
    id_vendedor INTEGER REFERENCES usuario(id_usuario),
    nombre_producto VARCHAR(200),
    descripcion TEXT,
    imagen TEXT,
    precio NUMERIC(12,2),
    stock INTEGER, -- minimo 1
    producto_nuevo BOOLEAN, -- nuevo o usado, esto puede ser un boolean? 
    categoria INTEGER REFERENCES categoria_producto(id_categoria),
    promedio_calificaciones NUMERIC(3,2), -- de 1 a 5 estrellas, que sea un double :3
    cantidad_compras INTEGER, -- veces que fue comprado o unidades vendidas??
    estado_aprobacion INTEGER REFERENCES estado_aprobacion_producto(id_estado_aprobacion)
);


CREATE TABLE solicitud_producto (
    id_solicitud SERIAL PRIMARY KEY,
    producto INTEGER REFERENCES producto(id_producto),
    moderador INTEGER REFERENCES usuario(id_usuario),  -- quien revisó
    fecha_solicitud TIMESTAMP,
    fecha_revision TIMESTAMP,
    aprobado BOOLEAN,
    comentario_moderador TEXT
);








-- - - - - - - - - - - - - - - - - - - - - - - - VENTAS - - - - - - - - - - - - - - - - - - - - - - - 
CREATE TABLE recaudacion_plataforma (
    id_recaudacion SERIAL PRIMARY KEY,
    fecha TIMESTAMP,
    descripcion TEXT, -- descripcion de como se obtuvo este ingreso en la plataforma :3
    monto NUMERIC(14,2)
);


CREATE TABLE estado_pedido (
    id_estado_pedido SERIAL PRIMARY KEY,
    nombre_estado VARCHAR(50)
    -- en curso, entregado
    INSERT INTO estado_pedido(nombre_estado) VALUES ('en curso'), ('entregado');
);


CREATE TABLE pedido (
    id_pedido SERIAL PRIMARY KEY,
    usuario INTEGER REFERENCES usuario(id_usuario),
    monto_total NUMERIC(14,2),
    tarjeta_usada INTEGER REFERENCES tarjetas(id_tarjeta),
    estado INTEGER REFERENCES estado_pedido(id_estado_pedido),
    fecha_realizacion TIMESTAMP,
    fecha_entrega_estimada TIMESTAMP,
    fecha_entrega_real TIMESTAMP
    -- cuando se hace el pedido es pq ya se hizo la venta :3 o sea es tanto para pedido como para venta
);


CREATE TABLE lista_producto_pedido (
    id_pedido INTEGER REFERENCES pedido(id_pedido),
    id_producto INTEGER REFERENCES producto(id_producto),
    cantidad INTEGER,
    precio_unitario NUMERIC(12,2),
    subtotal NUMERIC(14,2),
    PRIMARY KEY (id_pedido, id_producto)
);
-- lista de productos en un pedido



CREATE TABLE detalle_venta_vendedor (
    id_detalle_venta SERIAL PRIMARY KEY,
    pedido INTEGER REFERENCES pedido(id_pedido),
    producto INTEGER REFERENCES producto(id_producto),
    vendedor INTEGER REFERENCES usuario(id_usuario),
    comision_plataforma NUMERIC(14,2), -- 5%
    ganancia_vendedor NUMERIC(14,2), -- 95%
    cantidad INTEGER,
    precio_unitario NUMERIC(12,2), -- guardar el precio al momento de la venta
    subtotal NUMERIC(14,2),
    fecha TIMESTAMP
);
-- para llevar el control a los vendedores de sus ventas :3






--  - - - - - - - - - - - - - - - - - - - - - - - - - - - OTROS - - - - - - - - - - - - - - - - - - - - - - - - - - - 
CREATE TABLE calificacion_producto (
    producto INTEGER REFERENCES producto(id_producto),
    usuario INTEGER REFERENCES usuario(id_usuario),
    calificacion INTEGER, -- entero de uno a cinco 
    comentario TEXT, -- comentario sobre el producto
    fecha TIMESTAMP,
    PRIMARY KEY (usuario, producto)
);


CREATE TABLE notificacion (
    id_notificacion SERIAL PRIMARY KEY,
    usuario INTEGER REFERENCES usuario(id_usuario),
    titulo VARCHAR(200),
    cuerpo_de_la_notificacion TEXT,
    fecha TIMESTAMP
);


-- el carrito de compras debe estar en la base de datos??? o se maneja por cookie? :3
CREATE TABLE carrito (
    id_carrito SERIAL PRIMARY KEY,
    usuario INTEGER REFERENCES usuario(id_usuario),
    fecha_creacion TIMESTAMP,
    fecha_ultima_modificacion TIMESTAMP
);


CREATE TABLE detalle_carrito (
    id_detalle SERIAL PRIMARY KEY,
    carrito INTEGER REFERENCES carrito(id_carrito),
    producto INTEGER REFERENCES producto(id_producto),
    cantidad INTEGER,
    fecha_agregado TIMESTAMP
);
