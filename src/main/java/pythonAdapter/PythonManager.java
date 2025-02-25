package pythonAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.simple.JSONObject;

import java.io.*;
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
}
