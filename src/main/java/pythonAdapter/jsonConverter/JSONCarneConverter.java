package pythonAdapter.jsonConverter;
import data.carniceria.HistoricoCarne;
import data.carniceria.StockCarne;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class JSONCarneConverter extends AbstractJSONConverter {


//    public String extractHistoricStock(List<T> listaStock) {
//        StringBuilder stringBuilder = new StringBuilder("ds,producto,y");
//        stringBuilder.append(System.lineSeparator());
//
//        for (StockProducto stock : listaStock) {
//            stringBuilder
//                    .append(stock.getFechaIngreso().toString())
//                    .append(",")  // 🔥 Eliminamos el espacio después de la coma
//                    .append(stock.getProducto().getNombre().trim())  // 🔥 Eliminamos espacios extra en el nombre
//                    .append(",")
//                    .append(stock.getCantidad())
//                    .append(System.lineSeparator());
//        }
//
//        String finalString = stringBuilder.toString();
//        System.out.println("csv: " + finalString);
//        return finalString;
//    }
//
//    public String extractCurrentStock(List<StockProducto> listaStock) {
//        Map<String, BigDecimal> ProductoMap = new HashMap<>();
//
//        StringBuilder stringBuilder = new StringBuilder("{\"stock_actual\":[");
//
//        for (StockProducto stock : listaStock) {
//            if (stock.getFechaIngreso().isBefore(LocalDate.now()) && stock.getFechaVencimiento().isAfter(LocalDate.now())) {
//                ProductoMap.compute(
//                        stock.getProducto().getNombre().trim(),  // 🔥 Eliminamos espacios extra en el nombre
//                        (key, old) -> (old == null) ? stock.getCantidad() : old.add(stock.getCantidad())
//                );
//            }
//        }
//
//        for (Map.Entry<String, BigDecimal> par : ProductoMap.entrySet()) {
//            stringBuilder
//                    .append("{")
//                    .append("\"nombre\":\"")
//                    .append(par.getKey().trim())  // 🔥 Eliminamos espacios extra en los nombres
//                    .append("\",")
//                    .append("\"cantidad\":\"")
//                    .append(par.getValue())
//                    .append("\"},");
//        }
//
//        if (!ProductoMap.isEmpty()) {
//            stringBuilder.deleteCharAt(stringBuilder.length() - 1);  // 🔥 Eliminamos la última coma
//        }
//
//        stringBuilder.append("]}");
//        return stringBuilder.toString();
//    }

}
