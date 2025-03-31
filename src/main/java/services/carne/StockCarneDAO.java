package services.carne;

import data.StockProducto;
import data.carniceria.StockCarne;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import pythonAdapter.jsonPacker.JSONCarnePacker;
import services.StockProductoDAO;


@ApplicationScoped
public class StockCarneDAO extends StockProductoDAO<StockCarne> {
    public StockCarneDAO(){
        packer = new JSONCarnePacker();
    }

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<StockCarne> getEntityClass() {
        return StockCarne.class;
    }

    @Override
    protected Class<StockCarne> getHistoricoEntityClass() {
        return null;
    }
}
