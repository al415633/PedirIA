package pythonAdapter.jsonPacker;

import com.google.gson.Gson;
import data.StockProducto;
import data.carniceria.StockCarne;
import org.json.simple.JSONObject;
import pythonAdapter.jsonConverter.JSONCarneConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JSONCarnePacker extends AbstractJSONPacker<StockCarne> {
    public JSONCarnePacker(){
        converter = new JSONCarneConverter();
    }
}
