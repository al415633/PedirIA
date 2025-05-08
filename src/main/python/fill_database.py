from typing import Any

import psycopg2
from datetime import datetime, timedelta
import random

conn = psycopg2.connect(
    dbname="al415647_PedirIA",
    user="al415696",
    password="20910250F",
    host="db-aules.uji.es",
    port="5432",
)
conn.autocommit = True

global cur
cur = conn.cursor()
# global carnes_ids;#: list[tuple[Any, ...]]# = [row[0] for row in cur.fetchall()]
# global frutas_ids#: list[tuple[Any, ...]] = [row[0] for row in cur.fetchall()]
# global pescados_ids#: list[tuple[Any, ...]] = [row[0] for row in cur.fetchall()]
# global negocios_ids#: list[tuple[Any, ...]] = [row[0] for row in cur.fetchall()]
# global usuarios_ids#: list[tuple[Any, ...]] = [row[0] for row in cur.fetchall()]
global carnes_ids
global frutas_ids
global pescados_ids
global negocios_ids
global usuarios_ids
carnes_ids = []
frutas_ids = []
pescados_ids = []
negocios_ids = []
usuarios_ids = []



def init_negocios():
    global carnes_ids
    global frutas_ids
    global pescados_ids
    global negocios_ids
    global usuarios_ids

    global cur
    conn.autocommit = True

    # Conexión a la base de datos

    # Crear Usuarios
    usuarios = [
        (
            "carlos@email.com",
            1234,
            "negocio"
        ),
        (
            "laura@email.com",
            1234,
            "negocio"
        ),
        (
            "miguel@email.com",
            1234,
            "negocio"
        ),
    ]
    negocios = [
        (
            "Carnicería Carlos Gómez",
            datetime.now(),
            "carne",
        ),
        (
            "Pescadería Laura Pérez",
            datetime.now(),
            "pescado",
        ),
        (
            "Frutería Miguel Ruiz",
            datetime.now(),
            "fruta",
        ),
    ]

    usuarios_ids = [8, 9, 10]
    negocios_ids = [8, 9, 10]
    imagenes_carne_ids = [3, 4, 5]
    imagenes_pescado_ids = [1, 2, 3]
    imagenes_fruta_ids = [1, 2, 3]
    # Asociar negocios con su respectiva categoría

    # Productos de cada negocio
    carnes = [
        ("Res", "kg", 'refrigerado'),
        ("Cerdo", "kg", 'congelado'),
        ("Pollo", "kg", 'refrigerado'),
    ]
    pescados = [
        ("Salmón", "kg", 'fresco'),
        ("Merluza", "kg", 'congelado'),
        ("Atún", "kg", 'fresco'),
    ]
    frutas = [
        ("Manzana", "kg", 'fresco'),
        ("Plátano", "kg", 'fresco'),
        ("Naranja", "kg", 'fresco'),
    ]

    for i in range(3):
        cur.execute(
            """INSERT INTO Carne (nombre, unidad, tipo_conserva, id_negocio, id_img)
        VALUES (%s, %s, %s, %s, %s) RETURNING id_carne;""",
            (carnes[i][0], carnes[i][1], carnes[i][2].upper(), usuarios_ids[0], imagenes_carne_ids[i])
        )
        carne_id = cur.fetchone()[0]
        carnes_ids.append(carne_id)
    print(carnes_ids)


    for i in range(3):
        cur.execute(
            """INSERT INTO Pescado (nombre, unidad, tipo_conserva, id_negocio, id_img)
        VALUES (%s, %s, %s, %s, %s) RETURNING id_pescado;""",
            (pescados[i][0], pescados[i][1], pescados[i][2].upper(), usuarios_ids[1], imagenes_pescado_ids[i])
        )
        # Recuperamos la ID generada y la agregamos a la lista
        pescado_id = cur.fetchone()[0]
        pescados_ids.append(pescado_id)

    for i in range(3):
        cur.execute(
            """INSERT INTO HortoFruticola (nombre, unidad, tipo_conserva, id_negocio, id_img)
        VALUES (%s, %s, %s, %s, %s) RETURNING id_hortofruticola;""",
            (frutas[i][0], frutas[i][1], frutas[i][2].upper(), usuarios_ids[2], imagenes_fruta_ids[i])
        )
        # Recuperamos la ID generada y la agregamos a la lista
        fruta_id = cur.fetchone()[0]
        frutas_ids.append(fruta_id)
    # frutas_ids = [row[0] for row in cur.fetchall()]
    print(frutas_ids)

    # Insertar stock
    for carne_id in carnes_ids:
        cur.execute(
            "INSERT INTO StockCarne (cantidad, fecha_vencimiento, fecha_ingreso, id_carne) VALUES (%s, %s, %s, %s);",
            (
                random.randint(50, 200),
                datetime.now() + timedelta(days=30),
                datetime.now(),
                carne_id,
            ),
        )
    for pescado_id in pescados_ids:
        cur.execute(
            "INSERT INTO StockPescado (cantidad, fecha_vencimiento, fecha_ingreso, id_pescado) VALUES (%s, %s, %s, %s);",
            (
                random.randint(50, 200),
                datetime.now() + timedelta(days=15),
                datetime.now(),
                pescado_id,
            ),
        )
    for fruta_id in frutas_ids:
        cur.execute(
            "INSERT INTO StockHortoFruticola (cantidad, fecha_vencimiento, fecha_ingreso, id_hortofruticola) VALUES (%s, %s, %s, %s);",
            (
                random.randint(50, 200),
                datetime.now() + timedelta(days=10),
                datetime.now(),
                fruta_id,
            ),
        )
    print(carnes_ids)
    print(frutas_ids)
    print(pescados_ids)
    print(negocios_ids)
    print(usuarios_ids)


