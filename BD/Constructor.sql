-- - - - - - - - - - - - SUCURSALES (Gimnasios y bodegas) - - - - - - - - - - - - 

-- Pueden haber sucursales gimnasios o bodegas :3
CREATE TABLE tipo_sucursal(
    id_tipo SERIAL PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL UNIQUE
);
INSERT INTO tipo_sucursal (tipo) VALUES ('Bodega');
INSERT INTO tipo_sucursal (tipo) VALUES ('Gimnasio');


CREATE TABLE sucursal(
    id_sucursal SERIAL PRIMARY KEY, 
    id_tipo INT REFERENCES tipo_sucursal(id_tipo),
    nombre VARCHAR(100) NOT NULL UNIQUE, 
    direccion VARCHAR(100) NOT NULL UNIQUE, 
    cantidad_maquinas INT
);


-- - - - - - - - - - - - - - EQUIPO E INVENTARIO - - - - - - - - - - - - - - - -

-- tipo de maquinassss
CREATE TABLE equipo (
  id_equipo SERIAL PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  descripcion TEXT
);

-- control de la cantidad de maquinass 
--CREATE TABLE inventario (
--  id_inventario SERIAL PRIMARY KEY,
--  id_equipo INT REFERENCES equipo(id_equipo) ON DELETE CASCADE,
--  id_sucursal INT REFERENCES sucursal(id_sucursal) ON DELETE CASCADE,
--  cantidad INT NOT NULL CHECK (cantidad >= 0)
--); -- No sería mejor que la llave primaria sea la sucursal y el equipo???
CREATE TABLE inventario (
  id_equipo INT REFERENCES equipo(id_equipo) ON DELETE CASCADE,
  id_sucursal INT REFERENCES sucursal(id_sucursal) ON DELETE CASCADE,
  cantidad INT NOT NULL CHECK (cantidad >= 0),
  PRIMARY KEY (id_equipo, id_sucursal)
);


CREATE TABLE transferencia_equipo (
  id_transferencia SERIAL PRIMARY KEY,
  id_equipo INT REFERENCES equipo(id_equipo) ON DELETE CASCADE,
  desde_id_sucursal INT REFERENCES sucursal(id_sucursal) ON DELETE CASCADE,
  hacia_id_sucursal INT REFERENCES sucursal(id_sucursal) ON DELETE CASCADE,
  cantidad INT NOT NULL CHECK (cantidad > 0),
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- - - - - - - - - - - - - - EMPLEADOS - - - - - - - - - - - - - - - -

-- El rol administrador no necesariamente necesita una sucursal? 
-- Asignar sucursal central :3
-- Roles: Recepcionista, Entrenador, Inventario, Administrador
CREATE TABLE rol_empleado(
    id_rol_empleado SERIAL PRIMARY KEY, 
    nombre_rol VARCHAR(100) NOT NULL UNIQUE
);
INSERT INTO rol_empleado (nombre_rol) VALUES ('Administrador');
INSERT INTO rol_empleado (nombre_rol) VALUES ('Inventario');
INSERT INTO rol_empleado (nombre_rol) VALUES ('Recepcionista');
INSERT INTO rol_empleado (nombre_rol) VALUES ('Entrenador');

CREATE TABLE empleado (
    id_empleado SERIAL PRIMARY KEY,
    id_sucursal INT REFERENCES sucursal(id_sucursal) ON DELETE CASCADE,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) UNIQUE,
    telefono INT NOT NULL,
    rol INT REFERENCES rol_empleado(id_rol_empleado),
    usuario VARCHAR(50) UNIQUE,
    passwrd VARCHAR(250)
);


-- - - - - - - - - - - - - - CLIENTES - - - - - - - - - - - - - - - 

