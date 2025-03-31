import sys
import json


def main():
    # print(sys.argv[1])
    # data = json.loads(sys.argv[1])  # Recibe argumentos desde Java
    input_data = sys.stdin.read()
    f1 = open(input_data, "r")
    # print(input_data)
    # data = json.loads(input_data)  # Convertir JSON a diccionario
    content = f1.read()
    f2 = open("src/main/resources/temp/pythonTest.txt", "w")
    f2.write(content)
    f2.close()
    response = {"message": f"{f1.read()}"}
    print(json.dumps(response))  # Devuelve un JSON como respuesta

if __name__ == "__main__":
    main()