import psycopg2
from datetime import datetime, timedelta
import random

# Conexión a la base de datos
conn = psycopg2.connect(
    dbname="tu_base_de_datos",
    user="tu_usuario",
    password="tu_contraseña",
    host="localhost",
    port="5432",
)
cur = conn.cursor()

# Crear Usuarios
usuarios = [
    (
        "Carlos Gómez",
        "carlos@email.com",
        "Dueño de carnicería",
        "Calle 123",
        "123456789",
    ),
    (
        "Laura Pérez",
        "laura@email.com",
        "Dueña de pescadería",
        "Avenida 456",
        "987654321",
    ),
    ("Miguel Ruiz", "miguel@email.com", "Dueño de frutería", "Plaza 789", "567890123"),
]

cur.executemany(
    """
    INSERT INTO Usuario (nombre, correo, descripci, direccion, telefono, fecha_alta) 
    VALUES (%s, %s, %s, %s, %s, %s) RETURNING id_usuario;
""",
    [(u[0], u[1], u[2], u[3], u[4], datetime.now().date()) for u in usuarios],
)
usuarios_ids = [row[0] for row in cur.fetchall()]

# Crear Negocios
cur.executemany(
    """
    INSERT INTO Negocio (id_usuario, dia_compra) 
    VALUES (%s, %s) RETURNING id_negocio;
""",
    [(usuarios_ids[i], datetime.now()) for i in range(3)],
)
negocios_ids = [row[0] for row in cur.fetchall()]

# Asociar negocios con su respectiva categoría
cur.execute(
    "INSERT INTO Carniceria (id_negocio, capacidad_camar, capacidad_bodeg) VALUES (%s, %s, %s);",
    (negocios_ids[0], 500, 1000),
)
cur.execute(
    "INSERT INTO Pescaderia (id_negocio, capacidad_refrig, capacidad_tanque) VALUES (%s, %s, %s);",
    (negocios_ids[1], 300, 600),
)
cur.execute(
    "INSERT INTO Fruteria (id_negocio, capacidad_alm) VALUES (%s, %s);",
    (negocios_ids[2], 800),
)

# Productos de cada negocio
carnes = [
    ("Res", "Roja", "kg", "Refrigerado"),
    ("Cerdo", "Blanca", "kg", "Congelado"),
    ("Pollo", "Blanca", "kg", "Refrigerado"),
]
pescados = [
    ("Salmón", "Azul", "kg", "Fresco"),
    ("Merluza", "Blanco", "kg", "Congelado"),
    ("Atún", "Azul", "kg", "Enlatado"),
]
frutas = [
    ("Manzana", "Roja", "unidad", "Fresco"),
    ("Plátano", "Amarillo", "unidad", "Fresco"),
    ("Naranja", "Naranja", "unidad", "Fresco"),
]

# Insertar productos
cur.executemany(
    "INSERT INTO Carne (nombre, categoria, unidad, tipo_conserva) VALUES (%s, %s, %s, %s) RETURNING id_carne;",
    carnes,
)
carnes_ids = [row[0] for row in cur.fetchall()]
cur.executemany(
    "INSERT INTO Pescado (nombre, categoria, unidad, tipo_conserva) VALUES (%s, %s, %s, %s) RETURNING id_pescado;",
    pescados,
)
pescados_ids = [row[0] for row in cur.fetchall()]
cur.executemany(
    "INSERT INTO HortoFruticola (nombre, categoria, unidad, tipo_conserva) VALUES (%s, %s, %s, %s) RETURNING id_hortofruticola;",
    frutas,
)
frutas_ids = [row[0] for row in cur.fetchall()]

# Insertar stock
for carne_id in carnes_ids:
    cur.execute(
        "INSERT INTO StockCarne (cantidad, fecha_vencimiento, fecha_ingreso, id_carne, id_negocio) VALUES (%s, %s, %s, %s, %s);",
        (
            random.randint(50, 200),
            datetime.now() + timedelta(days=30),
            datetime.now(),
            carne_id,
            negocios_ids[0],
        ),
    )
for pescado_id in pescados_ids:
    cur.execute(
        "INSERT INTO StockPescado (cantidad, fecha_vencimiento, fecha_ingreso, id_pescado, id_negocio) VALUES (%s, %s, %s, %s, %s);",
        (
            random.randint(50, 200),
            datetime.now() + timedelta(days=15),
            datetime.now(),
            pescado_id,
            negocios_ids[1],
        ),
    )
for fruta_id in frutas_ids:
    cur.execute(
        "INSERT INTO StockHortoFruticola (cantidad, fecha_vencimiento, fecha_ingreso, id_hortofruticola, id_negocio) VALUES (%s, %s, %s, %s, %s);",
        (
            random.randint(50, 200),
            datetime.now() + timedelta(days=10),
            datetime.now(),
            fruta_id,
            negocios_ids[2],
        ),
    )


# Insertar histórico de ventas con tendencia de crecimiento
def generar_ventas(id_producto, id_negocio, tabla):
    fecha_inicio = datetime.now() - timedelta(days=365)
    for i in range(365):
        fecha = fecha_inicio + timedelta(days=i)
        cantidad = 10 + i // 30  # Incremento gradual
        if fecha.weekday() in [
            5,
            6,
        ]:  # Fines de semana más ventas para ciertos productos
            cantidad += 5
        cur.execute(
            f"INSERT INTO {tabla} (cantidad, fecha_vencimiento, fecha_ingreso, id_{tabla.split('Historico')[1].lower()}, id_negocio) VALUES (%s, %s, %s, %s, %s);",
            (cantidad, fecha + timedelta(days=10), fecha, id_producto, id_negocio),
        )


for carne_id in carnes_ids:
    generar_ventas(carne_id, negocios_ids[0], "HistoricoCarne")
for pescado_id in pescados_ids:
    generar_ventas(pescado_id, negocios_ids[1], "HistoricoPescado")
for fruta_id in frutas_ids:
    generar_ventas(fruta_id, negocios_ids[2], "HistoricoHortoFruticola")

# Confirmar cambios y cerrar conexión
conn.commit()
cur.close()
conn.close()
