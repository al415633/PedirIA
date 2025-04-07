package resources;

import data.ComercioDetails;
import data.Oferta;
import data.OfertaRequest;
import data.StockProducto;
import data.carniceria.StockCarne;
import data.hortofruticola.StockHortoFruticola;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import services.AprovechanteDao;
import services.ComercioDao;
import services.OfertaDAO;
import services.carne.StockCarneDAO;
import services.hortofruticola.StockHortoFruticolaDAO;
import services.pescado.StockPescadoDAO;

import java.util.List;

@Path("/oferta")
public class OfertaResource {

    @Inject
    OfertaDAO daoOferta;

    @Inject
    ComercioDao daoComercio;

    @Inject
    AprovechanteDao daoAprovechante;

    @Inject
    StockCarneDAO daoStockCarne;

    @Inject
    StockPescadoDAO daoStockPescado;

    @Inject
    StockHortoFruticolaDAO daoStockHortoFrutico;


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
    public Response createOffer(OfertaRequest ofertaRequest, @CookieParam("usuario") String correo) {

        ComercioDetails comercio = daoComercio.getComercioPorCorreo(correo).getNegocio();

        Oferta oferta = new Oferta();
        oferta.setUbicacion(ofertaRequest.getUbicacion());
        oferta.setFechaAlta(ofertaRequest.getFechaAlta());
        oferta.setFechaBaja(ofertaRequest.getFechaBaja());
        oferta.setCantidad(ofertaRequest.getCantidad());

        oferta.setNegocio(comercio);

        StockProducto<?> stock = buscarStockPorIdYTipo(ofertaRequest.getIdStock(), ofertaRequest.getTipoStock());
        if (stock == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Stock no encontrado o tipo incorrecto").build();
        }

        try {
            Oferta ofertaCreada = daoOferta.crearOferta(oferta, stock);
            return Response.status(Response.Status.CREATED).entity(ofertaCreada).build();
        } catch (PersistenceException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error de persistencia: " + e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
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

    private StockProducto<?> buscarStockPorIdYTipo(Long stockId, String tipoStock) {
        switch (tipoStock) {
            case "Carne":
                return daoStockCarne.retrieve(stockId);
            case "Pescado":
                return daoStockPescado.retrieve(stockId);
            case "Hortofruticola":
                return daoStockHortoFrutico.retrieve(stockId);
            default:
                return null;  // Tipo no válido
        }
    }
}
