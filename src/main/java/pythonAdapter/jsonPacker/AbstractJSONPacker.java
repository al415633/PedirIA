package pythonAdapter.jsonPacker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;

import com.google.gson.Gson;

import data.HistoricoProducto;
import data.StockProducto;
import pythonAdapter.jsonConverter.AbstractJSONConverter;

public abstract class AbstractJSONPacker {
    AbstractJSONConverter converter;
    File jsonFile;
    File csvFile;
    public String packageData(List<StockProducto> datosStock, List<HistoricoProducto> datosHistorico) throws IOException {
//        AbstractJSONConverter<T> converter = new JSONCarneConverter();

        String currentStock = converter.extractCurrentStock(datosStock);

        JSONObject json = new Gson().fromJson(currentStock, JSONObject.class);

        String csv = converter.extractHistoricStock(datosHistorico);

        try {
            jsonFile = File.createTempFile(
                    "temp", ".json",
                    new File(
                            "src/main/resources/temp"));
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
            // jsonFile.deleteOnExit();
            // csvFile.deleteOnExit();
            return "{\"csv\": \"" + csvFile.getAbsolutePath().replace("\\", "/") + "\", \"json\": \"" + jsonFile.getAbsolutePath().replace("\\", "/") + "\"}";
        }catch(Exception e){
            throw e;
        }
    }
//    public String packageData(List<T> datos) throws IOException {
////        AbstractJSONConverter<T> converter = new JSONCarneConverter();
//
//        String currentStock = converter.extractCurrentStock(datos);
//
//        JSONObject json = new Gson().fromJson(currentStock, JSONObject.class);
//
//        String csv = converter.extractHistoricStock(datos);
//
//        try {
//            jsonFile = File.createTempFile(
//                    "temp", ".json",
//                    new File(
//                            "src/main/resources/temp"));
//            System.out.println(jsonFile.getName());
//            try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile.getPath()))) {
//                writer.write(json.toJSONString());
//            }
//            csvFile = File.createTempFile(
//                    "temp", ".csv",
//                    new File(
//                            "src/main/resources/temp"));
//            try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile.getPath()))) {
//                writer.write(csv);
//            }
//            String line;
//            jsonFile.deleteOnExit();
//            csvFile.deleteOnExit();
//            return "{\"csv\": \"" + csvFile.getAbsolutePath().replace("\\", "/") + "\", \"json\": \"" + jsonFile.getAbsolutePath().replace("\\", "/") + "\"}";
//        }catch(Exception e){
//            System.out.println("es esto");
//            e.printStackTrace();
//            throw e;
//        }
//    }

    public void closeFiles() {
        /*if (!jsonFile.delete())
            jsonFile.deleteOnExit();
        if (!csvFile.delete())
            csvFile.deleteOnExit();
        */
    }

}
