package services.pescado;

import data.pescaderia.HistoricoPescado;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import services.HistoricoBaseDAO;

@ApplicationScoped
public class HistoricoStockPescadoDAO extends HistoricoBaseDAO<HistoricoPescado> {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<HistoricoPescado> getEntityClass() {
        return HistoricoPescado.class;
    }
}
