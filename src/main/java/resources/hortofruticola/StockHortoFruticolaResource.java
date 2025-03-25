package resources.hortofruticola;

import data.hortofruticola.StockHortoFruticola;
import data.hortofruticola.HistoricoHortofruticola;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import services.hortofruticola.StockHortoFruticolaDAO;
import services.hortofruticola.HistoricoStockHortofruticolaDAO;
import resources.StockProductoResource;

@Path("/hortofruticolas/stock")
@ApplicationScoped
public class StockHortoFruticolaResource extends StockProductoResource<StockHortoFruticola, HistoricoHortofruticola> {

    @Inject
    private StockHortoFruticolaDAO stockHortoFruticolaDAO;

    @Inject
    private HistoricoStockHortofruticolaDAO stockHortoFruticolaHistoricoDAO;

    @Override
    protected String getHistoricoEntityName() {
        return "HistoricoHortofruticola";
    }
}
