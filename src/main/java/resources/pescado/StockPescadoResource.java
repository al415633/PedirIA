package resources.pescado;

import data.pescaderia.StockPescado;
import data.pescaderia.HistoricoPescado;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import services.pescado.StockPescadoDAO;
import services.pescado.HistoricoStockPescadoDAO;
import resources.StockProductoResource;

@Path("/pescados/stock")
@ApplicationScoped
public class StockPescadoResource extends StockProductoResource<StockPescado, HistoricoPescado> {

    @Inject
    private StockPescadoDAO stockPescadoDAO;

    @Inject
    private HistoricoStockPescadoDAO stockPescadoHistoricoDAO;

    @Override
    protected String getHistoricoEntityName() {
        return "HistoricoPescado";
    }
}
