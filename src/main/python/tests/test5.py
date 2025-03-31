import sys
import json


def main():
    input_data = sys.stdin.read()
    trueJSON = repr(input_data)
    trueJSON = trueJSON.removeprefix("'").removesuffix("'")
    # print(trueJSON)
    data = json.loads(trueJSON)  # Convertir JSON a diccionario

    og_csv_file = open(data["csv"], "r")
    # print(input_data)
    # data = json.loads(input_data)  # Convertir JSON a diccionario
    content_csv = og_csv_file.read()
    new_csv_file = open("src/main/resources/temp/csvTestFile.csv", "w")
    new_csv_file.write(content_csv)
    og_csv_file.close()
    new_csv_file.close()

    og_json_file = open(data["json"], "r")
    # print(input_data)
    # data = json.loads(input_data)  # Convertir JSON a diccionario
    content_json = og_json_file.read()
    new_json_file = open("src/main/resources/temp/jsonTestFile.json", "w")
    new_json_file.write(content_json)
    og_json_file.close()
    new_json_file.close()

    response = {"message": f"{data['json']}"}
    print(json.dumps(response))  # Devuelve un JSON como respuesta


if __name__ == "__main__":
    main()
