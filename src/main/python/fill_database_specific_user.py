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


def generar_ventas(id_producto, tabla, dias_anterioridad):
    fecha_inicio = datetime.now() - timedelta(days=dias_anterioridad)
    print(tabla)
    for i in range(dias_anterioridad):
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


def fill_historico(tipo, dias_anterioridad):
    global carnes_ids
    global frutas_ids
    global pescados_ids
    global negocio_id
    if tipo == "carne" or tipo == "otro":
        for carne_id in (carnes_ids):
            generar_ventas(carne_id, "HistoricoCarne", dias_anterioridad)
    if tipo == "pescado" or tipo == "otro":
        for pescado_id in pescados_ids:
            generar_ventas(pescado_id, "HistoricoPescado", dias_anterioridad)
    if tipo == "fruta" or tipo == "otro":
        for fruta_id in frutas_ids:
            generar_ventas(fruta_id, "HistoricoHortoFruticola", dias_anterioridad)

    # Confirmar cambios y cerrar conexión
    conn.commit()
    cur.close()
    conn.close()


# Método para eliminar solo los datos insertados por este script

def main():
    negocio_id = 1
    carnes_ids = [10, 11, 12]
    pescados_ids = [4, 5, 6]
    frutas_ids = [4, 5, 6]
    #carne, pescado, fruta, otro
    tipo_usuario = 'carne'
    dias_anterioridad = 356
    fill_historico(tipo_usuario, dias_anterioridad)


def clean():
    cur.execute("DELETE FROM Usuario WHERE id_usuario > 60")


if __name__ == "__main__":
    main()
    #clean()
