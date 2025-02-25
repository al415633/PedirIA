package pythonAdapter;

import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileReader;
import java.util.Scanner;

class PythonManagerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void execPython1() {
        try {
            PythonManager manager = new PythonManager();
            String pythonFileDirection = "src/main/python/tests/test1.py";
            Scanner in = new Scanner(new FileReader("src/main/resources/testJSON1.json"));
//            Scanner in = new Scanner(new FileReader("src/main/java/org/example/testJSON.json"));

            StringBuilder input = new StringBuilder();
            String line;
            while (in.hasNextLine()) {
                line = in.nextLine();
                input.append(line);
            }
            String jsonInput = input.toString();
            Gson gson = new Gson();
            System.out.println(jsonInput);
            JSONObject answer = manager.execPython(pythonFileDirection, gson.fromJson(jsonInput, JSONObject.class));
            System.out.println(answer);
            assertFalse(answer.isEmpty());
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void sendCSVPython() {
        try {
            PythonManager manager = new PythonManager();
            String pythonFileDirection = "src/main/python/tests/test2.py";
            Scanner in = new Scanner(new FileReader("src/main/resources/ventas_historicas.csv"));
//            Scanner in = new Scanner(new FileReader("src/main/java/org/example/testJSON.json"));

            StringBuilder input = new StringBuilder();
            String line;
            while (in.hasNextLine()) {
                line = in.nextLine();
                input.append(line);
            }
            String csv = input.toString();
//            System.out.println(csv);
            Exception exception = null;
            JSONObject answer = manager.sendCSVPython(pythonFileDirection, csv);
            System.out.println(answer);
//            assertFalse(answer.isEmpty());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}