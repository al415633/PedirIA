package services.carne;

import data.carniceria.Carne;
import jakarta.enterprise.context.ApplicationScoped;
import services.ProductoDAO;

@ApplicationScoped
public class CarneDAO extends ProductoDAO<Carne> {
    public CarneDAO() {
        super(Carne.class, "Carne", "ImagenesCarnes", "id_carne", "CarneMapping");
    }
}
