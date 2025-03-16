package services;

import data.HistoricoCarne;
import data.HistoricoHortofruticola;
import data.StockCarne;
import data.StockHortoFruticola;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.json.simple.JSONObject;
import pythonAdapter.JSONConverter;
import pythonAdapter.PythonManager;

import java.math.BigDecimal;
import java.time.LocalDate;
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
        List<StockHortoFruticola> result = em.createQuery("SELECT s FROM StockHortoFruticola s", StockHortoFruticola.class)
                .getResultList();
        JSONConverter converter = new JSONConverter();
        converter.preparePythonMessage(result); //TODO: JSONCONVERTER HECHO SOLO PARA CARNE (?)
        return result;
    }

    @Transactional
    public void venderStock(Long idStock, BigDecimal cantidadVendida) {
        StockHortoFruticola stock = em.find(StockHortoFruticola.class, idStock);
        if (stock != null && stock.getCantidad().compareTo(cantidadVendida) >= 0) {

            LocalDate today = LocalDate.now();

            //  Buscar si ya existe una venta del mismo stock y fecha
            TypedQuery<HistoricoHortofruticola> query = em.createQuery(
                    "SELECT h FROM HistoricoHortofruticola h WHERE h.hortofruticola.id = :idHortofruticola " +
                            "AND h.fechaVenta = :fechaVenta AND h.fechaIngreso = :fechaIngreso AND h.fechaVencimiento = :fechaVencimiento",
                    HistoricoHortofruticola.class
            );
            query.setParameter("idHortofruticola", stock.getHortoFruticola().getId());
            query.setParameter("fechaVenta", today);
            query.setParameter("fechaIngreso", stock.getFechaIngreso());
            query.setParameter("fechaVencimiento", stock.getFechaVencimiento());

            List<HistoricoHortofruticola> ventasHoy = query.getResultList();

            if (!ventasHoy.isEmpty()) {
                //  Ya existe una venta del mismo stock hoy, actualizar la cantidad
                HistoricoHortofruticola historico = ventasHoy.getFirst();
                historico.setCantidad(historico.getCantidad().add(cantidadVendida));
                em.merge(historico);
            } else {
                //  No existe una venta hoy, crear una nueva entrada en el historial
                HistoricoHortofruticola historico = new HistoricoHortofruticola();
                historico.setCantidad(cantidadVendida);
                historico.setFechaIngreso(stock.getFechaIngreso());
                historico.setFechaVencimiento(stock.getFechaVencimiento());
                historico.setFechaVenta(today);
                historico.setHortofruticola(stock.getHortoFruticola());
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


    public String getPrediction() {
        JSONConverter converter = new JSONConverter();
        JSONObject data = converter.preparePythonMessage(getAll()); //TODO: JSONCONVERTER HECHO SOLO PARA CARNE (?)
        PythonManager pythonManager = new PythonManager();
        return "result of prediction";
    }
}