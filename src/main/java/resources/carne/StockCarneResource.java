package resources.carne;

import data.carniceria.StockCarne;
import data.carniceria.HistoricoCarne;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import services.carne.StockCarneDAO;
import services.carne.HistoricoStockCarneDAO;
import resources.StockProductoResource;

@Path("/carnes/stock")
@ApplicationScoped
public class StockCarneResource extends StockProductoResource<StockCarne, HistoricoCarne> {

    @Inject
    private StockCarneDAO stockCarneDAO;

    @Inject
    private HistoricoStockCarneDAO stockCarneHistoricoDAO;

    @Override
    protected String getHistoricoEntityName() {
        return "HistoricoCarne";
    }
}
