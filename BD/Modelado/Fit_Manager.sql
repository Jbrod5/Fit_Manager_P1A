
-- - - - - - - - - - - - - SUCURSALES (Gimnasios y bodegas) - - - - - - - - - - - -


-- Pueden haber sucursales gimnasios o bodegas :3
CREATE TABLE tipo_sucursal(
    id_tipo INTEGER PRIMARY KEY,
    tipo STRING NOT NULL UNIQUE
);

INSERT INTO tipo_sucursal(tipo) VALUES ("Bodega");
INSERT INTO tipo_sucursal(tipo) VALUES ("Gimnasio");

CREATE TABLE sucursal(
    id_sucursal INTEGER PRIMARY KEY, 
    tipo_sucursal INTEGER FOREIGN KEY, 
    nombre VARCHAR(100) NOT NULL UNIQUE, 
    direccion VARCHAR(100) NOT NULL UNIQUE, 
    cantidad_maquinas INTEGER
);


-- - - - - - - - - - - - - - - - EQUIPO E INVENTARIO - - - - - - - - - - - - - - -
-- tipo de maquinassss
CREATE TABLE equipo (
  id_equipo SERIAL PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  descripcion TEXT
);

-- control de la cantidad de maquinass 
CREATE TABLE inventario (
  id_inventario SERIAL PRIMARY KEY,
  id_equipo INT REFERENCES equipo(id_equipo) ON DELETE CASCADE,
  id_sucursal INT REFERENCES sucursal(id_sucursal) ON DELETE CASCADE,
  cantidad INT NOT NULL CHECK (cantidad >= 0)
);

CREATE TABLE transferencia_equipo (
  id_transferencia SERIAL PRIMARY KEY,
  id_equipo INT REFERENCES equipo(id_equipo),
  desde_id_sucursal INT REFERENCES sucursal(id_sucursal),
  hacia_id_sucursal INT REFERENCES sucursal(id_sucursal),
  cantidad INT NOT NULL CHECK (cantidad > 0),
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);




-- - - - - - - - - - - - - - - - - - EMPLEADOS - - - - - - - - - - - - - - - - -
-- El rol administrador no necesariamente necesita una sucursal? 
-- Asignar sucursar centrral :3
-- Roles: Recepcionista, Entrenador, Inventario, Administrador
CREATE TABLE rol_empleado(
    id_rol_empleado INTEGER PRIMARY KEY, 
    nombre_rol VARCHAR(100) NOT NULL UNIQUE
);
INSERT INTO rol_empleado(nombre_rol) VALUES ("Administrador");
INSERT INTO rol_empleado(nombre_rol) VALUES ("Inventario");
INSERT INTO rol_empleado(nombre_rol) VALUES ("Recepcionista");
INSERT INTO rol_empleado(nombre_rol) VALUES ("Entrenador");



CREATE TABLE empleado (
    id_empleado SERIAL PRIMARY KEY,
    id_sucursal INTEGER REFERENCES sucursal(id_sucursal),
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) UNIQUE,
    telefono VARCHAR(15),
    rol INTEGER REFERENCES rol_empleado(id_rol_empleado),
    usuario VARCHAR(50) UNIQUE,
    passwrd VARCHAR(100)
);



-- - - - - - - - - - - - - - - - - - - - CLIENTES- - - - - - - - - - - - - - - - - - - 


CREATE TABLE cliente(
    id_cliente INTEGER PRIMARY KEY, 
    nombre VARCHAR (100) NOT NULL, 
    correo VARCHAR (100) NOT NUL UNIQUE, 
    fecha_registro TIMESTAMP
);

-- membresias
CREATE TABLE tipo_membresia_cliente(
    id_tipo_membresia INTEGER PRIMARY KEY, 
    nombre_membresia VARCHAR(100) NOT NULL UNIQUE,
    porcentaje_desuento INTEGER NOT NULL, -- Indica el porcentajee: 10 = 10%
);
INSERT INTO tipo_membresia_cliente(nombre_membresia, porcentaje_desuento) VALUES ("Basica",0);
INSERT INTO tipo_membresia_cliente(nombre_membresia, porcentaje_desuento) VALUES ("Premium",10);
INSERT INTO tipo_membresia_cliente(nombre_membresia, porcentaje_desuento) VALUES ("VIP",20);

