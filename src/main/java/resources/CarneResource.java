package resources;

import data.Carne;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import services.CarneDAOJPA;

import java.net.URI;
import java.net.URISyntaxException;

@Path("/carnes")
public class CarneResource {
    @Inject
    CarneDAOJPA dao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        return Response.ok(dao.getAll()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/retrieve/{id}")
    public Response getCarne(@PathParam("id") final Long id) {
        Carne carne = dao.retrieve(id);
        if (carne == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(carne).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response createCarne(Carne carne) throws URISyntaxException {
        Carne response = dao.create(carne);
        if (response == null) return Response.status(Response.Status.CONFLICT).build();
        URI uri = new URI("/carnes/" + carne.getId());
        return Response.created(uri).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response updateCarne(final Carne carne) {
        Carne result = dao.update(carne);
        if (result == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.noContent().build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/{id}")
    public Response deleteCarne(@PathParam("id") final Long id) {
        Carne result = dao.delete(id);
        if (result == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.noContent().build();
    }
}