# Insertar histórico de ventas con tendencia de crecimiento
def generar_ventas(id_producto, id_negocio, tabla):
    fecha_inicio = datetime.now() - timedelta(days=365)
    print(tabla)
    for i in range(365):
        fecha = fecha_inicio + timedelta(days=i)
        cantidad = 10 + random.randint(-10, 10) + i // 30  # Incremento gradual
        if fecha.weekday() in [
            5,
            6,
        ]:  # Fines de semana más ventas para ciertos productos
            cantidad += random.randint(2, 10)
        cur.execute(
            f"INSERT INTO {tabla} (cantidad, fecha_vencimiento, fecha_ingreso, fecha_venta, id_{tabla.split('Historico')[1].title()}) VALUES (%s, %s, %s, %s, %s);",
            (cantidad, fecha + timedelta(days=10), fecha,fecha + timedelta(days=random.randint(1, 10)), id_producto),
        )


def fill_historico():
    global carnes_ids
    global frutas_ids
    global pescados_ids
    global negocios_ids
    global usuarios_ids
    for carne_id in (carnes_ids):
        generar_ventas(carne_id, negocios_ids[0], "HistoricoCarne")
    for pescado_id in pescados_ids:
        generar_ventas(pescado_id, negocios_ids[1], "HistoricoPescado")
    for fruta_id in frutas_ids:
        generar_ventas(fruta_id, negocios_ids[2], "HistoricoHortoFruticola")

    # Confirmar cambios y cerrar conexión
    conn.commit()
    cur.close()
    conn.close()


# Método para eliminar solo los datos insertados por este script


def eliminar_datos_insertados():
    global carnes_ids
    global frutas_ids
    global pescados_ids
    global negocios_ids
    global usuarios_ids
    for tabla, ids, campo in [
        ("HistoricoHortoFruticola", frutas_ids, "id_hortofruticola"),
        ("HistoricoPescado", pescados_ids, "id_pescado"),
        ("HistoricoCarne", carnes_ids, "id_carne"),
        ("StockHortoFruticola", frutas_ids, "id_hortofruticola"),
        ("StockPescado", pescados_ids, "id_pescado"),
        ("StockCarne", carnes_ids, "id_carne"),
    ]:
        for i in ids:
            cur.execute(f"DELETE FROM {tabla} WHERE {campo} = %s", (i,))

    for tabla, ids in [
        ("HortoFruticola", frutas_ids),
        ("Pescado", pescados_ids),
        ("Carne", carnes_ids)
    ]:
        for i in ids:
            cur.execute(f"DELETE FROM {tabla} WHERE id_{tabla.lower()} = %s", (i,))

    cur.execute("DELETE FROM Fruteria WHERE id_negocio = %s", (negocios_ids[2],))
    cur.execute("DELETE FROM Pescaderia WHERE id_negocio = %s", (negocios_ids[1],))
    cur.execute("DELETE FROM Carniceria WHERE id_negocio = %s", (negocios_ids[0],))

    for i in negocios_ids:
        cur.execute("DELETE FROM Negocio WHERE id_negocio = %s", (i,))
    for i in usuarios_ids:
        cur.execute("DELETE FROM Usuario WHERE id_usuario = %s", (i,))

    conn.commit()


def main():
    init_negocios()
    fill_historico()


def clean():
    cur.execute("DELETE FROM Usuario WHERE id_usuario > 60")


if __name__ == "__main__":
    main()
    #clean()
