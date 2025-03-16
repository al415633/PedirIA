package services;

import data.HistoricoCarne;
import data.HistoricoHortofruticola;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class HistoricoStockHortofruticolaDAO {

    @Inject
    EntityManager em;

    /**
     * Obtener todas las ventas históricas de un producto específico.
     */
    public List<HistoricoHortofruticola> obtenerHistorialPorProducto(Long idHortofruticola) {
        return em.createQuery(
                        "SELECT h FROM HistoricoHortofruticola h WHERE h.hortofruticola.id = :idHortofruticola ORDER BY h.fechaVenta DESC",
                        HistoricoHortofruticola.class)
                .setParameter("idHortofruticola", idHortofruticola)
                .getResultList();
    }

    /**
     * Obtener ventas históricas de un producto en un rango de fechas.
     */
    public List<HistoricoHortofruticola> obtenerHistorialPorProductoYRangoFechas(Long idHortofruticola, LocalDate fechaInicio, LocalDate fechaFin) {
        TypedQuery<HistoricoHortofruticola> query = em.createQuery(
                "SELECT h FROM HistoricoHortofruticola h WHERE h.hortofruticola.id = :idHortofruticola " +
                        "AND h.fechaVenta BETWEEN :fechaInicio AND :fechaFin ORDER BY h.fechaVenta DESC",
                HistoricoHortofruticola.class);
        query.setParameter("idHortofruticola", idHortofruticola);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        return query.getResultList();
    }

    /**
     * Obtener todas las ventas históricas sin filtro.
     */
    public List<HistoricoHortofruticola> obtenerTodosLosHistoriales() {
        return em.createQuery(
                        "SELECT h FROM HistoricoHortofruticola h ORDER BY h.fechaVenta DESC",
                        HistoricoHortofruticola.class)
                .getResultList();
    }
}
