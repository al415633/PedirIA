package services;

import data.HistoricoProducto;
import data.StockProducto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.json.simple.JSONObject;
import pythonAdapter.PythonManager;
import pythonAdapter.jsonPacker.AbstractJSONPacker;
import pythonAdapter.jsonPacker.JSONCarnePacker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public abstract class StockProductoDAO<T extends StockProducto> {

    public abstract EntityManager getEntityManager();
    public AbstractJSONPacker<T> packer;

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

    public String getPrediction() {
        PythonManager pythonManager = new PythonManager();
        String JSONtoFiles;

        List<T> stockList = getAll();
        System.out.println("Stock obtenido: " + stockList);

        if (stockList == null || stockList.isEmpty()) {
            return "{\"error\": \"No hay datos en el stock\"}";
        }

        try {
            JSONtoFiles = packer.packageData(stockList);
            System.out.println("JSON enviado a Python: " + JSONtoFiles);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Error en la serialización de datos\"}";
        }

        JSONObject prediction = pythonManager.sendPythonInfo("src/main/python/predictor.py", JSONtoFiles);
        packer.closeFiles();
        if (prediction == null) {
            return "{\"error\": \"Python no devolvió respuesta\"}";
        }

        String stringValue = prediction.get("message").toString();

        return stringValue;
    }
}
