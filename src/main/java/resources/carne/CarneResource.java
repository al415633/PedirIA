package resources.carne;

import data.carniceria.Carne;
import jakarta.ws.rs.Path;
import resources.ProductoResource;

@Path("/carnes")
public class CarneResource extends ProductoResource<Carne> {
}
