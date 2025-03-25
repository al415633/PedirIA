package services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

public abstract class HistoricoProductoDAO<T> {

    public abstract EntityManager getEntityManager();

    // Obtener todas las ventas históricas de un producto específico
    public List<T> obtenerHistorialPorProducto(Long idProducto, String entityName) {
        return getEntityManager().createQuery(
                "SELECT h FROM " + entityName + " h WHERE h.producto.id = :idProducto ORDER BY h.fechaVenta DESC", 
                (Class<T>) getEntityClass())
                .setParameter("idProducto", idProducto)
                .getResultList();
    }

    // Obtener ventas históricas de un producto en un rango de fechas
    public List<T> obtenerHistorialPorProductoYRangoFechas(Long idProducto, LocalDate fechaInicio, LocalDate fechaFin, String entityName) {
        TypedQuery<T> query = getEntityManager().createQuery(
                "SELECT h FROM " + entityName + " h WHERE h.producto.id = :idProducto " +
                "AND h.fechaVenta BETWEEN :fechaInicio AND :fechaFin ORDER BY h.fechaVenta DESC", 
                (Class<T>) getEntityClass());
        query.setParameter("idProducto", idProducto);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        return query.getResultList();
    }

    // Obtener todas las ventas históricas sin filtro
    public List<T> obtenerTodosLosHistoriales(String entityName) {
        return getEntityManager().createQuery(
                "SELECT h FROM " + entityName + " h ORDER BY h.fechaVenta DESC", 
                (Class<T>) getEntityClass())
                .getResultList();
    }

    // Método abstracto para obtener la clase de la entidad específica
    protected abstract Class<T> getEntityClass();
}
