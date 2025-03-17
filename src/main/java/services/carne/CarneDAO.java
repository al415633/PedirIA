package services;

import data.carniceria.Carne;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CarneDAO extends ProductoDAO<Carne> {
    public CarneDAO() {
        super(Carne.class, "Carne", "ImagenesCarnes", "id_carne", "CarneMapping");
    }
}
