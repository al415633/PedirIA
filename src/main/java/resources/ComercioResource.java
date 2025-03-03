package resources;

import data.Usuario;
import data.ComercioDetails;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import services.ComercioDao;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/comercio")
public class ComercioResource {
    @Inject
    ComercioDao dao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComercios() {
        return Response.ok(dao.getComercios()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/retrieve/{correo}")
    public Response getComercio(@PathParam("correo") final String correo) {
        Usuario usuario = dao.getComercioPorCorreo(correo);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(usuario).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response createComercio(Usuario usuario, ComercioDetails negocio) throws URISyntaxException {
        if (dao.existeCorreo(usuario.getCorreo())) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        dao.crearNegocio(usuario, negocio);
        URI uri = new URI("/comercio/retrieve/" + usuario.getCorreo());
        return Response.created(uri).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response updateComercio(Usuario usuario, ComercioDetails negocio) {
        boolean actualizado = dao.actualizarNegocio(usuario, negocio);
        if (!actualizado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/{correo}")
    public Response deleteComercio(@PathParam("correo") final String correo) {
        boolean eliminado = dao.eliminarNegocio(correo);
        if (!eliminado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
