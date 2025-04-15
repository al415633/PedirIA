package services;

import data.HistoricoProducto;
import data.Producto;
import data.StockProducto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public abstract class HistoricoProductoDAO<T extends HistoricoProducto> {

    public abstract EntityManager getEntityManager();

    // Obtener todas las ventas históricas de un producto específico
    public List<T> obtenerHistorialPorProducto(Long idProducto, String entityName) {
        return getEntityManager().createQuery(
                "SELECT h FROM " + entityName + " h WHERE h.producto.id = :idProducto ORDER BY h.fechaVenta DESC", 
                (Class<T>) getEntityClass())
                .setParameter("idProducto", idProducto)
                .getResultList();
    }

    public List<T> obtenerHistorialPorUsuario(Long idNegocio, String entityName) {
        return getEntityManager().createQuery(
                        "SELECT h FROM " + entityName + " h WHERE h.producto.idNegocio = :idNegocio ORDER BY h.fechaVenta DESC",
                        (Class<T>) getEntityClass())
                .setParameter("idNegocio", idNegocio)
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

    @Transactional
    public void addHistorico(StockProducto stockEntry, BigDecimal cantidadVendida) {
        EntityManager em = getEntityManager();
        LocalDate hoy = LocalDate.now();
        String entityName = getEntityClass().getSimpleName();

        // Buscar si ya existe un registro para el producto y la fecha actual
        TypedQuery<T> query = em.createQuery(
                "SELECT h FROM " + entityName + " h WHERE h.producto.id = :idProducto AND h.fechaVenta = :fechaVenta",
                getEntityClass());
        query.setParameter("idProducto", stockEntry.getProducto().getId());
        query.setParameter("fechaVenta", hoy);
        List<T> resultados = query.getResultList();

        if (!resultados.isEmpty()) {
            // Actualizar la cantidad vendida
            T historicoExistente = resultados.get(0);
            historicoExistente.setCantidad(historicoExistente.getCantidad().add(cantidadVendida));
            em.merge(historicoExistente);
        } else {
            try {
                // Crear una nueva instancia del historial
                T nuevoHistorico = getEntityClass().getDeclaredConstructor().newInstance();
                nuevoHistorico.setProducto(stockEntry.getProducto());
                nuevoHistorico.setCantidad(cantidadVendida);
                nuevoHistorico.setFechaVenta(hoy);
                nuevoHistorico.setFechaIngreso(stockEntry.getFechaIngreso());
                nuevoHistorico.setFechaVencimiento(stockEntry.getFechaVencimiento());
                em.persist(nuevoHistorico);
            } catch (Exception e) {
                throw new RuntimeException("Error creando el registro histórico", e);
            }
        }
    }



    // Método abstracto para obtener la clase de la entidad específica
    protected abstract Class<T> getEntityClass();
}
