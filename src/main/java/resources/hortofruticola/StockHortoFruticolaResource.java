package resources.hortofruticola;

import data.hortofruticola.StockHortoFruticola;
import data.hortofruticola.HistoricoHortofruticola;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Path;
import resources.StockProductoResource;

@Path("/hortofruticolas/stock")
@ApplicationScoped
public class StockHortoFruticolaResource extends StockProductoResource<StockHortoFruticola, HistoricoHortofruticola> {
    @Override
    protected String getHistoricoEntityName() {
        return "HistoricoHortofruticola";
    }
}
