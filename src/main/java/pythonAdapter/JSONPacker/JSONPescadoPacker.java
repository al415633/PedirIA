package pythonAdapter.JSONPacker;

import com.google.gson.Gson;
import data.StockPescado;
import org.json.simple.JSONObject;
import pythonAdapter.JSONConverter.IJSONConverter;
import pythonAdapter.JSONConverter.JSONPescadoConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JSONPescadoPacker implements IJSONPacker<StockPescado> {
    File jsonFile;
    File csvFile;

    @Override
    public String packageData(List<StockPescado> datos) throws IOException {
        IJSONConverter<StockPescado> converter = new JSONPescadoConverter();

        String currentStock = converter.extractCurrentStock(datos);

        JSONObject json = new Gson().fromJson(currentStock, JSONObject.class);

        String csv = converter.extractHistoricStock(datos);

        try {
            jsonFile = File.createTempFile(
                    "temp", ".json",
                    new File(
                            "src/main/resources/temp"));
            System.out.println(jsonFile.getName());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile.getPath()))) {
                writer.write(json.toJSONString());
            }
            csvFile = File.createTempFile(
                    "temp", ".csv",
                    new File(
                            "src/main/resources/temp"));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile.getPath()))) {
                writer.write(csv);
            }
            String line;
            return "{\"csv\": \"" + csvFile.getPath() + "\", \"json\": \"" + jsonFile.getPath() + "\"}";
    }catch(Exception e){
            System.out.println("es esto");
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void closeFiles() {
        if (!jsonFile.delete())
            jsonFile.deleteOnExit();
        if (!csvFile.delete())
            csvFile.deleteOnExit();
    }
}
