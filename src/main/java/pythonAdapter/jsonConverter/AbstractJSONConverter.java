package pythonAdapter.jsonConverter;

import data.HistoricoProducto;
import data.StockProducto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractJSONConverter {
    public String extractHistoricStock(List<HistoricoProducto> listaHistorico) {
        StringBuilder stringBuilder = new StringBuilder("ds,producto,y");
        stringBuilder.append(System.lineSeparator());

        for (HistoricoProducto stock : listaHistorico) { //Crear el CSV
            stringBuilder
                    .append(stock.getFechaIngreso().toString())
                    .append(",")
                    .append(stock.getProducto().getNombre().trim())
                    .append(",")
                    .append(stock.getCantidad())
                    .append(System.lineSeparator());
        }

        String finalString = stringBuilder.toString();
        System.out.println(("csv: " + finalString));
        return finalString;
    }

    public String extractCurrentStock(List<StockProducto> listaStock) {
        Map<String, BigDecimal> ProductoMap = new HashMap<>();

        StringBuilder stringBuilder = new StringBuilder("{\"stock_actual\":[");

        for (data.StockProducto stock : listaStock) {
            if (stock.getFechaIngreso().isBefore(LocalDate.now()) && stock.getFechaVencimiento().isAfter(LocalDate.now())) {
                ProductoMap.compute(
                        stock.getProducto().getNombre().trim(),  // 🔥 Eliminamos espacios extra en el nombre
                        (key, old) -> (old == null) ? stock.getCantidad() : old.add(stock.getCantidad())
                );
            }
        }

        for (Map.Entry<String, BigDecimal> par : ProductoMap.entrySet()) {
            stringBuilder
                    .append("{")
                    .append("\"nombre\":\"")
                    .append(par.getKey().trim())  // 🔥 Eliminamos espacios extra en los nombres
                    .append("\",")
                    .append("\"cantidad\":\"")
                    .append(par.getValue())
                    .append("\"},");
        }

        if (!ProductoMap.isEmpty()) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);  // 🔥 Eliminamos la última coma
        }

        stringBuilder.append("]}");
        System.out.println(("json: " + stringBuilder.toString()));

        return stringBuilder.toString();
    }
}
