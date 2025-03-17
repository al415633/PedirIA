package services;

import data.HistoricoCarne;
import data.HistoricoPescado;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class HistoricoStockPescadoDAO {

    @Inject
    EntityManager em;

    /**
     * Obtener todas las ventas históricas de un producto específico.
     */
    public List<HistoricoPescado> obtenerHistorialPorProducto(Long idPescado) {
        return em.createQuery(
                "SELECT h FROM HistoricoPescado h WHERE h.pescado.id = :idPescado ORDER BY h.fechaVenta DESC",
                HistoricoPescado.class)
                .setParameter("idPescado", idPescado)
                .getResultList();
    }

    /**
     * Obtener ventas históricas de un producto en un rango de fechas.
     */
    public List<HistoricoPescado> obtenerHistorialPorProductoYRangoFechas(Long idPescado, LocalDate fechaInicio, LocalDate fechaFin) {
        TypedQuery<HistoricoPescado> query = em.createQuery(
                "SELECT h FROM HistoricoPescado h WHERE h.pescado.id = :idPescado " +
                "AND h.fechaVenta BETWEEN :fechaInicio AND :fechaFin ORDER BY h.fechaVenta DESC",
                HistoricoPescado.class);
        query.setParameter("idPescado", idPescado);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        return query.getResultList();
    }

    /**
     * Obtener todas las ventas históricas sin filtro.
     */
    public List<HistoricoPescado> obtenerTodosLosHistoriales() {
        return em.createQuery(
                "SELECT h FROM HistoricoPescado h ORDER BY h.fechaVenta DESC",
                HistoricoPescado.class)
                .getResultList();
    }
}
