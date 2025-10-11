rol
    id_rol
    nombre_rol
    -- comun, moderador, logistica, administrador
    -- solo pueden registrarse usuarios comunes

usuario
    id_usuario
    rol REFERENCES rol(id_rol)
    nombre
    correo
    contrasena 

    activo boolean -- para evitar eliminar usuarios, tambien para ver si un usuario vendedor está sancionado :3

suspension
    id_suspension
    usuario REFERENCES usuario(id_usuario)
    id_moderador REFERENCES usuario(id_usuario) -- moderador que hizo la suspension

    motivo_suspension
    fecha_suspension timestamp
    fecha_fin timestamp
    activa boolean



tarjetas
    numero_tarjeta PK
    titular -- string
    parte_visible -- entero o string con unicamente los ultimos 4 digitos por seguridad :3 
    id_usuario REFERENCES usuario(id_usuario)
    fecha_vencimiento --timestamp??
    -- no guardar el numero de seguridad :3


categoria_producto
    id_categoria
    nombre_categoria
    -- tecnologia, hogar, academico, personal, decoracion, etc

estado_aprobacion_producto
    id_estado_aprobacion
    nombre_estado_aprobacion   
    -- pendiente, aprobado, rechazado

producto
    id_producto
    id_vendedor REFERENCES usuario(id_usuario)
    nombre_producto
    descripcion
    imagen
    precio
    stock -- minimo 1
    estado -- nuevo o usado, esto puede ser un boolean? 
    categoria REFERENCES categoria_producto(id_categoria)

    -- solo por eficiencia para no tener consultas enormes :c
    promedio_calificaciones -- de 1 a 5 estrellas, que sea un double :3
    cantidad_compras --veces que fue comprado o unidades vendidas??
    estado_aprobacion REFERENCES estado_aprobacion_producto(id_estado_aprobacion)

solicitud_producto
    id_solicitud (PK)
    producto REFERENCES producto(id_producto)
    moderador REFERENCES usuario(id_usuario)  -- quien revisó
    fecha_solicitud TIMESTAMP
    fecha_revision TIMESTAMP
    aprobado BOOLEAN
    comentario_moderador TEXT

recaudacion_plataforma
    id_recaudacion
    fecha TIMESTAMP
    descripcion -- descripcion de como se obtuvo este ingreso en la plataforma :3
    monto



estado_pedido
    id_estado_pedido
    nombre_estado -- en curso, entregado

pedido
    id_pedido 
    usuario REFERENCES usuario(id_usuario)
    monto_total
    estado REFERENCES estado_pedido(id_estado_pedido)
    fecha_realizacion timestamp
    fecha_entrega_estimada timestamp
    fecha_entrega_real timestamp


lista_producto_pedido -- lista de productos en un pedido
    id_pedido REFERENCES pedido(id_pedido)
    id_producto REFERENCES producto(id_producto)
    primary key id_pedido, id_producto

    cantidad 
    precio_unitario 
    subtotal

--registro_venta
--    id_registro_venta
--    pedido REFERENCES pedido(id_pedido)
--    usuario REFERENCES usuario(id_usuario)
--    monto total
--
--    tarjeta REFERENCES tarjetas (tarjeta)
--    fecha_realizacion TIMESTAMP
    -- cuando se haga la venta crear la recaudacion_plataforma

detalle_venta_vendedor -- para llevar el control a los vendedores de sus ventas :3
    id_detalle_venta (PK)
    pedido REFERENCES pedido(id_pedido)
    producto REFERENCES producto(id_producto)
    vendedor REFERENCES usuario(id_usuario)
    cantidad INTEGER
    precio_unitario DECIMAL  -- guardar el precio al momento de la venta
    subtotal DECIMAL
    comision_plataforma DECIMAL  -- 5%
    ganancia_vendedor DECIMAL  -- 95%

calificacion_producto

    producto REFERENCES producto(id_producto)
    usuario REFERENCES Usuario(id_usuario)
    pk(usuario, producto)
    
    calificacion -- entero de uno a cinco 
    comentario -- comentario sobre el producto
    fecha TIMESTAMP




notificacion
    id_notificacion
    usuario REFERENCES usuario(id_usuario)

    titulo
    cuerpo_de_la_notificacion
    fecha TIMESTAMP





-- el carrito de compras debe estar en la base de datos??? o se maneja por cookie? :3

carrito
    id_carrito (PK)
    usuario REFERENCES usuario(id_usuario)
    fecha_creacion TIMESTAMP
    fecha_ultima_modificacion TIMESTAMP

detalle_carrito
    id_detalle (PK)
    carrito REFERENCES carrito(id_carrito)
    producto REFERENCES producto(id_producto)
    cantidad INTEGER
    fecha_agregado TIMESTAMP