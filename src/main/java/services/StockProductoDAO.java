package services;

import java.math.BigDecimal;
import java.util.List;

import org.json.simple.JSONObject;

import data.StockProducto;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import pythonAdapter.PythonManager;
import pythonAdapter.jsonPacker.AbstractJSONPacker;

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
        // TODO
    }
    // Método abstracto para obtener la clase de la entidades específica
    protected abstract Class<T> getEntityClass();
    protected abstract Class<T> getHistoricoEntityClass();

    public String getPrediction() {
        PythonManager pythonManager = new PythonManager();
        String JSONtoFiles;

        List<T> stockList = getAll();

        if (stockList == null || stockList.isEmpty()) {
            return "{\"error\": \"No hay datos en el stock\"}";
        }

        try {
            JSONtoFiles = packer.packageData(stockList);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Error en la serialización de datos\"}";
        }

        JSONObject prediction = pythonManager.sendPythonInfo("src/main/python/predictor.py", JSONtoFiles);
        //packer.closeFiles();
        if (prediction == null) {
            return "{\"error\": \"Python no devolvió respuesta\"}";
        }

        String stringValue = prediction.get("message").toString();

        return stringValue;
    }
}
