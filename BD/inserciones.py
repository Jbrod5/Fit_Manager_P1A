# -*- coding: utf-8 -*-
# --------------------------------------------------------------
# FIT-MANAGER - Script de inserciones para PostgreSQL
# Genera e inserta datos de ejemplo en el esquema de la BD FitManager
#
# Requisitos:
#   pip install psycopg2-binary faker
#
# Ejecución:
#   python inserciones.py
#
# Nota: Asume que la base de datos siempre estará vacía,
#       ya que se recrea con un script de bash antes de ejecutar este.
# --------------------------------------------------------------

import os
import random
import datetime
from faker import Faker
import psycopg2

# Faker en español (genera nombres y todo eso en español asdjlka)
falso = Faker('es_ES')

# ------------------- CONFIGURACIÓN -------------------
CONEXION = {
    'host': os.getenv('PGHOST', 'localhost'),
    'port': int(os.getenv('PGPORT', 5432)),
    'dbname': os.getenv('PGDATABASE', 'fitmanager'),
    'user': os.getenv('PGUSER', 'postgres'),
    'password': os.getenv('PGPASSWORD', '')
}

# Sucursales (gimnasios + bodega)
SUCURSALES = [
    ('Central', 'Gimnasio', 'Av. Central 123', 50),
    ('Norte', 'Gimnasio', 'Av. Norte 45', 30),
    ('Sur', 'Gimnasio', 'Av. Sur 9', 20),
    ('Bodega_Central', 'Bodega', 'Carretera Bodega 1', 50),
]

# Empleados por sucursal
PLAN_EMPLEADOS = {
    'Central': {'Entrenador': 25, 'Inventario': 5, 'Recepcionista': 3},
    'Norte': {'Entrenador': 15, 'Inventario': 3, 'Recepcionista': 2},
    'Sur': {'Entrenador': 10, 'Inventario': 2, 'Recepcionista': 1},
}
# Un administrador fijo (en Central)

# Clientes y membresías
TOTAL_CLIENTES = 60
DISTRIBUCION_MEMBRESIAS = {'Basica': 30, 'Premium': 20, 'VIP': 10}

MIN_RUTINAS_POR_CLIENTE = 5
MIN_EJERCICIOS_POR_RUTINA = 4
MIN_PAGOS_POR_CLIENTE = 2

# Ejemplo de equipos y ejercicios
EQUIPOS = [
    'Cinta de correr', 'Bicicleta estática', 'Remadora', 'Prensa de piernas', 'Banco de pesas',
    'Máquina de pectorales', 'Polea alta', 'Máquina de abdominales', 'Stepper', 'Elíptica',
    'Kettlebell', 'Mancuernas 5kg', 'Mancuernas 10kg', 'Mancuernas 20kg', 'Barra olímpica'
]

EJERCICIOS = [
    'Sentadillas', 'Press de banca', 'Remo con barra', 'Press militar', 'Curl de bíceps',
    'Fondos', 'Peso muerto', 'Elevaciones laterales', 'Crunch abdominal', 'Plancha', 'Zancadas'
]

TIPOS_PAGO = ['Membresía', 'Clase especial', 'Entrenamiento personal', 'Producto']

DOMINIOS = ['@gmail.com', '@outlook.com', '@hotmail.com', '@yahoo.com']


# ------------------- FUNCIONES AUXILIARES -------------------

def obtener_conexion():
    return psycopg2.connect(**CONEXION)


# ------------------- INSERCIONES -------------------

def crear_sucursales(cur):
    cur.execute("SELECT id_tipo, tipo FROM tipo_sucursal")
    tipos = {r[1]: r[0] for r in cur.fetchall()}

    ids_sucursales = {}
    for nombre, tipo, direccion, cant in SUCURSALES:
        cur.execute(
            "INSERT INTO sucursal (id_tipo, nombre, direccion, cantidad_maquinas) VALUES (%s,%s,%s,%s) RETURNING id_sucursal",
            (tipos[tipo], nombre, direccion, cant)
        )
        ids_sucursales[nombre] = cur.fetchone()[0]
    return ids_sucursales


