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
global negocio_id
carnes_ids = []
frutas_ids = []
pescados_ids = []
negocio_id = 1

# Insertar histórico de ventas con tendencia de crecimiento

def add_productos(id_negocio, tipo):
    imagenes_carne_ids = [3, 4, 5]
    imagenes_pescado_ids = [1, 2, 3]
    imagenes_fruta_ids = [1, 2, 3]
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
    # Insertar stock

    match tipo:
        case 'carne':
            for i in range(3):
                cur.execute(
                    """INSERT INTO Carne (nombre, unidad, tipo_conserva, id_negocio, id_img)
                VALUES (%s, %s, %s, %s, %s) RETURNING id_carne;""",
                    (carnes[i][0], carnes[i][1], carnes[i][2].upper(), id_negocio, imagenes_carne_ids[i])
                )
                carne_id = cur.fetchone()[0]
                carnes_ids.append(carne_id)
            print(carnes_ids)
            for carne_id in carnes_ids:
                cur.execute(
                    "INSERT INTO StockCarne (cantidad, fecha_vencimiento, fecha_ingreso, id_carne) VALUES (%s, %s, %s, %s);",
                    (
                        random.randint(50, 200),
                        datetime.now() + timedelta(days=90),
                        datetime.now(),
                        carne_id,
                    ),
                )
        case 'pescado':
            for i in range(3):
                cur.execute(
                    """INSERT INTO Pescado (nombre, unidad, tipo_conserva, id_negocio, id_img)
                VALUES (%s, %s, %s, %s, %s) RETURNING id_pescado;""",
                    (pescados[i][0], pescados[i][1], pescados[i][2].upper(), id_negocio, imagenes_pescado_ids[i])
                )
                # Recuperamos la ID generada y la agregamos a la lista
                pescado_id = cur.fetchone()[0]
                pescados_ids.append(pescado_id)
            for pescado_id in pescados_ids:
                cur.execute(
                    "INSERT INTO StockPescado (cantidad, fecha_vencimiento, fecha_ingreso, id_pescado) VALUES (%s, %s, %s, %s);",
                    (
                        random.randint(50, 200),
                        datetime.now() + timedelta(days=60),
                        datetime.now(),
                        pescado_id,
                    ),
                )
        case 'fruta':
            for i in range(3):
                cur.execute(
                    """INSERT INTO HortoFruticola (nombre, unidad, tipo_conserva, id_negocio, id_img)
                VALUES (%s, %s, %s, %s, %s) RETURNING id_hortofruticola;""",
                    (frutas[i][0], frutas[i][1], frutas[i][2].upper(), id_negocio, imagenes_fruta_ids[i])
                )
                # Recuperamos la ID generada y la agregamos a la lista
                fruta_id = cur.fetchone()[0]
                frutas_ids.append(fruta_id)
            # frutas_ids = [row[0] for row in cur.fetchall()]
            print(frutas_ids)
            for fruta_id in frutas_ids:
                cur.execute(
                    "INSERT INTO StockHortoFruticola (cantidad, fecha_vencimiento, fecha_ingreso, id_hortofruticola) VALUES (%s, %s, %s, %s);",
                    (
                        random.randint(50, 200),
                        datetime.now() + timedelta(days=30),
                        datetime.now(),
                        fruta_id,
                    ),
                )
        case "otro":
            for i in range(3):
                cur.execute(
                    """INSERT INTO Carne (nombre, unidad, tipo_conserva, id_negocio, id_img)
                VALUES (%s, %s, %s, %s, %s) RETURNING id_carne;""",
                    (carnes[i][0], carnes[i][1], carnes[i][2].upper(), id_negocio, imagenes_carne_ids[i])
                )
                carne_id = cur.fetchone()[0]
                carnes_ids.append(carne_id)
            print(carnes_ids)
            for carne_id in carnes_ids:
                cur.execute(
                    "INSERT INTO StockCarne (cantidad, fecha_vencimiento, fecha_ingreso, id_carne) VALUES (%s, %s, %s, %s);",
                    (
                        random.randint(50, 200),
                        datetime.now() + timedelta(days=random.randint(60, 90)),
                        datetime.now(),
                        carne_id,
                    ),
                )
            for i in range(3):
                cur.execute(
                    """INSERT INTO Pescado (nombre, unidad, tipo_conserva, id_negocio, id_img)
                VALUES (%s, %s, %s, %s, %s) RETURNING id_pescado;""",
                    (pescados[i][0], pescados[i][1], pescados[i][2].upper(), id_negocio, imagenes_pescado_ids[i])
                )
                # Recuperamos la ID generada y la agregamos a la lista
                pescado_id = cur.fetchone()[0]
                pescados_ids.append(pescado_id)
                for pescado_id in pescados_ids:
                    cur.execute(
                        "INSERT INTO StockPescado (cantidad, fecha_vencimiento, fecha_ingreso, id_pescado) VALUES (%s, %s, %s, %s);",
                        (
                            random.randint(50, 200),
                            datetime.now() + timedelta(days=random.randint(50, 60)),
                            datetime.now(),
                            pescado_id,
                        ),
                    )
            for i in range(3):
                cur.execute(
                    """INSERT INTO HortoFruticola (nombre, unidad, tipo_conserva, id_negocio, id_img)
                VALUES (%s, %s, %s, %s, %s) RETURNING id_hortofruticola;""",
                    (frutas[i][0], frutas[i][1], frutas[i][2].upper(), id_negocio, imagenes_fruta_ids[i])
                )
                # Recuperamos la ID generada y la agregamos a la lista
                fruta_id = cur.fetchone()[0]
                frutas_ids.append(fruta_id)
                # frutas_ids = [row[0] for row in cur.fetchall()]
            print(frutas_ids)
            for fruta_id in frutas_ids:
                cur.execute(
                    "INSERT INTO StockHortoFruticola (cantidad, fecha_vencimiento, fecha_ingreso, id_hortofruticola) VALUES (%s, %s, %s, %s);",
                    (
                        random.randint(50, 200),
                        datetime.now() + timedelta(days=random.randint(30, 40)),
                        datetime.now(),
                        fruta_id,
                    ),
                )






