package services.hortofruticola;


import data.hortofruticola.HortoFruticola;
import jakarta.enterprise.context.ApplicationScoped;
import services.ProductoDAO;

@ApplicationScoped
public class HortoFruticolaDAO extends ProductoDAO<HortoFruticola> {
    public HortoFruticolaDAO() {
        super(HortoFruticola.class, "Hortofruticola", "imageneshortofruticolas", "id_hortofruticola", "HortoFruticolaMapping");
    }
}
