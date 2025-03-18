package services.pescado;

import data.pescaderia.Pescado;
import jakarta.enterprise.context.ApplicationScoped;
import services.ProductoDAO;

@ApplicationScoped
public class PescadoDAO extends ProductoDAO<Pescado> {
    public PescadoDAO() {
        super(Pescado.class, "Pescado", "ImagenesPescados", "id_pescado", "PescadoMapping");
    }
}
