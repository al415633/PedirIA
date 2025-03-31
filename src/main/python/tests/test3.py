import sys
import json


def main():
    # print(sys.argv[1])
    # data = json.loads(sys.argv[1])  # Recibe argumentos desde Java
    input_data = sys.stdin.read()
    f = open(input_data, "r")
    # print(input_data)
    # data = json.loads(input_data)  # Convertir JSON a diccionario
    response = {"message": f"{f.read()}"}
    print(json.dumps(response))  # Devuelve un JSON como respuesta

if __name__ == "__main__":
    main()