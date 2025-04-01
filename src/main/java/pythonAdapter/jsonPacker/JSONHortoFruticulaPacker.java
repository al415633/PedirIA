package pythonAdapter.jsonPacker;

import data.carniceria.StockCarne;
import data.hortofruticola.StockHortoFruticola;
import pythonAdapter.jsonConverter.JSONCarneConverter;
import pythonAdapter.jsonConverter.JSONHortoFruticolaConverter;

public class JSONHortoFruticulaPacker extends AbstractJSONPacker<StockHortoFruticola> {
    public JSONHortoFruticulaPacker(){
        converter = new JSONHortoFruticolaConverter();
    }
}
