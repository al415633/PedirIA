package services.hortofruticola;

import data.hortofruticola.StockHortoFruticola;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import services.StockProductoDAO;

@ApplicationScoped
public class StockHortoFruticolaDAO extends StockProductoDAO<StockHortoFruticola> {

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
}
