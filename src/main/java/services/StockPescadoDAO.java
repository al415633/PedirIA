package services;

import data.StockPescado;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class StockPescadoDAO {

    @Inject
    EntityManager em;

    @Transactional
    public StockPescado agregarStock(StockPescado stockPescado) {
        em.persist(stockPescado);
        return stockPescado;
    }

    public StockPescado retrieve(Long id) {
        return em.find(StockPescado.class, id);
    }

    public List<StockPescado> retrieveByCarne(Long idPescado) {
        return em.createQuery("SELECT s FROM StockPescado s WHERE s.pescado.id = :idPescado", StockPescado.class)
                .setParameter("idPescado", idPescado)
                .getResultList();
    }


    @Transactional
    public StockPescado actualizarStock(StockPescado stockPescado) {
        return em.merge(stockPescado);
    }

    @Transactional
    public StockPescado eliminarStock(Long id) {
        StockPescado stockPescado = em.find(StockPescado.class, id);
        if (stockPescado != null) {
            em.remove(stockPescado);
        }
        return stockPescado;
    }

    public List<StockPescado> getAll() {
        List< StockPescado > result = em.createQuery("SELECT s FROM StockPescado s", StockPescado.class)
                .getResultList();
        return result;
    }

    public String getPrediction() {
        return "result of prediction";
    }
}
