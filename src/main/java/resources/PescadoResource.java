package resources;

import data.Pescado;
import services.PescadoDAOJPA;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collection;

@Path("/pescados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PescadoResource {

    @Inject
    PescadoDAOJPA pescadoDAO;

    @POST
    @Path("/create")
    public Response create(Pescado pescado) {
        Pescado nuevoPescado = pescadoDAO.create(pescado);
        if (nuevoPescado == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: El pescado ya existe.").build();
        }
        return Response.status(Response.Status.CREATED).entity(nuevoPescado).build();
    }

    @GET
    public Collection<Pescado> getAll() {
        return pescadoDAO.getAll();
    }

    @GET
    @Path("/{id}")
    public Response retrieve(@PathParam("id") Long id) {
        Pescado pescado = pescadoDAO.retrieve(id);
        if (pescado == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Pescado no encontrado").build();
        }
        return Response.ok(pescado).build();
    }

    @PUT
    @Path("/update")
    public Response update(Pescado pescado) {
        Pescado updatedPescado = pescadoDAO.update(pescado);
        if (updatedPescado == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Error: Pescado no encontrado").build();
        }
        return Response.ok(updatedPescado).build();
    }

    @DELETE
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") Long id) {
        Pescado deletedPescado = pescadoDAO.delete(id);
        if (deletedPescado == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Error: Pescado no encontrado").build();
        }
        return Response.ok(deletedPescado).build();
    }
}
