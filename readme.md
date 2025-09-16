## Instalacion de dependencias 
#### 1. PostgreSQL (Version 17.6)
Para Solus usando dpkg: 
```sh 
sudo eokpg install postgresql
sudo eopkg install postgresql-devel
sudo eopkg install postgresql-contrib
```

#### 2. Python 
- El script de insercion usa Python (con psycopg) y `Faker` para poder generar datos de prueba. 
- La version de Python instalada debe ser mayor a 3.6 (a partir de allí se aceptan los strings formateados xd)

##### 1. Faker 
Faker es una herramienta que permite generar muchos tipos de datos de prueba, como nombres, correos, numeros de teléfono, etc. 

Puede instalarse ejecutando:
```sh
pip install faker
```
ó 
```sh
python3 -m pip install faker
```
si faker no se reconoce con una instalacion de pip automática :c

ó 

forzar su instalación ejecutando:

```sh
sudo pip install faker  
```
si todo lo anterior no funcionó :'c.

##### 2. psycopg2-binary
Como driver para postgresql utilizo psycopg2-binary.
Puede instalarse ejecutando: 
```sh
sudo pip install psycopg2-binary
```


#### 3. Java 21 - JDK 21.0.8 
Para el desarrollo de la aplicación de escritorio se utilizó Java como lenguaje de programación con el kit de desarrollo Temurin JDK 21.0.8 y se definió la versión 21 de java para el compilador.








## Ejecucion del sistema 😼

#### 1. PostgreSQL:
1.1 Iniciando el servicio de PostgreSQL: 
```sh
sudo systemctl start postgresql
```

1.2 Bonus: Acceder al CLI de postgres en fit manager:
```sh
psql -U postgres -d fitmanager
```

#### 2. Crear la Base de datos:
La creacion de la base de datos y la insercion de los datos de ejemplo es realizadqa por el script loader.sh

loader.sh se encarga de: 
- elimiar la abse de datos si existe
- crear la base de datos (sin las tablas :3) 
- ejecutar el script de creacion de tablas e inserciones basicas 
- ejecutar un script de python para crear toda la informacion de prueba

Puede ejecutarse con el comando:

```sh
sudo ./loaeder.sh # en el mismo directorio donde se encuenrtra el script
```













## Herramientas que me hacen la vida más fácil :3

#### BeeKeeper Studio
Beekeeper es un cliente SQL para visualizar y editar datos compatible con muchos gestores de bases de datos y sistemas operativos.

Puede ser descargado desde su página principal: https://www.beekeeperstudio.io/

##### Configuracion para la base de datos:
1. Seleccionar `Postgres` como base de datos
2. Configurar la conexión:
Establecer Host como `localhost` en el puerto `5432`
3. Configurar usuario y contraseña
`postgres` como usuario y la contraseña del usuario de postgres ` *vacio*` en mi caso.
4. Definir  `fitmanager` como default database  