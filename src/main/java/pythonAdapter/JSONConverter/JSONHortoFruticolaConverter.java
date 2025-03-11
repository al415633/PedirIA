package pythonAdapter.JSONConverter;

import data.StockHortoFruticola;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class JSONHortoFruticolaConverter implements IJSONConverter<StockHortoFruticola>{
    public String extractHistoricStock(List<StockHortoFruticola> listaStock) {
        StringBuilder stringBuilder = new StringBuilder("ds,producto,y");
        stringBuilder.append(System.lineSeparator());
        for (StockHortoFruticola stock : listaStock) {
            stringBuilder
                    .append(stock.getFechaIngreso().toString())
                    .append(", ")
                    .append(stock.getHortoFruticola().getNombre())
                    .append(", ")
                    .append(stock.getCantidad())
                    .append(System.lineSeparator());

        }
        String finalString = stringBuilder.toString();
        System.out.println("csv: " + finalString);
        return finalString;
    }

    public String extractCurrentStock(List<StockHortoFruticola> listaStock) {
        Map<String, BigDecimal> carneMap = new HashMap<String, BigDecimal>();
        StringBuilder stringBuilder = new StringBuilder("{\"stock_actual\":[");

        for (StockHortoFruticola stock : listaStock) {
            if (stock.getFechaIngreso().isBefore(LocalDate.now()) && stock.getFechaVencimiento().isAfter(LocalDate.now()))
                carneMap.compute(stock.getHortoFruticola().getNombre(), (key, old) -> (old == null) ? stock.getCantidad() : old.add(stock.getCantidad()));
        }
        for (Map.Entry<String, BigDecimal> par : carneMap.entrySet()) {
            stringBuilder
                    .append("{")
                    .append("\"nombre\":\"")
                    .append(par.getKey())
                    .append("\", ")
                    .append("\"cantidad\":\"")
                    .append(par.getValue())
                    .append("\"},");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("]}");
        return stringBuilder.toString();

    }
}
