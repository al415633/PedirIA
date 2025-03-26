package pythonAdapter.JSONConverter;

import data.Producto;
import data.StockProducto;

import java.util.List;

public interface IJSONConverter {
    String extractHistoricStock(List<StockProducto> listaStock);
    String extractCurrentStock(List<StockProducto> listaStock);


}
