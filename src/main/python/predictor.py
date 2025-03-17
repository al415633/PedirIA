import pandas as pd
import json
from prophet import Prophet
import logging
from sklearn.metrics import mean_absolute_error
import sys
from io import StringIO

# Configurar logs para que no se muestren
logger = logging.getLogger("cmdstanpy")
logger.addHandler(logging.NullHandler())
logger.propagate = False
logger.setLevel(logging.CRITICAL)

# Verificar argumentos
if len(sys.argv) != 2:
    print("Uso: python script.py <datos.json>")
    sys.exit(1)

# Cargar el JSON desde el argumento
with open(sys.argv[1], "r", encoding="utf-8") as f:
    data = json.load(f)

# Convertir el histórico de ventas (cadena CSV dentro del JSON) en un DataFrame
df_historico = pd.read_csv(StringIO(data["historicos"]))

# Convertir stock_actual en un DataFrame
df_stock = pd.DataFrame(data["stock_actual"])
df_stock = df_stock.rename(columns={"nombre": "producto", "cantidad": "unidades"})
df_stock["unidades"] = pd.to_numeric(df_stock["unidades"])

# Convertir fechas en el histórico
df_historico["fecha"] = pd.to_datetime(df_historico["fecha"])

# Obtener productos únicos
productos = df_historico["producto"].unique()

dias_prediccion = 7

# Diccionario para almacenar restock
restock = []

for i, producto in enumerate(productos):
    print(f"\nPredicción de ventas para: {producto}\n")

    # Filtrar el DataFrame por producto
    df_producto = df_historico[df_historico["producto"] == producto][
        ["fecha", "ventas"]
    ]
    df_producto = df_producto.rename(columns={"fecha": "ds", "ventas": "y"})

    # Dividir el DataFrame en datos de entrenamiento y prueba
    df_train = df_producto.iloc[
        :-dias_prediccion
    ].copy()  # Copiar para evitar el warning
    df_test = df_producto.iloc[-dias_prediccion:]

    # Agregar el día de la semana (dow) al conjunto de entrenamiento
    df_train["dow"] = df_train["ds"].dt.weekday

    # Crear y ajustar el modelo Prophet
    model = Prophet(
        yearly_seasonality=True,
        weekly_seasonality=True,
        daily_seasonality=False,
        changepoint_prior_scale=0.05,
        interval_width=0.80,
    )
    model.add_seasonality(name="weekly", period=7, fourier_order=5)
    model.add_regressor("dow")
    model.fit(df_train)

    # Realizar la predicción
    future = model.make_future_dataframe(periods=dias_prediccion, freq="D")
    future["dow"] = future["ds"].dt.weekday
    forecast = model.predict(future)

    # Obtener las predicciones para el período de prueba
    forecast_test = forecast[-dias_prediccion:][["ds", "yhat"]].rename(
        columns={"ds": "Fecha", "yhat": "Ventas"}
    )

    # Renombrar la columna 'ds' en df_test para hacer el merge
    df_test = df_test.rename(columns={"ds": "Fecha"})

    # Verificar que la columna 'Fecha' esté presente en ambas DataFrames
    if "Fecha" in df_test.columns and "Fecha" in forecast_test.columns:
        resultados = df_test.merge(forecast_test, on="Fecha")
    else:
        print("Error: 'Fecha' no encontrada en ambas DataFrames.")
        sys.exit(1)

    # Redondear las predicciones y calcular el MAE
    resultados["Ventas"] = resultados["Ventas"].round().astype(int)

    mae = mean_absolute_error(resultados["y"], resultados["Ventas"])
    print(f"MAE: {mae:.2f} unidades")

    # Agregar la columna 'Producto' para la impresión
    resultados["Producto"] = producto

    # Seleccionar y reordenar las columnas
    resultados = resultados[["Fecha", "Producto", "Ventas"]]

    print(resultados.to_string(index=False))

    # Calcular la cantidad total de ventas predicha y la diferencia con el stock actual
    total_predicho = resultados["Ventas"].sum()

    # Obtener stock actual para el producto
    stock_actual = df_stock[df_stock["producto"] == producto]["unidades"].values
    stock_actual = (
        stock_actual[0] if len(stock_actual) > 0 else 0
    )  # 0 si no se encuentra el producto

    # Calcular el restock necesario
    unidades_restock = max(0, total_predicho - stock_actual)

    # Almacenar los resultados en el diccionario de restock
    restock.append(
        {
            "Producto": producto,
            "Stock Actual": stock_actual,
            "Ventas previstas": total_predicho,
            "Restock necesario": unidades_restock,
        }
    )

    print("\n" + "=" * 50)

# Generar los datos de predicción en formato JSON
df_restock = pd.DataFrame(restock)

# Guardar los datos en un archivo JSON
with open("src/main/resources/datasets/restock_data.json", "w", encoding="utf-8") as f:
    json.dump(df_restock.to_dict(orient="records"), f, ensure_ascii=False, indent=4)

print(
    "\nLos datos de restock han sido guardados en 'src/main/resources/datasets/restock_data.json'"
)
