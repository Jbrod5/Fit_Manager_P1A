-- fit_manager_init.sql
-- Script de creación y datos iniciales para FIT-MANAGER (PostgreSQL)

BEGIN;

-- 1. Extensiones útiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Tablas maestras
CREATE TABLE sucursal (
  sucursal_id SERIAL PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL UNIQUE,
  direccion TEXT,
  ciudad VARCHAR(50),
  telefono VARCHAR(30)
);

CREATE TABLE bodega (
  bodega_id SERIAL PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  ubicacion TEXT
);

CREATE TABLE rol (
  rol_id SERIAL PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL UNIQUE
);

-- 3. Empleados
CREATE TABLE empleado (
  empleado_id SERIAL PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password_hash TEXT NOT NULL,
  nombre VARCHAR(80) NOT NULL,
  apellido VARCHAR(80),
  email VARCHAR(120),
  telefono VARCHAR(30),
  fecha_ingreso DATE DEFAULT CURRENT_DATE,
  sucursal_id INT REFERENCES sucursal(sucursal_id) ON DELETE SET NULL
);

CREATE TABLE empleado_rol (
  empleado_id INT REFERENCES empleado(empleado_id) ON DELETE CASCADE,
  rol_id INT REFERENCES rol(rol_id) ON DELETE CASCADE,
  PRIMARY KEY (empleado_id, rol_id)
);

-- 4. Clientes
CREATE TABLE cliente (
  cliente_id SERIAL PRIMARY KEY,
  nombre VARCHAR(80) NOT NULL,
  apellido VARCHAR(80),
  dni VARCHAR(30) UNIQUE,
  email VARCHAR(120),
  telefono VARCHAR(30),
  fecha_registro DATE DEFAULT CURRENT_DATE,
  fecha_nacimiento DATE
);

-- 5. Tipos de membresía y membresías contratadas
CREATE TABLE membresia_tipo (
  membresia_tipo_id SERIAL PRIMARY KEY,
  nombre VARCHAR(30) UNIQUE NOT NULL,
  descuento_pct NUMERIC(5,2) NOT NULL CHECK (descuento_pct >= 0 AND descuento_pct <= 100)
);

CREATE TABLE membresia (
  membresia_id SERIAL PRIMARY KEY,
  cliente_id INT REFERENCES cliente(cliente_id) ON DELETE CASCADE,
  membresia_tipo_id INT REFERENCES membresia_tipo(membresia_tipo_id),
  fecha_inicio DATE NOT NULL,
  fecha_vencimiento DATE NOT NULL,
  activo BOOLEAN DEFAULT TRUE,
  entrenador_id INT REFERENCES empleado(empleado_id), -- entrenador asignado
  sucursal_id INT REFERENCES sucursal(sucursal_id) -- sucursal donde se registró/venta
);

-- 6. Pagos
CREATE TABLE pago (
  pago_id SERIAL PRIMARY KEY,
  cliente_id INT REFERENCES cliente(cliente_id) ON DELETE CASCADE,
  tipo_pago VARCHAR(50) NOT NULL, -- 'membresia' o 'servicio' u otros
  descripcion TEXT,
  monto NUMERIC(12,2) NOT NULL CHECK (monto >= 0),
  fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  sucursal_id INT REFERENCES sucursal(sucursal_id)
);

-- 7. Servicios y compras de servicios
CREATE TABLE servicio (
  servicio_id SERIAL PRIMARY KEY,
  nombre VARCHAR(80) NOT NULL,
  descripcion TEXT,
  precio NUMERIC(10,2) NOT NULL CHECK (precio >= 0)
);

CREATE TABLE compra_servicio (
  compra_servicio_id SERIAL PRIMARY KEY,
  cliente_id INT REFERENCES cliente(cliente_id) ON DELETE CASCADE,
  servicio_id INT REFERENCES servicio(servicio_id),
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  cantidad INT DEFAULT 1 CHECK (cantidad > 0),
  monto_total NUMERIC(12,2) NOT NULL,
  entrenador_id INT REFERENCES empleado(empleado_id) -- opcional si fue con entrenador
);

