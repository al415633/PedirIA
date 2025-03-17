package services;

import data.HistoricoCarne;
import data.HistoricoPescado;
import data.StockCarne;
import data.StockPescado;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    public List<StockPescado> retrieveByPescado(Long idPescado) {
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

    @Transactional
    public void venderStock(Long idStock, BigDecimal cantidadVendida) {
        StockPescado stock = em.find(StockPescado.class, idStock);
        if (stock != null && stock.getCantidad().compareTo(cantidadVendida) >= 0) {

            LocalDate today = LocalDate.now();

            //  Buscar si ya existe una venta del mismo stock y fecha
            TypedQuery<HistoricoPescado> query = em.createQuery(
                    "SELECT h FROM HistoricoPescado h WHERE h.pescado.id = :idPescado " +
                            "AND h.fechaVenta = :fechaVenta AND h.fechaIngreso = :fechaIngreso AND h.fechaVencimiento = :fechaVencimiento",
                    HistoricoPescado.class
            );
            query.setParameter("idPescado", stock.getPescado().getId());
            query.setParameter("fechaVenta", today);
            query.setParameter("fechaIngreso", stock.getFechaIngreso());
            query.setParameter("fechaVencimiento", stock.getFechaVencimiento());

            List<HistoricoPescado> ventasHoy = query.getResultList();

            if (!ventasHoy.isEmpty()) {
                //  Ya existe una venta del mismo stock hoy, actualizar la cantidad
                HistoricoPescado historico = ventasHoy.getFirst();
                historico.setCantidad(historico.getCantidad().add(cantidadVendida));
                em.merge(historico);
            } else {
                //  No existe una venta hoy, crear una nueva entrada en el historial
                HistoricoPescado historico = new HistoricoPescado();
                historico.setCantidad(cantidadVendida);
                historico.setFechaIngreso(stock.getFechaIngreso());
                historico.setFechaVencimiento(stock.getFechaVencimiento());
                historico.setFechaVenta(today);
                historico.setPescado(stock.getPescado());
                em.persist(historico);
            }

            //  Actualizar el stock restante
            stock.setCantidad(stock.getCantidad().subtract(cantidadVendida));

            //  Si el stock llega a 0, eliminarlo
            if (stock.getCantidad().compareTo(BigDecimal.ZERO) == 0) {
                em.remove(stock);
            } else {
                em.merge(stock);
            }
        }
    }

}
