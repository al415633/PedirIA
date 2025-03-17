package pythonAdapter.JSONConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.StockCarne;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JSONCarneConverter implements IJSONConverter<StockCarne> {

    public String extractHistoricStock(List<StockCarne> listaStock) {
        StringBuilder stringBuilder = new StringBuilder("ds,producto,y");
        stringBuilder.append(System.lineSeparator());

        for (StockCarne stock : listaStock) {
            stringBuilder
                    .append(stock.getFechaIngreso().toString())
                    .append(",")  // 🔥 Eliminamos el espacio después de la coma
                    .append(stock.getCarne().getNombre().trim())  // 🔥 Eliminamos espacios extra en el nombre
                    .append(",")
                    .append(stock.getCantidad())
                    .append(System.lineSeparator());
        }

        String finalString = stringBuilder.toString();
        System.out.println("csv: " + finalString);
        return finalString;
    }

    public String extractCurrentStock(List<StockCarne> listaStock) {
        Map<String, BigDecimal> carneMap = new HashMap<>();

        StringBuilder stringBuilder = new StringBuilder("{\"stock_actual\":[");

        for (StockCarne stock : listaStock) {
            if (stock.getFechaIngreso().isBefore(LocalDate.now()) && stock.getFechaVencimiento().isAfter(LocalDate.now())) {
                carneMap.compute(
                        stock.getCarne().getNombre().trim(),  // 🔥 Eliminamos espacios extra en el nombre
                        (key, old) -> (old == null) ? stock.getCantidad() : old.add(stock.getCantidad())
                );
            }
        }

        for (Map.Entry<String, BigDecimal> par : carneMap.entrySet()) {
            stringBuilder
                    .append("{")
                    .append("\"nombre\":\"")
                    .append(par.getKey().trim())  // 🔥 Eliminamos espacios extra en los nombres
                    .append("\",")
                    .append("\"cantidad\":\"")
                    .append(par.getValue())
                    .append("\"},");
        }

        if (!carneMap.isEmpty()) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);  // 🔥 Eliminamos la última coma
        }

        stringBuilder.append("]}");
        return stringBuilder.toString();
    }
}
