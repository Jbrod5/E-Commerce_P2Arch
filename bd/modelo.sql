rol
    id_rol
    nombre_rol
    -- comun, moderador, logistica, administrador
    -- solo pueden registrarse usuarios comunes

usuario
    rol REFERENCES rol(id_rol)
    nombre
    correo

    activo boolean -- para evitar eliminar usuarios, tambien para ver si un usuario vendedor está sancionado :3

suspension
    id_suspension
    usuario REFERENCES usuario(correo)

    motivo_suspension
    fecha_suspension timestamp



tarjetas
    tarjeta PK
    id_usuario


categoria_producto
    id_categoria
    nombre_categoria
    -- tecnologia, hogar, academico, personal, decoracion, etc

producto
    id_producto
    nombre_producto
    descripcion
    imagen
    precio
    stock -- minimo 1
    estado -- nuevo o usado, esto puede ser un boolean? 
    categoria REFERENCES categoria_producto(id_categoria)

    promedio_calificaciones -- de 1 a 5 estrellas, que sea un double :3
    cantidad_compras --veces que fue comprado o unidades vendidas??

    aprobado boolean -- un moderador puede aceptarlo o rechazarlo

recaudacion_plataforma
    id_recaudacion
    fecha TIMESTAMP
    descripcion -- descripcion de como se obtuvo este ingreso en la plataforma :3
    monto



estado_pedido
    id_estado_pedido
    estado -- en curso, entregado

pedido
    id_pedido 
    usuario REFERENCES usuario(correo)
    --monto_total
    fecha_realizacion timestamp
    fecha_entrega timestamp


lista_producto_pedido -- lista de productos en un pedido
    id_pedido REFERENCES pedido(id_pedido)
    id_producto REFERENCES producto(id_producto)
    primary key id_pedido, id_producto

    cantidad 

venta
    pedido REFERENCES pedido(id_pedido)
    usuario REFERENCES usuario(correo)
    monto total

    tarjeta REFERENCES tarjetas (tarjeta)
    fecha_realizacion TIMESTAMP
    -- cuando se haga la venta crear la recaudacion_plataforma

calificacion_producto

    producto REFERENCES producto(id_producto)
    usuario REFERENCES Usuario(correo)
    pk(usuario, producto)
    
    calificacion -- entero de uno a cinco 
    comentario -- comentario sobre el producto




notificacion
    id_notificacion
    usuario REFERENCES usuario(correo)

    cuerpo_de_la_notificacion

-- el carrito de compras debe estar en la base de datos??? o se maneja por cookie? :3