def generar_ventas(id_producto, tabla, dias_anterioridad):
    fecha_inicio = datetime.now() - timedelta(days=dias_anterioridad)
    print(f'{tabla}(id stock {id_producto})')
    for i in range(dias_anterioridad):
        if i % 50 == 0:
            print(".", end="")
        fecha = fecha_inicio + timedelta(days=i)
        cantidad = 10 + random.randint(-9, 10) + i // 20  # Incremento gradual
        if fecha.weekday() in [
            5,
            6,
        ]:  # Fines de semana más ventas para ciertos productos
            cantidad += random.randint(2, 20)
        cur.execute(
            f"INSERT INTO {tabla} (cantidad, fecha_vencimiento, fecha_ingreso, fecha_venta, id_{tabla.split('Historico')[1].title()}) VALUES (%s, %s, %s, %s, %s);",
            (cantidad, fecha + timedelta(days=10), fecha, fecha + timedelta(days=random.randint(1, 10)), id_producto),
        )
    print(f"completado {id_producto}")


def fill_historico(tipo, dias_anterioridad):
    global carnes_ids
    global frutas_ids
    global pescados_ids
    global negocio_id
    if tipo == "carne" or tipo == "otro":
        print(f"historiales para productos {carnes_ids}")
        for carne_id in (carnes_ids):
            generar_ventas(carne_id, "HistoricoCarne", dias_anterioridad)
    if tipo == "pescado" or tipo == "otro":
        print(f"historiales para productos {pescados_ids}")
        for pescado_id in pescados_ids:
            generar_ventas(pescado_id, "HistoricoPescado", dias_anterioridad)
    if tipo == "fruta" or tipo == "otro":
        print(f"historiales para productos {frutas_ids}")
        for fruta_id in frutas_ids:
            generar_ventas(fruta_id, "HistoricoHortoFruticola", dias_anterioridad)
    print("finalizado inserciones")
    # Confirmar cambios y cerrar conexión
    conn.commit()
    cur.close()
    conn.close()
    print("finalizado commit")



# Método para eliminar solo los datos insertados por este script

def main(funcion, negocio_id , dias_anterioridad, tipo_usuario):

    # Pablo: 17 /  Carne: [20, 21, 23, 24, 25, 26]
    # Carla: 18 / Fruta: [9, 10, 11, 13, 14, 15]

    match funcion:
        case "productos":
            add_productos(negocio_id, tipo_usuario)
        case "historicos":
            fill_historico(tipo_usuario, dias_anterioridad)



def clean():
    cur.execute("DELETE FROM Usuario WHERE id_usuario > 60")


if __name__ == "__main__":


    #Cambiar esto a la id del negocio con el que quieres trabajar
    ## Asignar a la variable "id" correspondiente las ids de los productos para los que quieras crear historicos
    global carnes_ids
    global frutas_ids
    global pescados_ids
    # carnes_ids = [24, 25, 26]
    # carnes_ids = [20, 21, 23]
    # frutas_ids= [13, 14, 15]
    # frutas_ids = [9, 10, 11]

    ## Cambiar a la id del negocio con el que se quiere trabajar
    negocio_id = 18

    ## Cambiar tipo_usuario al tipo de negocio del usuario
    # carne, pescado, fruta, otro
    tipo_usuario = 'fruta'

    ##Elegir si se quiere añadir productos, o historicos (Se añadirán los productos especificados en "add_productos")
    #productos , historicos
    funcion_utilizada = "historicos"
    ## Los dias hacia atrás en los que se generan historicos (30 desde hace més, 365 un año etc), 1 cada día con tendencia creciente
    dias_anterioridad = 30

    main(funcion= funcion_utilizada, negocio_id=negocio_id, dias_anterioridad=dias_anterioridad, tipo_usuario= tipo_usuario)
    #clean()
