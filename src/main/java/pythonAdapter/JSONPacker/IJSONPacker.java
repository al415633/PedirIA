package pythonAdapter.JSONPacker;

import data.StockProducto;

import java.io.IOException;
import java.util.List;

public interface IJSONPacker {
    String packageData(List<StockProducto> datos) throws IOException;

    void closeFiles();
}
