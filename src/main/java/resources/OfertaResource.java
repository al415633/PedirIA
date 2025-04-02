package resources;

import data.Oferta;
import data.StockProducto;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import services.AprovechanteDao;
import services.ComercioDao;
import services.OfertaDAO;

import java.util.List;

@Path("/oferta")
public class OfertaResource {

    @Inject
    OfertaDAO daoOferta;

    @Inject
    ComercioDao daoComercio;

    @Inject
    AprovechanteDao daoAprovechante;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOfertas() {
        List<Oferta> ofertas = daoOferta.obtenerOfertas();
        return Response.ok(ofertas).build();
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOferta(@PathParam("id") Long id) {
        Oferta oferta = daoOferta.obtenerOfertaPorId(id);
        if (oferta == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Oferta no encontrada").build();
        }
        return Response.ok(oferta).build();
    }


    @GET
    @Path("/mis-ofertas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOfertasByNegocio(@CookieParam("usuario") String correo) {

        Long idNegocio = daoComercio.getComercioPorCorreo(correo).getId_usuario();

        if (idNegocio == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Se requiere el id del negocio").build();
        }
        List<Oferta> ofertas = daoOferta.obtenerOfertasPorNegocio(idNegocio);
        return Response.ok(ofertas).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public <T extends StockProducto<?>> Response createOffer(Oferta oferta, T stock)  {
        try {
            Oferta ofertaCreada = daoOferta.crearOferta(oferta, stock);
            return Response.status(Response.Status.CREATED).entity(ofertaCreada).build();
        } catch (PersistenceException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error de persistencia: " + e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al crear la oferta: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOferta(@PathParam("id") Long id) {
        try {
            boolean result = daoOferta.eliminarOferta(id);
            if (!result) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Ocurrió un error al eliminar: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response aceptarOferta(Oferta oferta, @CookieParam("usuario") String correo) {

        Long idAprovechante = daoAprovechante.getAprovechantePorCorreo(correo).getId_usuario();

        try {
            Oferta ofertaAcepptada = daoOferta.aceptarOferta(oferta, idAprovechante);
            return Response.status(Response.Status.CREATED).entity(ofertaAcepptada).build();
        } catch (PersistenceException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error de persistencia: " + e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al aceptar la oferta: " + e.getMessage()).build();
        }
    }
}
