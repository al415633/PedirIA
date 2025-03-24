package resources.hortofruticola;

import data.hortofruticola.HortoFruticola;
import jakarta.ws.rs.*;
import resources.ProductoResource;

@Path("/hortofruticolas")
public class HortoFruticolaResource extends ProductoResource<HortoFruticola> {
}