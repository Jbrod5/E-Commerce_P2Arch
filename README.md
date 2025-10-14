# E-Commerce_P2Arch

## Iniciar el servidcio de Postgres
Si el servicio no está configurado para iniciarse al arrancar el sistema operativo, ejecutar:
```sh
sudo systemctl start postgresql
```

## Configurar entorno de Intellij
El backend es desarrollado usando Java y Spring. El proyecto de Spring fue configurado con Spring Initializr.
Unicamente debe cargarse el proyecto de Java en el ide (de preferencia Intellij) y sincronizar las dependencias de maven.
CORS ya está configurado para poder usarse en el puerto por defecto de Vite.

## Configurar entorno de Vue
El frontend es desarrollado utilizando Vue como framework.
Para poder ejecutarlo es necesario entrar a la carpeta del proyecto de vue `e-commerce_frontend` desde una terminal y ejecutar:
```sh
npm install
```
Para ejecutar el proyecto en desarrollo, dentro de la misma carmeta en la terminal ejecutar:
```sh
npm run dev
```

En caso de que se indiquen errores de axios y js-cookie:
1. Entrar en package.json en la raíz del proyecto de Vue 
2. en la seccion dependencies agregar:
```json
    "axios": "^1.6.8", 
    "js-cookie": "^3.0.5"
```
Debería quedar algo así: 
```json
  "dependencies": {
    "pinia": "^3.0.3",
    "vue": "^3.5.22",
    "vue-router": "^4.5.1",
    "axios": "^1.6.8", 
    "js-cookie": "^3.0.5" 
  },
```
3. Reinstalar y ejecutar el servidor
Ejecutar:
```sh
npm install
```
Para instalar las dependencias faltantes y luego ejecutar:
```sh
npm run dev
``` 
para ejecutar el servidor vite para Vue.