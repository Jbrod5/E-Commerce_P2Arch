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
    rol INTEGER NOT NULL REFERENCES rol(id_rol),
    nombre VARCHAR(150) NOT NULL,
    correo VARCHAR(255) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    activo BOOLEAN DEFAULT TRUE -- para evitar eliminar usuarios, tambien para ver si un usuario vendedor está sancionado :3
);


CREATE TABLE suspension (
    id_suspension SERIAL PRIMARY KEY,
    usuario INTEGER NOT NULL REFERENCES usuario(id_usuario),
    id_moderador INTEGER NOT NULL REFERENCES usuario(id_usuario), -- moderador que hizo la suspension
    motivo_suspension TEXT NOT NULL,
    fecha_suspension TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_fin TIMESTAMP NOT NULL,
    activa BOOLEAN DEFAULT TRUE
);



CREATE TABLE tarjetas (
    id_tarjeta SERIAL PRIMARY KEY,
    numero_tarjeta VARCHAR(50) NOT NULL UNIQUE, -- numero tarjeta completo
    parte_visible VARCHAR(10) NOT NULL, -- string con unicamente los ultimos 4 digitos por seguridad :3 
    titular VARCHAR(200) NOT NULL, -- string
    id_usuario INTEGER NOT NULL REFERENCES usuario(id_usuario),
    mes_vencimiento INTEGER NOT NULL,
    anio_vencimiento INTEGER NOT NULL
    -- no guardar el numero de seguridad :3
);





--  - - - - - - - - - - - - - - - - - - - - - - - - PRODUCTOS - - - - - - - - - - - - - - - - - - - - - - - - 

CREATE TABLE categoria_producto (
    id_categoria SERIAL PRIMARY KEY,
    nombre_categoria VARCHAR(100) NOT NULL
);
-- tecnologia, hogar, academico, personal, decoracion, etc
INSERT INTO categoria_producto(nombre_categoria) VALUES ('tecnologia'),('hogar'),('academico'),('personal'),('decoracion'),('otro');


CREATE TABLE estado_aprobacion_producto (
    id_estado_aprobacion SERIAL PRIMARY KEY,
    nombre_estado_aprobacion VARCHAR(50) NOT NULL
);
-- pendiente, aprobado, rechazado
INSERT INTO estado_aprobacion_producto(nombre_estado_aprobacion) VALUES ('pendiente'), ('aprobado'), ('rechazado');


CREATE TABLE producto (
    id_producto SERIAL PRIMARY KEY,
    id_vendedor INTEGER NOT NULL REFERENCES usuario(id_usuario),
    nombre_producto VARCHAR(200) NOT NULL,
    descripcion TEXT NOT NULL,
    imagen TEXT, -- NULL: puede crearse sin imagen y agregarla después
    precio NUMERIC(12,2) NOT NULL,
    stock INTEGER NOT NULL, -- minimo 1
    producto_nuevo BOOLEAN NOT NULL, -- nuevo o usado, esto puede ser un boolean? 
    categoria INTEGER NOT NULL REFERENCES categoria_producto(id_categoria),
    promedio_calificaciones NUMERIC(3,2) DEFAULT 0, -- de 1 a 5 estrellas, que sea un double :3
    cantidad_compras INTEGER DEFAULT 0, -- veces que fue comprado o unidades vendidas??
    estado_aprobacion INTEGER DEFAULT 1 NOT NULL REFERENCES estado_aprobacion_producto(id_estado_aprobacion) -- DEFAULT 1 = pendiente
);


CREATE TABLE solicitud_producto (
    id_solicitud SERIAL PRIMARY KEY,
    producto INTEGER NOT NULL REFERENCES producto(id_producto),
    moderador INTEGER REFERENCES usuario(id_usuario),  -- NULL: hasta que un moderador tome la solicitud
    fecha_solicitud TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_revision TIMESTAMP, -- NULL: hasta que se revise
    aprobado BOOLEAN, -- NULL: hasta que se tome una decisión
    comentario_moderador TEXT -- NULL: el moderador puede no dejar comentario
);








-- - - - - - - - - - - - - - - - - - - - - - - - VENTAS - - - - - - - - - - - - - - - - - - - - - - - 
CREATE TABLE recaudacion_plataforma (
    id_recaudacion SERIAL PRIMARY KEY,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    descripcion TEXT NOT NULL, -- descripcion de como se obtuvo este ingreso en la plataforma :3
    monto NUMERIC(14,2) NOT NULL
);


