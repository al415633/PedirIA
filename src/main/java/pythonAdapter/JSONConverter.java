package pythonAdapter;

import com.google.gson.Gson;
import data.StockCarne;
import jakarta.enterprise.context.ApplicationScoped;
import org.json.simple.JSONObject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class JSONConverter {

    public String extractHistoricStockCarne(List<StockCarne> listaStock){
        StringBuilder stringBuilder = new StringBuilder("ds,producto,y");
        stringBuilder.append(System.lineSeparator());
        for (StockCarne stock : listaStock){
            stringBuilder
                    .append(stock.getFechaIngreso().toString())
                    .append(", ")
                    .append(stock.getCarne().getNombre())
                    .append(", ")
                    .append(stock.getCantidad())
                    .append(System.lineSeparator());

        }
        String finalString = stringBuilder.toString();
        System.out.println("csv: " + finalString);
        return finalString;
    }
    public JSONObject extractMapCurrentStockCarne(List<StockCarne> listaStock){
        Map<String, BigDecimal> carneMap = new HashMap<String, BigDecimal>();
//        StringBuilder stringBuilder = new StringBuilder("ds, producto\n");

        for (StockCarne stock : listaStock){
            if (stock.getFechaIngreso().isBefore(LocalDate.now()) && stock.getFechaVencimiento().isAfter(LocalDate.now()))
                carneMap.compute(stock.getCarne().getNombre(),(key, old)-> (old == null) ? stock.getCantidad() : old.add(stock.getCantidad()));
        }

//        for (Map.Entry<String,BigDecimal> par: carneMap.entrySet()){
//            stringBuilder
//                    .append(par.getKey())
//                    .append(", ")
//                    .append(par.getValue())
//                    .append("\n");
//        }
        Gson gson = new Gson();
        return gson.fromJson(JSONObject.toJSONString(carneMap), JSONObject.class);
//        System.out.println( JSONObject.toJSONString(carneMap));



//        String finalString = stringBuilder.toString();
//        System.out.println(finalString);
//        return null;
    }

    public String extractCurrentStockCarne(List<StockCarne> listaStock){
        Map<String, BigDecimal> carneMap = new HashMap<String, BigDecimal>();
        StringBuilder stringBuilder = new StringBuilder("\"stock_actual\":[");

        for (StockCarne stock : listaStock){
            if (stock.getFechaIngreso().isBefore(LocalDate.now()) && stock.getFechaVencimiento().isAfter(LocalDate.now()))
                carneMap.compute(stock.getCarne().getNombre(),(key, old)-> (old == null) ? stock.getCantidad() : old.add(stock.getCantidad()));
        }
        for (Map.Entry<String,BigDecimal> par: carneMap.entrySet()){
            stringBuilder
                    .append("{")
                    .append("\"nombre\":\"")
                    .append(par.getKey())
                    .append("\", ")
                    .append("\"cantidad\":\"")
                    .append(par.getValue())
                    .append("\"},");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        stringBuilder.append("]");
        Gson gson = new Gson();
//        System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
//        System.out.println( JSONObject.toJSONString(carneMap));



//        String finalString = stringBuilder.toString();
//        System.out.println(finalString);
//        return null;
    }

    public JSONObject preparePythonMessage(List<StockCarne> listaStock){
        StringBuilder stringBuilder = new StringBuilder();
        String currentStock = extractCurrentStockCarne(listaStock);
        String historicosCSV = extractHistoricStockCarne(listaStock);
        stringBuilder
                .append("{")
                .append(System.lineSeparator())
                .append(currentStock)
                .append(",")
                .append(System.lineSeparator())
                .append("\"historicos\":")
                .append(System.lineSeparator())
                .append("\"")
                .append(historicosCSV)
                .append("\"")
                .append("}");

//        System.out.println(stringBuilder);
        Gson gson = new Gson();
        return gson.fromJson(stringBuilder.toString(), JSONObject.class);
    }


}