CREATE TABLE cliente(
    id_cliente SERIAL PRIMARY KEY, 
    nombre VARCHAR(100) NOT NULL, 
    correo VARCHAR(100) UNIQUE NOT NULL, 
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Membresías
CREATE TABLE tipo_membresia_cliente(
    id_tipo_membresia SERIAL PRIMARY KEY, 
    nombre_membresia VARCHAR(100) NOT NULL UNIQUE,
    porcentaje_descuento INT NOT NULL CHECK (porcentaje_descuento >= 0 AND porcentaje_descuento <= 100)
);
INSERT INTO tipo_membresia_cliente (nombre_membresia, porcentaje_descuento) VALUES ('Basica', 0);
INSERT INTO tipo_membresia_cliente (nombre_membresia, porcentaje_descuento) VALUES ('Premium', 10);
INSERT INTO tipo_membresia_cliente (nombre_membresia, porcentaje_descuento) VALUES ('VIP', 20);

CREATE TABLE membresia_cliente(
    id_membresia_cliente SERIAL PRIMARY KEY, 
    id_cliente INT REFERENCES cliente(id_cliente) ON DELETE CASCADE,
    id_tipo_membresia INT REFERENCES tipo_membresia_cliente(id_tipo_membresia) ON DELETE CASCADE,
    fecha_inicio DATE NOT NULL, 
    fecha_fin DATE NOT NULL
);

-- Asignación entrenador :3
CREATE TABLE asignacion_entrenador( -- Al hacer el insert verificar que el rol del empleado sea entrenador 
    id_cliente INT REFERENCES cliente(id_cliente)ON DELETE CASCADE UNIQUE NOT NULL PRIMARY KEY, -- Porque un cliente solo puede tener un entrenador ,
    id_entrenador INT REFERENCES empleado(id_empleado) ON DELETE CASCADE NOT NULL
);

CREATE TABLE historial_asignaciones_entrenador(
    id_historial_asignaciones_entrenador SERIAL PRIMARY KEY,
    id_cliente INT REFERENCES cliente(id_cliente)ON DELETE CASCADE NOT NULL,  
    id_entrenador INT REFERENCES empleado(id_empleado) ON DELETE CASCADE NOT NULL,
    fecha_asignacion DATE NOT NULL
);

-- Asistencias
CREATE TABLE asistencia(
    id_asistencia SERIAL PRIMARY KEY, 
    id_cliente INT REFERENCES cliente(id_cliente)ON DELETE CASCADE,
    id_sucursal INT REFERENCES sucursal(id_sucursal) ON DELETE CASCADE, -- Al insertar verificar que la sucursal sea un gimnasio y no una bodega
    entrada TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- - - - - - - - - - - - - - RUTINAS - - - - - - - - - - - - - - - -

CREATE TABLE ejercicio(
    id_ejercicio SERIAL PRIMARY KEY, 
    nombre VARCHAR(200) NOT NULL UNIQUE, 
    descripcion TEXT
);

-- Una rutina es una colección de ejercicios
CREATE TABLE rutina(
    id_rutina SERIAL PRIMARY KEY, 
    nombre_rutina VARCHAR(200), 
    descripcion TEXT, 
    id_entrenador_creador INT REFERENCES empleado(id_empleado) ON DELETE CASCADE, -- al insertar verificar que sea un entrenador
    fecha_creacion DATE DEFAULT CURRENT_DATE
); 

CREATE TABLE ejercicio_rutina(
    id_ejercicio_rutina SERIAL PRIMARY KEY, 
    id_ejercicio INT REFERENCES ejercicio(id_ejercicio) ON DELETE CASCADE NOT NULL, 
    id_rutina INT REFERENCES rutina(id_rutina) ON DELETE CASCADE NOT NULL, 
    numero_series INT NOT NULL, 
    repeticiones INT NOT NULL, 
    duracion_estimada INT, 
    equipo_requerido TEXT
);

-- Planes clientes (define las rutinas que tienen los clientes :3)
CREATE TABLE rutina_cliente(
    id_cliente INT REFERENCES cliente(id_cliente) ON DELETE CASCADE,
    id_rutina INT REFERENCES rutina(id_rutina) ON DELETE CASCADE, 
    PRIMARY KEY (id_cliente, id_rutina)
);


-- - - - - - - - - - - - - - PAGOS - - - - - - - - - - - - - - - - -

CREATE TABLE pago (
    id_pago SERIAL PRIMARY KEY,
    id_cliente INT REFERENCES cliente(id_cliente) ON DELETE CASCADE,
    id_membresia INT REFERENCES membresia_cliente(id_membresia_cliente)ON DELETE CASCADE NULL,
    monto DECIMAL(10,2),
    fecha_pago DATE DEFAULT CURRENT_DATE,
    tipo_servicio VARCHAR(50),
    descripcion TEXT
);

