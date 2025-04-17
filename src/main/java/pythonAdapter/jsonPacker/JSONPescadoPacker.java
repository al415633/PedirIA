package pythonAdapter.jsonPacker;

import data.hortofruticola.StockHortoFruticola;
import data.pescaderia.HistoricoPescado;
import data.pescaderia.StockPescado;
import pythonAdapter.jsonConverter.JSONHortoFruticolaConverter;
import pythonAdapter.jsonConverter.JSONPescadoConverter;

public class JSONPescadoPacker extends AbstractJSONPacker {
    public JSONPescadoPacker(){
        converter = new JSONPescadoConverter();
    }
}
