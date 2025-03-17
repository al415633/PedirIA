package services;

import data.pescaderia.Pescado;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PescadoDAO extends ProductoDAO<Pescado> {
    public PescadoDAO() {
        super(Pescado.class, "Pescado", "ImagenesPescados", "id_pescado", "PescadoMapping");
    }
}
