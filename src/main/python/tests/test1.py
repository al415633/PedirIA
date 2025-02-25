import sys
import json


def main():
    # print(sys.argv[1])
    # data = json.loads(sys.argv[1])  # Recibe argumentos desde Java
    # response = {"message": f"Hola, {data['nombre']}!"}
    # print(json.dumps(response))  # Devuelve un JSON como respuesta

    input_data = sys.stdin.read()
    data = json.loads(input_data)  # Convertir JSON a diccionario
    extras = data.get("extra", [])
    mensaje = ""
    for extra in extras:
        vida = extra.get("vida", "")
        muerte = extra.get("muerte", "")
        mensaje += f"{vida} es muerte, {muerte} es vida!"

    response = {"message": f"Hola, {data['nombre']}!, mi trabajo es {data['ocupacion']}",
                "extra": f"Hay, {mensaje}!"}
    print(json.dumps(response))  # Devolver respuesta en JSON


if __name__ == "__main__":
    main()