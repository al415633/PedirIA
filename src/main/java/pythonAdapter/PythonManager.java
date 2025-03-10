package pythonAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import data.StockCarne;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class PythonManager {


    public JSONObject execPython(String pythonFilePath, JSONObject command){
        try {
//            String pythonScript = "src/main/java/org/example/main.py";  // Ruta del script Python
//            String jsonInput = "{\"nombre\": \"Mundo\"}";  // JSON sin caracteres escapados
            String line;


            ProcessBuilder pb = new ProcessBuilder("python3", pythonFilePath);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            System.out.println(command.toJSONString());
            System.out.println(command.toString());
            // Escribir el JSON en la entrada estándar (stdin) de Python
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write(command.toJSONString());
                writer.flush();  // Asegurar que los datos se envíen
            }

            // Leer la salida del proceso (respuesta del script Python)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            Gson gson = new Gson();

            process.waitFor(); // Espera a que el proceso termine
            System.out.println("Respuesta de Python: " + output.toString());
            return gson.fromJson(output.toString(), JSONObject.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public JSONObject sendCSVPython(String pythonFilePath, String csv){
        try {
            String line;
            ProcessBuilder pb = new ProcessBuilder("python3", pythonFilePath);
            pb.redirectErrorStream(true);
            Process process = pb.start();
//            System.out.println(csv);
            // Escribir el JSON en la entrada estándar (stdin) de Python
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write("{ \"csv\": \"" + csv + "\"}");
                writer.flush();  // Asegurar que los datos se envíen
            }
            catch(Exception e){
                System.out.println("es esto");
                e.printStackTrace();
            }

            // Leer la salida del proceso (respuesta del script Python)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            Gson gson = new Gson();

            process.waitFor(); // Espera a que el proceso termine
//            System.out.println("Respuesta de Python: " + output.toString());
            return gson.fromJson(output.toString(), JSONObject.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject sendPythonJSONAsFile(String pythonFilePath, JSONObject json){

        try {
            File file = File.createTempFile(
                    "temp", ".json",
                    new File(
                            "src/main/resources/temp"));
            System.out.println(file.getName());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getPath()))) {
                writer.write(json.toJSONString());
            }
            String line;
            ProcessBuilder pb = new ProcessBuilder("python3", pythonFilePath);
            pb.redirectErrorStream(true);
            Process process = pb.start();
//            System.out.println(json);
            // Escribir el JSON en la entrada estándar (stdin) de Python
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write(file.getPath());
                writer.flush();  // Asegurar que los datos se envíen
            }
            catch(Exception e){
                System.out.println("es esto");
                e.printStackTrace();
            }

            // Leer la salida del proceso (respuesta del script Python)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            System.out.println(output);
            Gson gson = new Gson();

            process.waitFor(); // Espera a que el proceso termine
            if (!file.delete())
                file.deleteOnExit();
            return gson.fromJson(output.toString(), JSONObject.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public JSONObject sendPythonSeparateJSONAndCSV(String pythonFilePath, JSONObject json, String csv){

        try {
            File jsonFile = File.createTempFile(
                    "temp", ".json",
                    new File(
                            "src/main/resources/temp"));
            System.out.println(jsonFile.getName());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile.getPath()))) {
                writer.write(json.toJSONString());
            }
            File csvFile = File.createTempFile(
                    "temp", ".csv",
                    new File(
                            "src/main/resources/temp"));
            System.out.println(csvFile.getName());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile.getPath()))) {
                writer.write(csv);
            }
            String line;
            String directionsJSON = "{\"csv\": \"" + csvFile.getPath() + "\", \"json\": \"" + jsonFile.getPath() + "\"}";
            System.out.println("directionsJSON: " + directionsJSON);
            ProcessBuilder pb = new ProcessBuilder("python", pythonFilePath);
            pb.redirectErrorStream(true);
            Process process = pb.start();
//            System.out.println(json);
            // Escribir el JSON en la entrada estándar (stdin) de Python
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write(directionsJSON);
                writer.flush();  // Asegurar que los datos se envíen
            }
            catch(Exception e){
                System.out.println("es esto");
                e.printStackTrace();
            }

            // Leer la salida del proceso (respuesta del script Python)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            System.out.println("output: " + output);
            Gson gson = new Gson();

            process.waitFor(); // Espera a que el proceso termine
            if (!jsonFile.delete())
                jsonFile.deleteOnExit();
            if (!csvFile.delete())
                csvFile.deleteOnExit();
            return gson.fromJson(output.toString(), JSONObject.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public JSONObject sendPythonInfo(String pythonFilePath, List<StockCarne> datos){
        JSONConverter converter = new JSONConverter();

        String currentStock = "{" + converter.extractCurrentStockCarne(datos) + "}";

        JSONObject json = new Gson().fromJson(currentStock, JSONObject.class);

        String csv = converter.extractHistoricStockCarne(datos);

        try {
            File jsonFile = File.createTempFile(
                    "temp", ".json",
                    new File(
                            "src/main/resources/temp"));
            System.out.println(jsonFile.getName());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile.getPath()))) {
                writer.write(json.toJSONString());
            }
            File csvFile = File.createTempFile(
                    "temp", ".csv",
                    new File(
                            "src/main/resources/temp"));
            System.out.println(csvFile.getName());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile.getPath()))) {
                writer.write(csv);
            }
            String line;
            String directionsJSON = "{\"csv\": \"" + csvFile.getPath() + "\", \"json\": \"" + jsonFile.getPath() + "\"}";
            System.out.println("directionsJSON: " + directionsJSON);
            ProcessBuilder pb = new ProcessBuilder("python", pythonFilePath);
            pb.redirectErrorStream(true);
            Process process = pb.start();
//            System.out.println(json);
            // Escribir el JSON en la entrada estándar (stdin) de Python
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write(directionsJSON);
                writer.flush();  // Asegurar que los datos se envíen
            }
            catch(Exception e){
                System.out.println("es esto");
                e.printStackTrace();
            }

            // Leer la salida del proceso (respuesta del script Python)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            System.out.println("output: " + output);
            Gson gson = new Gson();

            process.waitFor(); // Espera a que el proceso termine
            if (!jsonFile.delete())
                jsonFile.deleteOnExit();
            if (!csvFile.delete())
                csvFile.deleteOnExit();
            return gson.fromJson(output.toString(), JSONObject.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
