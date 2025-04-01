package services.hortofruticola;

import data.hortofruticola.StockHortoFruticola;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import pythonAdapter.jsonPacker.JSONCarnePacker;
import pythonAdapter.jsonPacker.JSONHortoFruticulaPacker;
import services.StockProductoDAO;

@ApplicationScoped
public class StockHortoFruticolaDAO extends StockProductoDAO<StockHortoFruticola> {
    public StockHortoFruticolaDAO(){
        packer = new JSONHortoFruticulaPacker();
    }
    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<StockHortoFruticola> getEntityClass() {
        return StockHortoFruticola.class;
    }

    @Override
    protected Class<StockHortoFruticola> getHistoricoEntityClass() {
        return null;
    }
}
