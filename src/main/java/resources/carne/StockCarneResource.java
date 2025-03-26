package resources.carne;

import data.carniceria.StockCarne;
import data.carniceria.HistoricoCarne;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Path;
import resources.StockProductoResource;

@Path("/carnes/stock")
@ApplicationScoped
public class StockCarneResource extends StockProductoResource<StockCarne, HistoricoCarne> {

    @Override
    protected String getHistoricoEntityName() {
        return "HistoricoCarne";
    }
}
