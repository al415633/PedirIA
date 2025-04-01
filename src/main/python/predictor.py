import pandas as pd
import json
import sys
import os
from prophet import Prophet
import logging
from sklearn.metrics import mean_absolute_error

# Configurar logs para que no se muestren
logger = logging.getLogger("cmdstanpy")
logger.addHandler(logging.NullHandler())
logger.propagate = False
logger.setLevel(logging.CRITICAL)


def main():
    # Leer entrada estándar
    input_data = sys.stdin.read()

    data = json.loads(input_data)

    # Leer CSV desde la ruta proporcionada
    csv_path = data["csv"]
    if not os.path.exists(csv_path):
        print(f"ERROR: No se encontró el archivo CSV en {csv_path}")
        return

    df_historico = pd.read_csv(csv_path)

    # Leer JSON desde la ruta proporcionada
    json_path = data["json"]
    if not os.path.exists(json_path):
        print(f"ERROR: No se encontró el archivo JSON en {json_path}")
        return

    with open(json_path, "r", encoding="utf-8") as f:
        json_data = json.load(f)

    # Extraer datos de stock
    df_stock = pd.DataFrame(json_data["stock_actual"])

    df_stock = df_stock.rename(columns={"nombre": "producto", "cantidad": "unidades"})
    df_stock["unidades"] = pd.to_numeric(df_stock["unidades"])

    # Convertir fechas en el histórico
    df_historico["ds"] = pd.to_datetime(df_historico["ds"])

    # Obtener productos únicos
    productos = df_historico["producto"].unique()

    dias_prediccion = 7
    restock = []

    for producto in productos:
        # Filtrar el DataFrame por producto
        df_producto = df_historico[df_historico["producto"] == producto][["ds", "y"]]
        print(df_producto)
        # Verificar si hay al menos 2 registros no nulos
        if df_producto.dropna().shape[0] < 2:
            print(f"Producto {producto} omitido: menos de 2 registros válidos.")
            continue

        # Crear el conjunto de entrenamiento y prueba
        df_train = df_producto.iloc[:-dias_prediccion].copy()
        df_test = df_producto.iloc[-dias_prediccion:]

        # Agregar el día de la semana como regresor
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

        # Renombrar y unir con datos reales
        df_test = df_test.rename(columns={"ds": "Fecha"})
        resultados = df_test.merge(forecast_test, on="Fecha")
        resultados["Ventas"] = resultados["Ventas"].round().astype(int)

        # Calcular el restock necesario
        total_predicho = resultados["Ventas"].sum()
        stock_actual = df_stock[df_stock["producto"] == producto]["unidades"].values
        stock_actual = stock_actual[0] if len(stock_actual) > 0 else 0
        unidades_restock = max(0, total_predicho - stock_actual)

        # Almacenar resultado
        restock.append(
            {
                "Producto": producto,
                "Stock Actual": stock_actual,
                "Ventas previstas": total_predicho,
                "Restock necesario": unidades_restock,
            }
        )

    # Crear respuesta JSON
    response = {"message": json.dumps(restock, ensure_ascii=False, indent=4)}
    print(json.dumps(response))


if __name__ == "__main__":
    main()