-- 8. Equipos e inventario
CREATE TABLE equipo (
  equipo_id SERIAL PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  descripcion TEXT,
  estado VARCHAR(30) DEFAULT 'activo', -- 'activo','mantenimiento','fuera_de_servicio'
  vida_util_estimada INT -- meses por ejemplo
);

CREATE TABLE inventario (
  inventario_id SERIAL PRIMARY KEY,
  equipo_id INT REFERENCES equipo(equipo_id) ON DELETE CASCADE,
  sucursal_id INT REFERENCES sucursal(sucursal_id) ON DELETE CASCADE,
  cantidad INT NOT NULL CHECK (cantidad >= 0)
);

CREATE TABLE transferencia_equipo (
  transferencia_id SERIAL PRIMARY KEY,
  equipo_id INT REFERENCES equipo(equipo_id),
  desde_sucursal_id INT REFERENCES sucursal(sucursal_id),
  hacia_sucursal_id INT REFERENCES sucursal(sucursal_id),
  cantidad INT NOT NULL CHECK (cantidad > 0),
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  estado VARCHAR(30) DEFAULT 'pendiente' -- 'pendiente','enviado','recibido'
);

-- 9. Rutinas, ejercicios y su relación
CREATE TABLE ejercicio (
  ejercicio_id SERIAL PRIMARY KEY,
  nombre VARCHAR(120) NOT NULL,
  descripcion TEXT,
  duracion_estimada INT -- minutos, nullable
);

CREATE TABLE rutina (
  rutina_id SERIAL PRIMARY KEY,
  cliente_id INT REFERENCES cliente(cliente_id) ON DELETE CASCADE,
  entrenador_id INT REFERENCES empleado(empleado_id),
  nombre VARCHAR(120) NOT NULL,
  fecha_inicio DATE DEFAULT CURRENT_DATE,
  fecha_fin DATE,
  descripcion TEXT
);

CREATE TABLE rutina_ejercicio (
  rutina_id INT REFERENCES rutina(rutina_id) ON DELETE CASCADE,
  ejercicio_id INT REFERENCES ejercicio(ejercicio_id),
  series INT NOT NULL CHECK (series >= 0),
  repeticiones INT NOT NULL CHECK (repeticiones >= 0),
  orden INT NOT NULL,
  equipo_requerido_id INT REFERENCES equipo(equipo_id),
  PRIMARY KEY (rutina_id, ejercicio_id, orden)
);

