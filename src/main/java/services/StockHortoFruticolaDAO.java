package services;

import data.StockHortoFruticola;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class StockHortoFruticolaDAO {

    @Inject
    EntityManager em;

    @Transactional
    public StockHortoFruticola agregarStock(StockHortoFruticola stockHortoFruticola) {
        em.persist(stockHortoFruticola);
        return stockHortoFruticola;
    }

    public StockHortoFruticola retrieve(Long id) {
        return em.find(StockHortoFruticola.class, id);
    }

    public List<StockHortoFruticola> retrieveByHortoFruticola(Long idHortoFruticola) {
        return em.createQuery("SELECT s FROM StockHortoFruticola s WHERE s.hortoFruticola.id = :idHortoFruticola", StockHortoFruticola.class)
                .setParameter("idHortoFruticola", idHortoFruticola)
                .getResultList();
    }

    @Transactional
    public StockHortoFruticola actualizarStock(StockHortoFruticola stockHortoFruticola) {
        return em.merge(stockHortoFruticola);
    }

    @Transactional
    public StockHortoFruticola eliminarStock(Long id) {
        StockHortoFruticola stockHortoFruticola = em.find(StockHortoFruticola.class, id);
        if (stockHortoFruticola != null) {
            em.remove(stockHortoFruticola);
        }
        return stockHortoFruticola;
    }

    public List<StockHortoFruticola> getAll() {
        return em.createQuery("SELECT s FROM StockHortoFruticola s", StockHortoFruticola.class)
                .getResultList();
    }
}