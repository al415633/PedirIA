package services.pescado;

import data.pescaderia.StockPescado;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import services.StockProductoDAO;

@ApplicationScoped
public class StockPescadoDAO extends StockProductoDAO<StockPescado> {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<StockPescado> getEntityClass() {
        return StockPescado.class;
    }
}
