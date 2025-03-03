package services;

import data.StockCarne;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class StockCarneDAO {

    @Inject
    EntityManager em;

    @Transactional
    public StockCarne agregarStock(StockCarne stockCarne) {
        em.persist(stockCarne);
        return stockCarne;
    }

    public StockCarne retrieve(Long id) {
        return em.find(StockCarne.class, id);
    }

    public List<StockCarne> retrieveByCarne(Long idCarne) {
        return em.createQuery("SELECT s FROM StockCarne s WHERE s.carne.id = :idCarne", StockCarne.class)
                .setParameter("idCarne", idCarne)
                .getResultList();
    }


    @Transactional
    public StockCarne actualizarStock(StockCarne stockCarne) {
        return em.merge(stockCarne);
    }

    @Transactional
    public StockCarne eliminarStock(Long id) {
        StockCarne stockCarne = em.find(StockCarne.class, id);
        if (stockCarne != null) {
            em.remove(stockCarne);
        }
        return stockCarne;
    }

    public List<StockCarne> getAll() {
        return em.createQuery("SELECT s FROM StockCarne s", StockCarne.class)
                .getResultList();
    }

}
