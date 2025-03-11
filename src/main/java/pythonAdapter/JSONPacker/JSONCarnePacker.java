package pythonAdapter.JSONPacker;

import com.google.gson.Gson;
import data.StockCarne;
import org.json.simple.JSONObject;
import pythonAdapter.JSONConverter.IJSONConverter;
import pythonAdapter.JSONConverter.JSONCarneConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JSONCarnePacker implements IJSONPacker<StockCarne> {
    File jsonFile;
    File csvFile;

    @Override
    public String packageData(List<StockCarne> datos) throws IOException {
        IJSONConverter<StockCarne> converter = new JSONCarneConverter();

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
            jsonFile.deleteOnExit();
            csvFile.deleteOnExit();
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
