package services;

import java.math.BigDecimal;
import java.util.List;

import org.json.simple.JSONObject;

import data.HistoricoProducto;
import data.StockProducto;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import pythonAdapter.PythonManager;
import pythonAdapter.jsonPacker.AbstractJSONPacker;

public abstract class StockProductoDAO<T extends StockProducto> {

    public abstract EntityManager getEntityManager();
    public AbstractJSONPacker packer;

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

    // Obtener stock por idProducto
    public List<T> retrieveByProducto(Long idProducto) {
        return getEntityManager().createQuery("SELECT s FROM " + getEntityClass().getSimpleName() + " s WHERE s.producto.id = :idProducto", getEntityClass())
                .setParameter("idProducto", idProducto)
                .getResultList();
    }

    // Obtener stock por Negocio
    @Transactional
    public List<T> getAllByNegocio(Long idNegocio) {
        return getEntityManager().createQuery("SELECT s FROM " + getEntityClass().getSimpleName() + " s WHERE s.producto.idNegocio = :idNegocio", getEntityClass())
                .setParameter("idNegocio", idNegocio)
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
        // Buscar el stock con el ID proporcionado
        T stock = getEntityManager().find(getEntityClass(), idStock);

        if (stock == null) {
            throw new IllegalArgumentException("El stock con ID " + idStock + " no existe.");
        }

        // Verificar que la cantidad vendida sea menor o igual que la cantidad disponible
        if (stock.getCantidad().compareTo(cantidadVendida) < 0) {
            throw new IllegalArgumentException("Cantidad vendida mayor que la cantidad disponible en el stock.");
        }

        // Reducir la cantidad del stock
        stock.setCantidad(stock.getCantidad().subtract(cantidadVendida));

        // Si la cantidad llega a cero o menos, podemos eliminar el stock (opcional)
        if (stock.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            getEntityManager().remove(stock);
        } else {
            // Si no se eliminó, actualizar el stock
            getEntityManager().merge(stock);
        }
    }

    @Transactional
    public void eliminarStock(Long idStock, BigDecimal cantidadVendida) {
        // Buscar el stock con el ID proporcionado
        T stock = getEntityManager().find(getEntityClass(), idStock);

        if (stock == null) {
            throw new IllegalArgumentException("El stock con ID " + idStock + " no existe.");
        }

        // Verificar que la cantidad vendida sea menor o igual que la cantidad disponible
        if (stock.getCantidad().compareTo(cantidadVendida) < 0) {
            throw new IllegalArgumentException("Cantidad vendida mayor que la cantidad disponible en el stock.");
        }

        // Reducir la cantidad del stock
        stock.setCantidad(stock.getCantidad().subtract(cantidadVendida));

        // Si la cantidad llega a cero o menos, podemos eliminar el stock (opcional)
        if (stock.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            getEntityManager().remove(stock);
        } else {
            // Si no se eliminó, actualizar el stock
            getEntityManager().merge(stock);
        }
    }


    // Métod0 abstracto para obtener la clase de las entidades específica
    protected abstract Class<T> getEntityClass();
    protected abstract Class<T> getHistoricoEntityClass();

    public JSONObject getPrediction(Long usuario, HistoricoProductoDAO historicoProductoDAO) {
        PythonManager pythonManager = new PythonManager();
        String JSONtoFiles;

        List<T> stockList = getAllByNegocio(usuario);
        List<HistoricoProducto> historicoList = historicoProductoDAO.obtenerHistorialPorUsuario(usuario, historicoProductoDAO.getEntityClass().getSimpleName());

        if (stockList == null || stockList.isEmpty()) {
            return null;
        }
        if (historicoList == null || historicoList.isEmpty()) {
            return null;
        }

        try {
            JSONtoFiles = packer.packageData((List<StockProducto>) stockList, historicoList);
            System.out.println("JSON enviado a Python: " + JSONtoFiles);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        JSONObject prediction = pythonManager.sendPythonInfo("src/main/python/predictor.py", JSONtoFiles);
        System.out.println(prediction);
        packer.closeFiles();
        
        return prediction;
    }
}
