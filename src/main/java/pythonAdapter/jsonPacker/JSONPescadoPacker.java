package pythonAdapter.jsonPacker;

import data.hortofruticola.StockHortoFruticola;
import data.pescaderia.StockPescado;
import pythonAdapter.jsonConverter.JSONHortoFruticolaConverter;
import pythonAdapter.jsonConverter.JSONPescadoConverter;

public class JSONPescadoPacker extends AbstractJSONPacker<StockPescado> {
    public JSONPescadoPacker(){
        converter = new JSONPescadoConverter();
    }
}
