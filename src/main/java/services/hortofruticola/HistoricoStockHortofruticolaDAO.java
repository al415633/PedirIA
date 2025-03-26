package services.hortofruticola;

import data.hortofruticola.HistoricoHortofruticola;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import services.HistoricoProductoDAO;

@ApplicationScoped
public class HistoricoStockHortofruticolaDAO extends HistoricoProductoDAO<HistoricoHortofruticola> {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<HistoricoHortofruticola> getEntityClass() {
        return HistoricoHortofruticola.class;
    }
}
