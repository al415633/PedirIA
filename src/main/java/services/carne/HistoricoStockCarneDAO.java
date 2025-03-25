package services.carne;

import data.carniceria.HistoricoCarne;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import services.HistoricoBaseDAO;

@ApplicationScoped
public class HistoricoStockCarneDAO extends HistoricoBaseDAO<HistoricoCarne> {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<HistoricoCarne> getEntityClass() {
        return HistoricoCarne.class;
    }
}
