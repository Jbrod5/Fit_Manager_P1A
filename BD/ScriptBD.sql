-- 1. SUCURSAL
CREATE TABLE sucursal (
    id_sucursal SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    direccion VARCHAR(100),
    cantidad_maquianas INTEGER
);

-- 2. EMPLEADO
CREATE TABLE empleado (
    id_empleado SERIAL PRIMARY KEY,
    id_sucursal INTEGER REFERENCES sucursal(id_sucursal),
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    telefono VARCHAR(15),
    rol VARCHAR(20) CHECK (rol IN ('administrador', 'recepcionista', 'entrenador', 'inventario')),
    usuario VARCHAR(50) UNIQUE,
    contraseña VARCHAR(100),
    fecha_contratacion DATE
);

-- 3. CLIENTE
CREATE TABLE cliente (
    id_cliente SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    telefono VARCHAR(15),
    fecha_registro DATE,
    fecha_nacimiento DATE
);

-- 4. MEMBRESIA_TIPO
CREATE TABLE membresia_tipo (
    id_tipo_membresia SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descuento DECIMAL(5,2),
    duracion_dias INTEGER
);

-- 5. MEMBRESIA_CLIENTE
CREATE TABLE membresia_cliente (
    id_membresia SERIAL PRIMARY KEY,
    id_cliente INTEGER REFERENCES cliente(id_cliente),
    id_tipo_membresia INTEGER REFERENCES membresia_tipo(id_tipo_membresia),
    fecha_inicio DATE,
    fecha_vencimiento DATE,
    activa BOOLEAN DEFAULT true
);

-- 6. ASIGNACION_ENTRENADOR
CREATE TABLE asignacion_entrenador (
    id_asignacion SERIAL PRIMARY KEY,
    id_cliente INTEGER REFERENCES cliente(id_cliente),
    id_entrenador INTEGER REFERENCES empleado(id_empleado),
    fecha_asignacion DATE,
    activa BOOLEAN DEFAULT true
);

-- 7. RUTINA
CREATE TABLE rutina (
    id_rutina SERIAL PRIMARY KEY,
    id_cliente INTEGER REFERENCES cliente(id_cliente),
    id_entrenador INTEGER REFERENCES empleado(id_empleado),
    nombre VARCHAR(100),
    tipo VARCHAR(50),
    fecha_creacion DATE
);

-- 8. EJERCICIO
CREATE TABLE ejercicio (
    id_ejercicio SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

-- 9. RUTINA_EJERCICIO
CREATE TABLE rutina_ejercicio (
    id_rutina_ejercicio SERIAL PRIMARY KEY,
    id_rutina INTEGER REFERENCES rutina(id_rutina),
    id_ejercicio INTEGER REFERENCES ejercicio(id_ejercicio),
    series INTEGER,
    repeticiones INTEGER,
    duracion_minutos INTEGER,
    equipos_requeridos VARCHAR(200)
);

-- 10. ASISTENCIA
CREATE TABLE asistencia (
    id_asistencia SERIAL PRIMARY KEY,
    id_cliente INTEGER REFERENCES cliente(id_cliente),
    id_sucursal INTEGER REFERENCES sucursal(id_sucursal),
    fecha_entrada TIMESTAMP,
    fecha_salida TIMESTAMP
);

-- 11. EQUIPO
CREATE TABLE equipo (
    id_equipo SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    estado VARCHAR(20) CHECK (estado IN ('disponible', 'mantenimiento', 'reposicion'))
);

-- 12. INVENTARIO_SUCURSAL
CREATE TABLE inventario_sucursal (
    id_inventario SERIAL PRIMARY KEY,
    id_sucursal INTEGER REFERENCES sucursal(id_sucursal),
    id_equipo INTEGER REFERENCES equipo(id_equipo),
    cantidad INTEGER,
    ultima_actualizacion DATE
);

-- 13. PAGO
CREATE TABLE pago (
    id_pago SERIAL PRIMARY KEY,
    id_cliente INTEGER REFERENCES cliente(id_cliente),
    id_membresia INTEGER REFERENCES membresia_cliente(id_membresia),
    monto DECIMAL(10,2),
    fecha_pago DATE,
    tipo_servicio VARCHAR(50),
    descripcion TEXT
);

-- 14. MOVIMIENTO_INVENTARIO
CREATE TABLE movimiento_inventario (
    id_movimiento SERIAL PRIMARY KEY,
    id_equipo INTEGER REFERENCES equipo(id_equipo),
    id_sucursal_origen INTEGER REFERENCES sucursal(id_sucursal),
    id_sucursal_destino INTEGER REFERENCES sucursal(id_sucursal),
    cantidad INTEGER,
    fecha_movimiento DATE,
    tipo_movimiento VARCHAR(20) CHECK (tipo_movimiento IN ('entrada', 'salida', 'transferencia'))
);