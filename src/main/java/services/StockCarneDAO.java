package services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.json.simple.JSONObject;

import data.HistoricoCarne;
import data.StockCarne;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import pythonAdapter.JSONPacker.IJSONPacker;
import pythonAdapter.JSONPacker.JSONCarnePacker;
import pythonAdapter.PythonManager;

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
        return result;
    }

    public String getPrediction() {
        PythonManager pythonManager = new PythonManager();
        String JSONtoFiles;
        IJSONPacker<StockCarne> packer = new JSONCarnePacker();
    
        List<StockCarne> stockList = getAll();
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
    
        JSONObject prediction = pythonManager.sendPythonInfo("src/main/python/predecir_carnes.py", JSONtoFiles);
        
        if (prediction == null) {
            return "{\"error\": \"Python no devolvió respuesta\"}";
        }
    
        String stringValue = prediction.get("message").toString();
        return stringValue;
    }

    @Transactional
    public void venderStock(Long idStock, BigDecimal cantidadVendida) {
        StockCarne stock = em.find(StockCarne.class, idStock);
        if (stock != null && stock.getCantidad().compareTo(cantidadVendida) >= 0) {

            LocalDate today = LocalDate.now();

            //  Buscar si ya existe una venta del mismo stock y fecha
            TypedQuery<HistoricoCarne> query = em.createQuery(
                    "SELECT h FROM HistoricoCarne h WHERE h.carne.id = :idCarne " +
                            "AND h.fechaVenta = :fechaVenta AND h.fechaIngreso = :fechaIngreso AND h.fechaVencimiento = :fechaVencimiento",
                    HistoricoCarne.class
            );
            query.setParameter("idCarne", stock.getCarne().getId());
            query.setParameter("fechaVenta", today);
            query.setParameter("fechaIngreso", stock.getFechaIngreso());
            query.setParameter("fechaVencimiento", stock.getFechaVencimiento());

            List<HistoricoCarne> ventasHoy = query.getResultList();

            if (!ventasHoy.isEmpty()) {
                //  Ya existe una venta del mismo stock hoy, actualizar la cantidad
                HistoricoCarne historico = ventasHoy.getFirst();
                historico.setCantidad(historico.getCantidad().add(cantidadVendida));
                em.merge(historico);
            } else {
                //  No existe una venta hoy, crear una nueva entrada en el historial
                HistoricoCarne historico = new HistoricoCarne();
                historico.setCantidad(cantidadVendida);
                historico.setFechaIngreso(stock.getFechaIngreso());
                historico.setFechaVencimiento(stock.getFechaVencimiento());
                historico.setFechaVenta(today);
                historico.setCarne(stock.getCarne());
                em.persist(historico);
            }

            //  Actualizar el stock restante
            stock.setCantidad(stock.getCantidad().subtract(cantidadVendida));

            //  Si el stock llega a 0, eliminarlo
            if (stock.getCantidad().compareTo(BigDecimal.ZERO) == 0) {
                em.remove(stock);
            } else {
                em.merge(stock);
            }
        }
    }
}
