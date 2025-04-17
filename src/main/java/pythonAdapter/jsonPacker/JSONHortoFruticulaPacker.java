package pythonAdapter.jsonPacker;

import data.carniceria.StockCarne;
import data.hortofruticola.HistoricoHortofruticola;
import data.hortofruticola.StockHortoFruticola;
import pythonAdapter.jsonConverter.JSONCarneConverter;
import pythonAdapter.jsonConverter.JSONHortoFruticolaConverter;

public class JSONHortoFruticulaPacker extends AbstractJSONPacker {
    public JSONHortoFruticulaPacker(){
        converter = new JSONHortoFruticolaConverter();
    }
}
