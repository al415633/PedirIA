package services;

import data.StockCarne;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.json.simple.JSONObject;
import pythonAdapter.JSONConverter;
import pythonAdapter.PythonManager;

import java.util.List;

@ApplicationScoped
public class StockCarneDAO {

    @Inject
    EntityManager em;

    @Transactional
    public StockCarne agregarStock(StockCarne stockCarne) {
        em.persist(stockCarne);
        return stockCarne;
    }

    public StockCarne retrieve(Long id) {
        return em.find(StockCarne.class, id);
    }

    public List<StockCarne> retrieveByCarne(Long idCarne) {
        return em.createQuery("SELECT s FROM StockCarne s WHERE s.carne.id = :idCarne", StockCarne.class)
                .setParameter("idCarne", idCarne)
                .getResultList();
    }


    @Transactional
    public StockCarne actualizarStock(StockCarne stockCarne) {
        return em.merge(stockCarne);
    }

    @Transactional
    public StockCarne eliminarStock(Long id) {
        StockCarne stockCarne = em.find(StockCarne.class, id);
        if (stockCarne != null) {
            em.remove(stockCarne);
        }
        return stockCarne;
    }

    public List<StockCarne> getAll() {
        List< StockCarne > result = em.createQuery("SELECT s FROM StockCarne s", StockCarne.class)
                .getResultList();
//        JSONConverter converter = new JSONConverter();
//        converter.extractHistoricStockCarne(result);
//        converter.extractMapCurrentStockCarne(result);
//        converter.extractCurrentStockCarne(result);
//        converter.preparePythonMessage(result);
        return result;
    }

    public String getPrediction() {
        JSONConverter converter = new JSONConverter();
        JSONObject data = converter.preparePythonMessage(getAll());
        System.out.println(data.toJSONString());
        System.out.println(data);
        PythonManager pythonManager = new PythonManager();
        JSONObject prediction = pythonManager.sendPythonJSONAsFile("src/main/python/tests/test3.py", data);
        System.out.println(prediction);
        System.out.println(prediction.get("message"));
        String stringValue = (String) prediction.get("message");
        System.out.println(stringValue);
//        pythonManager.execPython()
        return stringValue;
    }
}
