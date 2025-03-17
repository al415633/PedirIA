package pythonAdapter.JSONPacker;

import java.io.IOException;
import java.util.List;

public interface IJSONPacker<T> {
    String packageData(List<T> datos) throws IOException;

    void closeFiles();
}
