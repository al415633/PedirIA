package resources.pescado;

import data.pescaderia.Pescado;
import resources.ProductoResource;
import jakarta.ws.rs.*;

@Path("/pescados")
public class PescadoResource extends ProductoResource<Pescado> {
}
