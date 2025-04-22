import pandas as pd
import json
import sys
import os
import numpy as np
from prophet import Prophet
import logging

# Configurar logs para que no se muestren
logger = logging.getLogger("cmdstanpy")
logger.addHandler(logging.NullHandler())
logger.propagate = False
logger.setLevel(logging.CRITICAL)


# Función para convertir tipos NumPy a tipos nativos de Python
def convert_numpy(obj):
    if isinstance(obj, (np.integer, np.int64)):
        return int(obj)
    elif isinstance(obj, (np.floating, np.float64)):
        return float(obj)
    elif isinstance(obj, np.ndarray):
        return obj.tolist()
    else:
        raise TypeError(f"Object of type {type(obj).__name__} is not JSON serializable")


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
        df_historico_producto = df_historico[df_historico["producto"] == producto][
            ["ds", "y"]
        ].dropna()
        if df_historico_producto.shape[0] < dias_prediccion + 1:
            print(f"Producto {producto} omitido: no hay suficientes datos.")
            continue

        # Ordenar por fecha
        df_historico_producto = df_historico_producto.sort_values("ds")

        # Separar últimos 7 días como test y el resto como entrenamiento
        df_test = df_historico_producto.tail(dias_prediccion).copy()
        df_train = df_historico_producto.iloc[:-dias_prediccion].copy()

        # Agregar día de la semana como regresor
        df_train["dow"] = df_train["ds"].dt.weekday
        df_test["dow"] = df_test["ds"].dt.weekday

        # Crear y entrenar el modelo
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

        # Crear DataFrame future con fechas exactas del test
        future = df_test[["ds", "dow"]].copy()
        forecast = model.predict(future)

        # Preparar predicciones
        forecast_test = forecast[["ds", "yhat"]].rename(
            columns={"ds": "Fecha", "yhat": "Predicción"}
        )
        forecast_test["Fecha"] = pd.to_datetime(forecast_test["Fecha"]).dt.date

        resultados = df_test.rename(columns={"ds": "Fecha", "y": "Ventas Reales"})
        resultados["Fecha"] = pd.to_datetime(resultados["Fecha"]).dt.date

        resultados = resultados.merge(forecast_test, on="Fecha")
        resultados["Predicción"] = (
            resultados["Predicción"].clip(lower=0).round().astype(int)
        )
        resultados = resultados.drop(columns=["dow"], errors="ignore")

        total_predicho = resultados["Predicción"].sum()
        stock_actual = df_stock[df_stock["producto"] == producto]["unidades"].values
        stock_actual = stock_actual[0] if len(stock_actual) > 0 else 0
        unidades_restock = max(0, total_predicho - stock_actual)

        restock.append(
            {
                "Producto": str(producto),
                "Stock Actual": int(stock_actual),
                "Ventas previstas": int(total_predicho),
                "Restock necesario": int(unidades_restock),
            }
        )

    print(json.dumps({"message": restock}, ensure_ascii=True, indent=4))


if __name__ == "__main__":
    main()
