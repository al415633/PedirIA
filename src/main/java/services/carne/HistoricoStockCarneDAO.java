package services.carne;

import data.carniceria.HistoricoCarne;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class HistoricoStockCarneDAO {

    @Inject
    EntityManager em;

    /**
     * Obtener todas las ventas históricas de un producto específico.
     */
    public List<HistoricoCarne> obtenerHistorialPorProducto(Long idCarne) {
        return em.createQuery(
                "SELECT h FROM HistoricoCarne h WHERE h.carne.id = :idCarne ORDER BY h.fechaVenta DESC",
                HistoricoCarne.class)
                .setParameter("idCarne", idCarne)
                .getResultList();
    }

    /**
     * Obtener ventas históricas de un producto en un rango de fechas.
     */
    public List<HistoricoCarne> obtenerHistorialPorProductoYRangoFechas(Long idCarne, LocalDate fechaInicio, LocalDate fechaFin) {
        TypedQuery<HistoricoCarne> query = em.createQuery(
                "SELECT h FROM HistoricoCarne h WHERE h.carne.id = :idCarne " +
                "AND h.fechaVenta BETWEEN :fechaInicio AND :fechaFin ORDER BY h.fechaVenta DESC",
                HistoricoCarne.class);
        query.setParameter("idCarne", idCarne);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        return query.getResultList();
    }

    /**
     * Obtener todas las ventas históricas sin filtro.
     */
    public List<HistoricoCarne> obtenerTodosLosHistoriales() {
        return em.createQuery(
                "SELECT h FROM HistoricoCarne h ORDER BY h.fechaVenta DESC",
                HistoricoCarne.class)
                .getResultList();
    }
}