def crear_equipos_inventario(cur, sucursales):
    ids_equipos = {}
    for nombre in EQUIPOS:
        cur.execute("INSERT INTO equipo (nombre, descripcion) VALUES (%s,%s) RETURNING id_equipo",
                    (nombre, "Equipo generado automáticamente"))
        ids_equipos[nombre] = cur.fetchone()[0]

    # Distribuir equipos
    for nombre_sucursal, _, _, total in SUCURSALES:
        id_suc = sucursales[nombre_sucursal]
        for eq, id_eq in ids_equipos.items():
            if nombre_sucursal == 'Bodega_Central':
                cantidad = 50
            else:
                base = max(1, total // len(ids_equipos))
                cantidad = base + random.randint(0, 3)
            cur.execute("INSERT INTO inventario (id_equipo, id_sucursal, cantidad) VALUES (%s,%s,%s)",
                        (id_eq, id_suc, cantidad))


def crear_empleados(cur, sucursales):
    cur.execute("SELECT id_rol_empleado, nombre_rol FROM rol_empleado")
    roles = {r[1]: r[0] for r in cur.fetchall()}

    # Administrador fijo
    cur.execute(
        "INSERT INTO empleado (id_sucursal, nombre, correo, telefono, rol, usuario, passwrd) "
        "VALUES (%s,%s,%s,%s,%s,%s,%s) RETURNING id_empleado",
        (sucursales['Central'], 'Administrador Principal', 'admin@fitmanager.com', 0,
         roles['Administrador'], 'admin', 'admin123')
    )

    # Resto de empleados
    for suc, plan in PLAN_EMPLEADOS.items():
        for rol, cantidad in plan.items():
            for i in range(cantidad):
                nombre = falso.name()
                usuario = f"{nombre.split()[0].lower()}_{suc}_{i}"
                correo = usuario + random.choice(DOMINIOS)
                telefono = random.randint(20000000, 79999999)  # teléfono guatemalteco de 8 dígitos
                cur.execute(
                    "INSERT INTO empleado (id_sucursal, nombre, correo, telefono, rol, usuario, passwrd) "
                    "VALUES (%s,%s,%s,%s,%s,%s,%s) RETURNING id_empleado",
                    (sucursales[suc], nombre, correo, telefono, roles[rol], usuario, 'pass123')
                )



def crear_clientes(cur, sucursales):
    cur.execute("SELECT id_tipo_membresia, nombre_membresia FROM tipo_membresia_cliente")
    membresias = {r[1]: r[0] for r in cur.fetchall()}

    cur.execute(
        "SELECT id_empleado FROM empleado e "
        "JOIN rol_empleado r ON e.rol=r.id_rol_empleado "
        "WHERE r.nombre_rol='Entrenador'"
    )
    entrenadores = [r[0] for r in cur.fetchall()]

    ids_clientes = []

    # Crear clientes
    for tipo, cantidad in DISTRIBUCION_MEMBRESIAS.items():
        for _ in range(cantidad):
            nombre = falso.name()
            correo = f"{nombre.split()[0].lower()}.{random.randint(100,999)}{random.choice(DOMINIOS)}"
            cur.execute(
                "INSERT INTO cliente (nombre, correo) VALUES (%s,%s) RETURNING id_cliente",
                (nombre, correo)
            )
            id_cliente = cur.fetchone()[0]
            ids_clientes.append(id_cliente)

            # Membresía
            fecha_inicio = datetime.date.today() - datetime.timedelta(days=random.randint(0, 365))
            duracion = random.choice([30, 90, 365])
            fecha_fin = fecha_inicio + datetime.timedelta(days=duracion)
            cur.execute(
                "INSERT INTO membresia_cliente (id_cliente, id_tipo_membresia, fecha_inicio, fecha_fin) "
                "VALUES (%s,%s,%s,%s)",
                (id_cliente, membresias[tipo], fecha_inicio, fecha_fin)
            )

            # Asignar entrenador
            entrenador = random.choice(entrenadores)
            cur.execute(
                "INSERT INTO asignacion_entrenador (id_cliente, id_entrenador) VALUES (%s,%s)",
                (id_cliente, entrenador)
            )

            # Rutinas y ejercicios
            for i in range(MIN_RUTINAS_POR_CLIENTE):
                cur.execute(
                    "INSERT INTO rutina (nombre_rutina, descripcion, id_entrenador_creador, fecha_creacion) "
                    "VALUES (%s,%s,%s,%s) RETURNING id_rutina",
                    (f"Rutina_{id_cliente}_{i}", "Generada automáticamente", entrenador, fecha_inicio)
                )
                id_rutina = cur.fetchone()[0]
                cur.execute(
                    "INSERT INTO rutina_cliente (id_cliente, id_rutina) VALUES (%s,%s)",
                    (id_cliente, id_rutina)
                )

                for _ in range(MIN_EJERCICIOS_POR_RUTINA):
                    nombre_ej = random.choice(EJERCICIOS)

                    # Comprobar si ya existe
                    cur.execute("SELECT id_ejercicio FROM ejercicio WHERE nombre = %s", (nombre_ej,))
                    resultado = cur.fetchone()

                    if resultado:
                        id_ej = resultado[0]  # Usar ejercicio existente
                    else:
                        cur.execute(
                            "INSERT INTO ejercicio (nombre, descripcion) VALUES (%s,%s) RETURNING id_ejercicio",
                            (nombre_ej, "Ejercicio generado")
                        )
                        id_ej = cur.fetchone()[0]

                    # Insertar en la rutina
                    cur.execute(
                        "INSERT INTO ejercicio_rutina (id_ejercicio, id_rutina, numero_series, repeticiones) "
                        "VALUES (%s,%s,%s,%s)",
                        (id_ej, id_rutina, random.randint(3,5), random.choice([8,10,12,15]))
                    )

            # Pagos
            for _ in range(MIN_PAGOS_POR_CLIENTE):
                monto = round(random.uniform(50, 500), 2)
                tipo_pago = random.choice(TIPOS_PAGO)
                fecha_pago = datetime.date.today() - datetime.timedelta(days=random.randint(0, 365))
                cur.execute(
                    "INSERT INTO pago (id_cliente, monto, fecha_pago, tipo_servicio, descripcion) "
                    "VALUES (%s,%s,%s,%s,%s)",
                    (id_cliente, monto, fecha_pago, tipo_pago, f"Pago de {tipo_pago}")
                )


def main():
    conn = obtener_conexion()
    conn.autocommit = False
    try:
        with conn.cursor() as cur:
            sucursales = crear_sucursales(cur)
            crear_equipos_inventario(cur, sucursales)
            crear_empleados(cur, sucursales)
            crear_clientes(cur, sucursales)
            conn.commit()
            print("✅ Inserciones completadas con éxito")
    except Exception as e:
        conn.rollback()
        print("❌ Error durante inserciones:", e)
    finally:
        conn.close()


if __name__ == '__main__':
    main()
