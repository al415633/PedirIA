package resources.pescado;

import data.pescaderia.StockPescado;
import data.pescaderia.HistoricoPescado;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Path;
import resources.StockProductoResource;

@Path("/pescados/stock")
@ApplicationScoped
public class StockPescadoResource extends StockProductoResource<StockPescado, HistoricoPescado> {

    @Override
    protected String getHistoricoEntityName() {
        return "HistoricoPescado";
    }
}
