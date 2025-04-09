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
            "carnicería",
        ),
        (
            "Pescadería Laura Pérez",
            datetime.now(),
            "pescadería",
        ),
        (
            "Frutería Miguel Ruiz",
            datetime.now(),
            "frutería",
        ),
    ]
    print([(u[0], u[1], u[2]) for u in usuarios])
    print(usuarios)
    # cur.executemany(
    #     """
    #     INSERT INTO Usuario (correo, password, tipo)
    #     VALUES (%s, %s, %s) RETURNING id_usuario;
    # """,
    #     [(u[0], u[1], u[2]) for u in usuarios],
    # )

    # cur.executemany(
    #     """
    #     INSERT INTO Usuario (correo, password, tipo)
    #     VALUES (%s, %s, %s) RETURNING *;
    # """,
    #     usuarios
    # )

    # print(cur.execute("""SELECT *
    #                  FROM Usuario
    #                  """))
    # print(cur.fetchall())

    # cur.execute("""SELECT id_usuario
    #             FROM Usuario
    #             ORDER BY id_usuario DESC
    #             LIMIT 3;
    #             """)
    # usuarios_ids = [row[0] for row in cur.fetchall()]
    for usuario in usuarios:
        cur.execute(
            """
            INSERT INTO Usuario (correo, password, tipo)
            VALUES (%s, %s, %s) RETURNING id_usuario;
            """,
            usuario
        )
        # Recuperamos la ID generada y la agregamos a la lista
        usuario_id = cur.fetchone()[0]
        usuarios_ids.append(usuario_id)

    print(usuarios_ids)
    # Crear Negocios
    # cur.executemany(
    #     """
    #     INSERT INTO Negocio (id_negocio, nombre, dia, tipo_negocio)
    #     VALUES (%s, %s, %s, %s) RETURNING id_negocio;
    # """,
    #     [(usuarios_ids[i], negocios[i][0], negocios[i][1], negocios[i][2]) for i in range(3)],
    # )
    for i in range(3):
        cur.execute(
            """
            INSERT INTO Negocio (id_negocio, nombre, dia, tipo_negocio) 
        VALUES (%s, %s, %s, %s) RETURNING id_negocio;
            """,
            (usuarios_ids[i], negocios[i][0], negocios[i][1], negocios[i][2])
        )
        # Recuperamos la ID generada y la agregamos a la lista
        negocio_id = cur.fetchone()[0]
        negocios_ids.append(negocio_id)
    print(negocios_ids)
    # cur.execute("""SELECT id_negocio
    #                 FROM Negocio
    #                 ORDER BY id_negocio DESC
    #                 LIMIT 3;
    #                 """)
    # usuarios_ids = [row[0] for row in cur.fetchall()]
    # negocios_ids = [row[0] for row in cur.fetchall()]

    # Asociar negocios con su respectiva categoría

    # Productos de cada negocio
    carnes = [
        ("Res", "kg", "Refrigerado"),
        ("Cerdo", "kg", "Congelado"),
        ("Pollo", "kg", "Refrigerado"),
    ]
    pescados = [
        ("Salmón", "kg", "Fresco"),
        ("Merluza", "kg", "Congelado"),
        ("Atún", "kg", "Enlatado"),
    ]
    frutas = [
        ("Manzana", "kg", "Fresco"),
        ("Plátano", "kg", "Fresco"),
        ("Naranja", "kg", "Fresco"),
    ]

    # Insertar productos
    # cur.executemany(
    #     """INSERT INTO Carne (nombre, unidad, tipo_conserva, id_negocio)
    #     VALUES (%s, %s, %s, %s) RETURNING id_carne;""",
    #     [(carnes[i][0], carnes[i][1], carnes[i][2], usuarios_ids[i]) for i in range(3)],
    # )
    # cur.execute("""SELECT id_carne
    #                     FROM Carne
    #                     ORDER BY id_carne DESC
    #                     LIMIT 3;
    #                     """)

    for i in range(3):
        cur.execute(
            """INSERT INTO Carne (nombre, unidad, tipo_conserva, id_negocio)
        VALUES (%s, %s, %s, %s) RETURNING id_carne;""",
            (carnes[i][0], carnes[i][1], carnes[i][2], usuarios_ids[i])
        )
        # Recuperamos la ID generada y la agregamos a la lista
        carne_id = cur.fetchone()[0]
        carnes_ids.append(carne_id)
    # carnes_ids = [row[0] for row in cur.fetchall()]
    print(carnes_ids)
    cur.executemany(
        """INSERT INTO Pescado (nombre, unidad, tipo_conserva, id_negocio)
        VALUES (%s, %s, %s, %s) RETURNING id_pescado;""",
        [(pescados[i][0], pescados[i][1], pescados[i][2], usuarios_ids[i]) for i in range(3)],
    )
    # cur.execute("""SELECT id_pescado
    #                         FROM Pescado
    #                         ORDER BY id_pescado DESC
    #                         LIMIT 3;
    #                         """)
    for i in range(3):
        cur.execute(
            """INSERT INTO Pescado (nombre, unidad, tipo_conserva, id_negocio)
        VALUES (%s, %s, %s, %s) RETURNING id_pescado;""",
            (pescados[i][0], pescados[i][1], pescados[i][2], usuarios_ids[i])
        )
        # Recuperamos la ID generada y la agregamos a la lista
        pescado_id = cur.fetchone()[0]
        pescados_ids.append(pescado_id)
    # pescados_ids = [row[0] for row in cur.fetchall()]
    print(pescados_ids)

    cur.executemany(
        """INSERT INTO HortoFruticola (nombre, unidad, tipo_conserva, id_negocio)
        VALUES (%s, %s, %s, %s) RETURNING id_hortofruticola;""",
        [(frutas[i][0], frutas[i][1], frutas[i][2], usuarios_ids[i]) for i in range(3)],
    )
    # cur.execute("""SELECT id_hortofruticola
    #                         FROM Hortofruticola
    #                         ORDER BY id_hortofruticola DESC
    #                         LIMIT 3;
    #                         """)
    for i in range(3):
        cur.execute(
            """INSERT INTO HortoFruticola (nombre, unidad, tipo_conserva, id_negocio)
        VALUES (%s, %s, %s, %s) RETURNING id_hortofruticola;""",
            (frutas[i][0], frutas[i][1], frutas[i][2], usuarios_ids[i])
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
