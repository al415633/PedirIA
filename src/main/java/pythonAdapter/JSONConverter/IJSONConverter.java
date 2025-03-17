package pythonAdapter.JSONConverter;

import data.StockCarne;

import java.util.List;

public interface IJSONConverter<T> {
    String extractHistoricStock(List<T> listaStock);
    String extractCurrentStock(List<T> listaStock);


}
