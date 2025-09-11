-- Insertar sucursales
INSERT INTO sucursal (nombre, capacidad_maquinas) VALUES
('Sucursal Central', 50),
('Sucursal Norte', 30),
('Sucursal Sur', 20);

-- Insertar tipos de membresía
INSERT INTO membresia_tipo (nombre, descuento, duracion_dias) VALUES
('Básica', 0.00, 30),
('Premium', 10.00, 30),
('VIP', 20.00, 30);

-- Insertar administrador
INSERT INTO empleado (id_sucursal, nombre, apellido, rol, usuario, contraseña) VALUES
(1, 'Admin', 'Principal', 'administrador', 'admin', 'admin123');

-- Insertar ejercicios base
INSERT INTO ejercicio (nombre, descripcion) VALUES
('Press de banca', 'Ejercicio para pectorales'),
('Sentadillas', 'Ejercicio para piernas'),
('Dominadas', 'Ejercicio para espalda'),
('Press militar', 'Ejercicio para hombros'),
('Curl de bíceps', 'Ejercicio para bíceps'),
('Extensiones de tríceps', 'Ejercicio para tríceps'),
('Peso muerto', 'Ejercicio completo'),
('Abdominales', 'Ejercicio para core'),
('Cardio running', 'Ejercicio cardiovascular'),
('Cardio bicicleta', 'Ejercicio cardiovascular');