CREATE TABLE estado_pedido (
    id_estado_pedido SERIAL PRIMARY KEY,
    nombre_estado VARCHAR(50) NOT NULL
    -- en curso, entregado
);
INSERT INTO estado_pedido(nombre_estado) VALUES ('en curso'), ('enviado'), ('entregado');


CREATE TABLE pedido (
    id_pedido SERIAL PRIMARY KEY,
    usuario INTEGER NOT NULL REFERENCES usuario(id_usuario),
    monto_total NUMERIC(14,2) NOT NULL,
    tarjeta_usada INTEGER NOT NULL REFERENCES tarjetas(id_tarjeta),
    estado INTEGER DEFAULT 1 NOT NULL REFERENCES estado_pedido(id_estado_pedido), -- DEFAULT 1 = en curso

    direccion TEXT NOT NULL,
    
    fecha_realizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_entrega_estimada TIMESTAMP NOT NULL,
    fecha_entrega_real TIMESTAMP -- NULL: hasta que se entregue el pedido
    -- cuando se hace el pedido es pq ya se hizo la venta :3 o sea es tanto para pedido como para venta
);


CREATE TABLE lista_producto_pedido (
    id_pedido INTEGER NOT NULL REFERENCES pedido(id_pedido),
    id_producto INTEGER NOT NULL REFERENCES producto(id_producto),
    cantidad INTEGER NOT NULL,
    precio_unitario NUMERIC(12,2) NOT NULL,
    subtotal NUMERIC(14,2) NOT NULL,
    PRIMARY KEY (id_pedido, id_producto)
);
-- lista de productos en un pedido



CREATE TABLE detalle_venta_vendedor (
    id_detalle_venta SERIAL PRIMARY KEY,
    pedido INTEGER NOT NULL REFERENCES pedido(id_pedido),
    producto INTEGER NOT NULL REFERENCES producto(id_producto),
    vendedor INTEGER NOT NULL REFERENCES usuario(id_usuario),
    comision_plataforma NUMERIC(14,2) NOT NULL, -- 5%
    ganancia_vendedor NUMERIC(14,2) NOT NULL, -- 95%
    cantidad INTEGER NOT NULL,
    precio_unitario NUMERIC(12,2) NOT NULL, -- guardar el precio al momento de la venta
    subtotal NUMERIC(14,2) NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- para llevar el control a los vendedores de sus ventas :3






--  - - - - - - - - - - - - - - - - - - - - - - - - - - - OTROS - - - - - - - - - - - - - - - - - - - - - - - - - - - 
CREATE TABLE calificacion_producto (
    producto INTEGER NOT NULL REFERENCES producto(id_producto),
    usuario INTEGER NOT NULL REFERENCES usuario(id_usuario),
    calificacion INTEGER NOT NULL, -- entero de uno a cinco 
    comentario TEXT, -- NULL: el usuario puede calificar sin comentar
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario, producto)
);


CREATE TABLE notificacion (
    id_notificacion SERIAL PRIMARY KEY,
    usuario INTEGER NOT NULL REFERENCES usuario(id_usuario),
    titulo VARCHAR(200) NOT NULL,
    cuerpo_de_la_notificacion TEXT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    leida BOOLEAN DEFAULT FALSE NOT NULL
);


-- manejarlo en la base de datos para mantenerlo si se inicia sesión otra vez
CREATE TABLE carrito (
    id_carrito SERIAL PRIMARY KEY,
    usuario INTEGER NOT NULL REFERENCES usuario(id_usuario),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Asegura que un usuario solo tenga un carrito abierto (o activo) a la vez
    CONSTRAINT uq_usuario_carrito UNIQUE (usuario)
);


CREATE TABLE detalle_carrito (
    carrito INTEGER NOT NULL REFERENCES carrito(id_carrito),
    producto INTEGER NOT NULL REFERENCES producto(id_producto),


    cantidad INTEGER NOT NULL,


    -- Congela el precio al momento de añadir el producto.
    precio_unitario DECIMAL(10, 2) NOT NULL,

    -- Clave Primaria Compuesta: La combinación de Carrito + Producto debe ser única.
    PRIMARY KEY (carrito, producto)
);