-- 10. Asistencias
CREATE TABLE asistencia (
  asistencia_id SERIAL PRIMARY KEY,
  cliente_id INT REFERENCES cliente(cliente_id) ON DELETE CASCADE,
  sucursal_id INT REFERENCES sucursal(sucursal_id),
  fecha_hora_entrada TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 11. Historial de cambios de entrenador
CREATE TABLE historial_entrenador (
  hist_id SERIAL PRIMARY KEY,
  cliente_id INT REFERENCES cliente(cliente_id),
  entrenador_old_id INT REFERENCES empleado(empleado_id),
  entrenador_new_id INT REFERENCES empleado(empleado_id),
  fecha_cambio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  motivo TEXT
);

-- Índices para consultas comunes
CREATE INDEX idx_asistencia_cliente_fecha ON asistencia (cliente_id, fecha_hora_entrada);
CREATE INDEX idx_membresia_cliente_activa ON membresia (cliente_id, activo);

-- 4. Insertar roles base
INSERT INTO rol (nombre) VALUES ('Recepcionista'), ('Entrenador'), ('Inventario'), ('Administrador');

-- 5. Insertar sucursales y bodega
INSERT INTO sucursal (nombre, direccion, ciudad, telefono) VALUES
 ('Sucursal Central','Av. Central 123','CiudadX','+502-000-0001'),
 ('Sucursal Norte','Av. Norte 45','CiudadX','+502-000-0002'),
 ('Sucursal Sur','Av. Sur 78','CiudadX','+502-000-0003');

INSERT INTO bodega (nombre, ubicacion) VALUES ('Bodega Central','Centro logístico');

-- 6. Insertar tipos de membresía
INSERT INTO membresia_tipo (nombre, descuento_pct) VALUES
 ('Basica', 0.0), ('Premium', 10.0), ('VIP', 20.0);

-- 7. Crear 1 administrador (obligatorio)
INSERT INTO empleado (username, password_hash, nombre, apellido, email, telefono, sucursal_id)
VALUES ('admin','$2y$12$examplehash','Admin','Fit','admin@fitmanager.local','+502-111-1111', 1);

-- asignarle rol de ADMIN
INSERT INTO empleado_rol (empleado_id, rol_id)
 SELECT e.empleado_id, r.rol_id FROM empleado e, rol r WHERE e.username = 'admin' AND r.nombre = 'Administrador';

-- 8. Generar empleados por sucursal para cumplir el minimo del enunciado
-- Central: 25 entrenadores, 5 inventario, 3 recepcionistas.
-- Norte: 15 entrenadores,3 inventario,2 recepcionistas.
-- Sur: 10 entrenadores,2 inventario,1 recepcionista.

-- función helper para insertar empleados por rango
DO $$
DECLARE i INT;
BEGIN
  -- Central
  FOR i IN 1..25 LOOP
    INSERT INTO empleado (username,password_hash,nombre,apellido,email,sucursal_id)
    VALUES (concat('cent_ent_',i),'hash',concat('EntrenadorC',i),'Apellido','entc'||i||'@fit.local',1);
    INSERT INTO empleado_rol (empleado_id, rol_id)
      VALUES ((SELECT max(empleado_id) FROM empleado), (SELECT rol_id FROM rol WHERE nombre='Entrenador'));
  END LOOP;
  FOR i IN 1..5 LOOP
    INSERT INTO empleado (username,password_hash,nombre,apellido,email,sucursal_id)
    VALUES (concat('cent_inv_',i),'hash',concat('InventarioC',i),'Apellido','invc'||i||'@fit.local',1);
    INSERT INTO empleado_rol (empleado_id, rol_id)
      VALUES ((SELECT max(empleado_id) FROM empleado), (SELECT rol_id FROM rol WHERE nombre='Inventario'));
  END LOOP;
  FOR i IN 1..3 LOOP
    INSERT INTO empleado (username,password_hash,nombre,apellido,email,sucursal_id)
    VALUES (concat('cent_rec_',i),'hash',concat('RecepC',i),'Apellido','repc'||i||'@fit.local',1);
    INSERT INTO empleado_rol (empleado_id, rol_id)
      VALUES ((SELECT max(empleado_id) FROM empleado), (SELECT rol_id FROM rol WHERE nombre='Recepcionista'));
  END LOOP;

  -- Norte
  FOR i IN 1..15 LOOP
    INSERT INTO empleado (username,password_hash,nombre,apellido,email,sucursal_id)
    VALUES (concat('nort_ent_',i),'hash',concat('EntrenadorN',i),'Apellido','entn'||i||'@fit.local',2);
    INSERT INTO empleado_rol (empleado_id, rol_id)
      VALUES ((SELECT max(empleado_id) FROM empleado), (SELECT rol_id FROM rol WHERE nombre='Entrenador'));
  END LOOP;
  FOR i IN 1..3 LOOP
    INSERT INTO empleado (username,password_hash,nombre,apellido,email,sucursal_id)
    VALUES (concat('nort_inv_',i),'hash',concat('InventarioN',i),'Apellido','invn'||i||'@fit.local',2);
    INSERT INTO empleado_rol (empleado_id, rol_id)
      VALUES ((SELECT max(empleado_id) FROM empleado), (SELECT rol_id FROM rol WHERE nombre='Inventario'));
  END LOOP;
  FOR i IN 1..2 LOOP
    INSERT INTO empleado (username,password_hash,nombre,apellido,email,sucursal_id)
    VALUES (concat('nort_rec_',i),'hash',concat('RecepN',i),'Apellido','recn'||i||'@fit.local',2);
    INSERT INTO empleado_rol (empleado_id, rol_id)
      VALUES ((SELECT max(empleado_id) FROM empleado), (SELECT rol_id FROM rol WHERE nombre='Recepcionista'));
  END LOOP;

  -- Sur
  FOR i IN 1..10 LOOP
    INSERT INTO empleado (username,password_hash,nombre,apellido,email,sucursal_id)
    VALUES (concat('sur_ent_',i),'hash',concat('EntrenadorS',i),'Apellido','ents'||i||'@fit.local',3);
    INSERT INTO empleado_rol (empleado_id, rol_id)
      VALUES ((SELECT max(empleado_id) FROM empleado), (SELECT rol_id FROM rol WHERE nombre='Entrenador'));
  END LOOP;
  FOR i IN 1..2 LOOP
    INSERT INTO empleado (username,password_hash,nombre,apellido,email,sucursal_id)
    VALUES (concat('sur_inv_',i),'hash',concat('InventarioS',i),'Apellido','invs'||i||'@fit.local',3);
    INSERT INTO empleado_rol (empleado_id, rol_id)
      VALUES ((SELECT max(empleado_id) FROM empleado), (SELECT rol_id FROM rol WHERE nombre='Inventario'));
  END LOOP;
  FOR i IN 1..1 LOOP
    INSERT INTO empleado (username,password_hash,nombre,apellido,email,sucursal_id)
    VALUES (concat('sur_rec_',i),'hash',concat('RecepS',i),'Apellido','reps'||i||'@fit.local',3);
    INSERT INTO empleado_rol (empleado_id, rol_id)
      VALUES ((SELECT max(empleado_id) FROM empleado), (SELECT rol_id FROM rol WHERE nombre='Recepcionista'));
  END LOOP;
END$$;

-- 9. Insertar equipos base y agregar inventario (incluyendo bodega con 50 equipos)
INSERT INTO equipo (nombre, descripcion, estado, vida_util_estimada) VALUES
 ('Cinta de correr','Cinta para cardio', 'activo', 60),
 ('Bicicleta estática','Bici estática', 'activo', 60),
 ('Máquina de pesas','Máquina multiuso', 'activo', 120),
 ('Remo','Máquina de remo', 'activo', 60),
 ('Elíptica','Elíptica', 'activo', 60);

-- Agregar 50 unidades a la bodega (usamos sucursal_id = NULL o crear entrada especial apuntando a bodega)
-- Para simplificar vamos a usar sucursal_id = 0 para bodega central: primero creamos sucursal "Bodega" en tabla sucursal
INSERT INTO sucursal (nombre, direccion, ciudad, telefono) VALUES ('Bodega Central (virtual)','Centro','CiudadX','');
-- obtener id de esa sucursal:
WITH s AS (SELECT sucursal_id FROM sucursal WHERE nombre LIKE 'Bodega Central (virtual)%' LIMIT 1)
INSERT INTO inventario (equipo_id, sucursal_id, cantidad)
 SELECT e.equipo_id, s.sucursal_id, 50 FROM equipo e, s LIMIT 1; -- asigna 50 del primer equipo como ejemplo

-- 10. Generar 150 clientes con distribución de membresías (75 Basica, 50 Premium, 25 VIP)
-- Generamos clientes dummy
INSERT INTO cliente (nombre, apellido, dni, email, telefono, fecha_registro)
SELECT 'Cliente' || gs, 'Ap' || gs, 'DNI' || gs, 'cliente' || gs || '@mail.local', '+502' || (1000000 + gs), CURRENT_DATE - (gs % 365)
FROM generate_series(1,150) AS gs;

-- Asignar membresías con la distribución solicitada
-- IDs de tipos:
WITH tipos AS (
  SELECT membresia_tipo_id, nombre FROM membresia_tipo
)
-- VIP: 25
INSERT INTO membresia (cliente_id, membresia_tipo_id, fecha_inicio, fecha_vencimiento, activo, entrenador_id, sucursal_id)
SELECT c.cliente_id, t.membresia_tipo_id, CURRENT_DATE - (random()*30)::int, CURRENT_DATE + (interval '1 month')::date, true,
 (SELECT empleado_id FROM empleado WHERE username LIKE 'cent_ent_%' ORDER BY random() LIMIT 1),
 (1 + (random()*2))::int
FROM cliente c CROSS JOIN LATERAL (SELECT membresia_tipo_id FROM tipos WHERE nombre='VIP' LIMIT 1) t
WHERE c.cliente_id BETWEEN 1 AND 25;

-- PREMIUM: next 50 (26..75)
INSERT INTO membresia (cliente_id, membresia_tipo_id, fecha_inicio, fecha_vencimiento, activo, entrenador_id, sucursal_id)
SELECT c.cliente_id, t.membresia_tipo_id, CURRENT_DATE - (random()*30)::int, CURRENT_DATE + (interval '3 month')::date, true,
 (SELECT empleado_id FROM empleado WHERE username LIKE 'nort_ent_%' ORDER BY random() LIMIT 1),
 (1 + (random()*2))::int
FROM cliente c CROSS JOIN LATERAL (SELECT membresia_tipo_id FROM tipos WHERE nombre='Premium' LIMIT 1) t
WHERE c.cliente_id BETWEEN 26 AND 75;

-- BASICA: remaining 75 (76..150)
INSERT INTO membresia (cliente_id, membresia_tipo_id, fecha_inicio, fecha_vencimiento, activo, entrenador_id, sucursal_id)
SELECT c.cliente_id, t.membresia_tipo_id, CURRENT_DATE - (random()*30)::int, CURRENT_DATE + (interval '1 month')::date, true,
 (SELECT empleado_id FROM empleado WHERE username LIKE 'sur_ent_%' ORDER BY random() LIMIT 1),
 (1 + (random()*2))::int
FROM cliente c CROSS JOIN LATERAL (SELECT membresia_tipo_id FROM tipos WHERE nombre='Basica' LIMIT 1) t
WHERE c.cliente_id BETWEEN 76 AND 150;

-- 11. Insertar al menos 2 pagos por cliente (pagos simples)
INSERT INTO pago (cliente_id, tipo_pago, descripcion, monto, fecha_pago, sucursal_id)
SELECT c.cliente_id,
       CASE WHEN random() < 0.6 THEN 'membresia' ELSE 'servicio' END,
       'Pago inicial',
       (10 + (random()*90))::numeric(10,2),
       NOW() - (random()*1000000)::int * interval '1 second',
       (1 + (random()*2))::int
FROM cliente c, generate_series(1,2) gs;

-- 12. Crear ejercicios base
INSERT INTO ejercicio (nombre, descripcion, duracion_estimada) VALUES
 ('Flexiones','Push ups', 5),
 ('Sentadillas','Squats', 10),
 ('Press de banca','Pecho', 15),
 ('Remo con barra','Espalda', 15),
 ('Cardio intervalos','Cardio HIIT', 20),
 ('Abdominales','Core', 8),
 ('Curl de bíceps','Brazos', 5),
 ('Burpees','Full body', 10);

-- 13. Para cada cliente crear al menos 5 rutinas y cada rutina 4 ejercicios
DO $$
DECLARE c RECORD;
DECLARE i INT;
DECLARE r_id INT;
BEGIN
  FOR c IN SELECT cliente_id FROM cliente LOOP
    FOR i IN 1..5 LOOP
      INSERT INTO rutina (cliente_id, entrenador_id, nombre, fecha_inicio)
      VALUES (c.cliente_id,
        (SELECT empleado_id FROM empleado WHERE username LIKE 'cent_ent_%' ORDER BY random() LIMIT 1),
        concat('Rutina_', c.cliente_id, '_', i), CURRENT_DATE - (i*7));
      r_id := (SELECT currval('rutina_rutina_id_seq'));
      -- Añadir 4 ejercicios aleatorios
      INSERT INTO rutina_ejercicio (rutina_id, ejercicio_id, series, repeticiones, orden, equipo_requerido_id)
      SELECT r_id, e.ejercicio_id, (2 + (random()*3))::int, (6 + (random()*10))::int, row_number() OVER (ORDER BY random()), NULL
      FROM ejercicio e
      ORDER BY random()
      LIMIT 4;
    END LOOP;
  END LOOP;
END$$;

-- 14. Generar asistencias aleatorias para clientes (ejemplo: 1-10 asistencias por cliente)
INSERT INTO asistencia (cliente_id, sucursal_id, fecha_hora_entrada)
SELECT c.cliente_id, (1 + (random()*2))::int, NOW() - (random()*1000000)::int * interval '1 second'
FROM cliente c, generate_series(1, (1 + (random()*10))::int) gs;

COMMIT;
