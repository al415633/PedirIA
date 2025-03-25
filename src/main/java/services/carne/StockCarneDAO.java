package services.carne;

import data.carniceria.StockCarne;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import services.StockProductoDAO;


@ApplicationScoped
public class StockCarneDAO extends StockProductoDAO<StockCarne> {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<StockCarne> getEntityClass() {
        return StockCarne.class;
    }
}
