package resources;

import data.ComercioDetails;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import services.ComercioDao;

import java.net.URI;
import java.net.URISyntaxException;

@Path("/comercio")
public class
ComercioResource {
    @Inject
    ComercioDao dao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getData() {
        return Response.ok(dao.getComercios()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/retrieve/{correo}")
    public Response getContact(@PathParam("correo") final String correo) {
        if (dao.getComercio(correo) == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(dao.getComercio(correo)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response createContact(ComercioDetails comercioDetails) throws URISyntaxException {
        ComercioDetails response = dao.loadComercioByUsername(comercioDetails.getCorreo(), comercioDetails.getPassword());
        if (response == null) return Response.status(Response.Status.CONFLICT).build();
        URI uri = new URI("/contacts/" + comercioDetails.getNombre());
        return Response.created(uri).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response updateContact(final ComercioDetails comercioDetails) {
        /*Contact result = dao.;
        if(result == Contact.NOT_FOUND) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.noContent().build();
        todo*/
        return null;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/{correo}")
    public Response deleteContact(@PathParam("correo") final String correo) {
       /* Contact result = dao.(correo);
        if(result == Contact.NOT_FOUND) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.noContent().build();
    */
        //TODO
        return null;
    }


}