package services;

import data.StockProducto;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

public abstract class StockProductoDAO<T extends StockProducto> {

    public abstract EntityManager getEntityManager();

    // Agregar un nuevo stock
    @Transactional
    public T agregarStock(T stock) {
        getEntityManager().persist(stock);
        return stock;
    }

    // Obtener un stock por ID
    public T retrieve(Long id) {
        return getEntityManager().find(getEntityClass(), id);
    }

    // Obtener stock por idProducto ¡
    public List<T> retrieveByProducto(Long idProducto) {
        return getEntityManager().createQuery("SELECT s FROM " + getEntityClass().getSimpleName() + " s WHERE s.producto.id = :idProducto", getEntityClass())
                .setParameter("idProducto", idProducto)
                .getResultList();
    }

    // Obtener todos los registros de stock
    public List<T> getAll() {
        return getEntityManager().createQuery("SELECT s FROM " + getEntityClass().getSimpleName() + " s", getEntityClass())
                .getResultList();
    }

    // Actualizar stock existente
    @Transactional
    public T actualizarStock(T stock) {
        return getEntityManager().merge(stock);
    }

    // Eliminar stock por ID
    @Transactional
    public T eliminarStock(Long id) {
        T stock = getEntityManager().find(getEntityClass(), id);
        if (stock != null) {
            getEntityManager().remove(stock);
        }
        return stock;
    }

    // Método genérico para vender un producto del stock
    @Transactional
    public void venderStock(Long idStock, BigDecimal cantidadVendida) {
        T stock = getEntityManager().find(getEntityClass(), idStock);
        if (stock != null && stock.getCantidad().compareTo(cantidadVendida) >= 0) {
            // Actualizamos el stock
            stock.setCantidad(stock.getCantidad().subtract(cantidadVendida));

            // Si el stock llega a 0, lo eliminamos
            if (stock.getCantidad().compareTo(BigDecimal.ZERO) == 0) {
                getEntityManager().remove(stock);
            } else {
                getEntityManager().merge(stock);
            }
        }
    }

    // Método abstracto para obtener la clase de la entidad específica
    protected abstract Class<T> getEntityClass();
}
