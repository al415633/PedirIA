package pythonAdapter.jsonPacker;

import com.google.gson.Gson;
import data.StockProducto;
import org.json.simple.JSONObject;
import pythonAdapter.jsonConverter.AbstractJSONConverter;
import pythonAdapter.jsonConverter.JSONCarneConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public abstract class AbstractJSONPacker<T extends StockProducto> {
    AbstractJSONConverter<T> converter;
    File jsonFile;
    File csvFile;

    public String packageData(List<T> datos) throws IOException {
//        AbstractJSONConverter<T> converter = new JSONCarneConverter();

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
            return "{\"csv\": \"" + csvFile.getAbsolutePath().replace("\\", "/") + "\", \"json\": \"" + jsonFile.getAbsolutePath().replace("\\", "/") + "\"}";
        }catch(Exception e){
            System.out.println("es esto");
            e.printStackTrace();
            throw e;
        }
    }

    public void closeFiles() {
        if (!jsonFile.delete())
            jsonFile.deleteOnExit();
        if (!csvFile.delete())
            csvFile.deleteOnExit();
    }

}