CREATE TABLE membresia_cliente(
    id_membresia_cliente INTEGER PRIMARY KEY, 
    id_cliente INTEGER REFERENCES cliente(id_cliente),
    id_tipo_membresia INTEGER REFERENCES tipo_membresia_cliente(id_tipo_membresia),
    fecha_inicio DATE NOT NULL, 
    fecha_fin DATE NOT NULL
);

-- Asignacion entrenador :3
CREATE TABLE asignacion_entrenador( -- Al hacer el insert verificar que el rol del empleado sea entrenador 
    id_asignacion INTEGER PRIMARY KEY, 
    id_cliente INTEGER REFERENCES cliente(id_cliente) NOT NULL UNIQUE,
    id_entrenador INTEGER REFERENCES empleado(id_empleado) NOT NULL
);

CREATE TABLE historial_asignaciones_entrenador(
    id_historial_asignacion_entrenador INTEGER PRIMARY KEY, 
    id_cliente INTEGER REFERENCES cliente(id_cliente) NOT NULL,
    id_entrenador INTEGER REFERENCES empleado(id_empleado) NOT NULL,
    fecha_asignacion DATE NOT NULL
);

-- Asistencias
CREATE TABLE asistencia(
    id_asistencia INTEGER PRIMARY KEY, 
    id_cliente INTEGER REFERENCES cliente(id_cliente),
    id_sucursal INTEGER REFERENCES sucursal(id_sucursal), --Al insertar verificar que la sucursal sea un gimasio y no una bodega
    entrada TIMESTAMP
    -- debe colocarse la rutina que hizo??????
);




-- - - - - - - - - - - - - - - - - - - - - RUTINAS - - - - - - - - - - - - - - - - - - - -
CREATE TABLE ejercicio(
    id_ejercicio INTEGER PRIMARY KEY, 
    nombre VARCHAR(200) NOT NULL UNIQUE, 
    descripcion TEXT
);

-- una rutina es una coleccion de ejercicios
CREATE TABLE rutina(
    id_rutina INTEGER PRIMARY KEY, 
    nombre_rutina VARCHAR(200), 
    descripcion TEXT, 

    id_entrenador_creador REFERENCES empleado(id_empleado), -- al insertar verificar que sea un entrenador
    fecha_creacion DATE
); 

CREATE TABLE ejercicio_rutina(
    -- enlanzando ejercicios con rutinas :3
    id_ejercicio_rutina INTEGER PRIMARY KEY, 
    id_ejercicio INTEGER REFERENCES ejercicio(id_ejercicio) NOT NULL, 
    id_rutina INTEGER REFERENCES rutina(id_rutina) NOT NULL, 

    -- info de la rutina
    numero_series INTEGER NOT NULL, 
    repeticiones INTEGER NOT NULL, 
    duracion_estimada INTEGER, 
    equipo_requerido TEXT -- no debe indicar id? solo especificar el tipo de equipo
);

-- Planes clientes (define las rutinas que tienen los clientes :3)
CREATE TABLE rutina_cliente(
    id_rutina_cliente INTEGER PRIMARY KEY, 
    id_cliente INTEGER REFERENCES cliente(id_cliente),
    id_rutina INTEGER REFERENCES rutina(id_rutina)
);


-- - - - - - - - - - - - - - - - - - - - - - PAGOS - - - - - - - - - - - - - - - - - - - - -
CREATE TABLE pago (
    id_pago INTEGER PRIMARY KEY,
    id_cliente INTEGER REFERENCES cliente(id_cliente),
    id_membresia INTEGER REFERENCES membresia_cliente(id_membresia),
    monto DECIMAL(10,2),
    fecha_pago DATE,
    tipo_servicio VARCHAR(50),
    descripcion TEXT
);