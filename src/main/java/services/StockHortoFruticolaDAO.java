package services;

import java.util.List;

import org.json.simple.JSONObject;

import data.StockHortoFruticola;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import pythonAdapter.JSONPacker.IJSONPacker;
import pythonAdapter.JSONPacker.JSONHortoFruticolaPacker;
import pythonAdapter.PythonManager;

@ApplicationScoped
public class StockHortoFruticolaDAO {

    @Inject
    EntityManager em;

    @Transactional
    public StockHortoFruticola agregarStock(StockHortoFruticola stockHortoFruticola) {
        em.persist(stockHortoFruticola);
        return stockHortoFruticola;
    }

    public StockHortoFruticola retrieve(Long id) {
        return em.find(StockHortoFruticola.class, id);
    }

    public List<StockHortoFruticola> retrieveByHortoFruticola(Long idHortoFruticola) {
        return em.createQuery("SELECT s FROM StockHortoFruticola s WHERE s.hortoFruticola.id = :idHortoFruticola", StockHortoFruticola.class)
                .setParameter("idHortoFruticola", idHortoFruticola)
                .getResultList();
    }

    @Transactional
    public StockHortoFruticola actualizarStock(StockHortoFruticola stockHortoFruticola) {
        return em.merge(stockHortoFruticola);
    }

    @Transactional
    public StockHortoFruticola eliminarStock(Long id) {
        StockHortoFruticola stockHortoFruticola = em.find(StockHortoFruticola.class, id);
        if (stockHortoFruticola != null) {
            em.remove(stockHortoFruticola);
        }
        return stockHortoFruticola;
    }

    public List<StockHortoFruticola> getAll() {
        return em.createQuery("SELECT s FROM StockHortoFruticola s", StockHortoFruticola.class)
                .getResultList();
    }

    public String getPrediction() {
        PythonManager pythonManager = new PythonManager();
        String JSONtoFiles;
        IJSONPacker<StockHortoFruticola> packer = new JSONHortoFruticolaPacker();
    
        List<StockHortoFruticola> stockList = getAll();
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
        
        if (prediction == null) {
            return "{\"error\": \"Python no devolvió respuesta\"}";
        }
    
        String stringValue = prediction.get("message").toString();
        return stringValue;
    }
}