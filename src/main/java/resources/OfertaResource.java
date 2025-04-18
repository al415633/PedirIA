package resources;

import data.ComercioDetails;
import data.Oferta;
import data.OfertaRequest;
import data.StockProducto;
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
    public Response getAllOfertasPublicadas() {
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
    @Path("/mis-ofertas-publicadas/{tipo}/{idProducto}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOfertasByTipoYProductoPublicadas(
            @CookieParam("usuario") String correo,
            @PathParam("tipo") String tipo,
            @PathParam("idProducto") Long idProducto) {

        Long idNegocio = daoComercio.getComercioPorCorreo(correo).getId_usuario();

        List<Oferta> ofertas;
        switch (tipo.toLowerCase()) {
            case "carne" -> ofertas = daoOferta.obtenerOfertasPorProductoCarnePublicadas(idNegocio, idProducto);
            case "pescado" -> ofertas = daoOferta.obtenerOfertasPorProductoPescadoPublicadas(idNegocio, idProducto);
            case "hortofruticola" -> ofertas = daoOferta.obtenerOfertasPorProductoHortofruticolaPublicadas(idNegocio, idProducto);
            default -> {
                return Response.status(Response.Status.BAD_REQUEST).entity("Tipo no válido").build();
            }
        }

        return Response.ok(ofertas).build();
    }

    @GET
    @Path("/mis-ofertas-aceptadas/{tipo}/{idProducto}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOfertasByTipoYProductoAceptadas(
            @CookieParam("usuario") String correo,
            @PathParam("tipo") String tipo,
            @PathParam("idProducto") Long idProducto) {

        Long idNegocio = daoComercio.getComercioPorCorreo(correo).getId_usuario();

        List<Oferta> ofertas;
        switch (tipo.toLowerCase()) {
            case "carne" -> ofertas = daoOferta.obtenerOfertasPorProductoCarneAceptadas(idNegocio, idProducto);
            case "pescado" -> ofertas = daoOferta.obtenerOfertasPorProductoPescadoAceptadas(idNegocio, idProducto);
            case "hortofruticola" -> ofertas = daoOferta.obtenerOfertasPorProductoHortofruticolaAceptadas(idNegocio, idProducto);
            default -> {
                return Response.status(Response.Status.BAD_REQUEST).entity("Tipo no válido").build();
            }
        }

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
    @Path("/aceptar/{id}")
    public Response aceptarOferta(@PathParam("id") Long idOferta, @CookieParam("usuario") String correo) {

        Long idAprovechante = daoAprovechante.getAprovechantePorCorreo(correo).getId_usuario();

        try {
            Oferta ofertaAcepptada = daoOferta.aceptarOferta(idOferta, idAprovechante);
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

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editarOferta(@PathParam("id") Long id, OfertaRequest ofertaRequest) {
        // Buscar la oferta por ID
        Oferta oferta = daoOferta.obtenerOfertaPorId(id);
        if (oferta == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Oferta no encontrada").build();
        }

        // Actualizar los campos de la oferta con los datos proporcionados en ofertaRequest
        oferta.setUbicacion(ofertaRequest.getUbicacion());
        oferta.setCantidad(ofertaRequest.getCantidad());
        oferta.setFechaAlta(ofertaRequest.getFechaAlta());
        oferta.setFechaBaja(ofertaRequest.getFechaBaja());

        // Actualizar la oferta en la base de datos
        try {
            Oferta ofertaActualizada = daoOferta.modificarOferta(oferta);
            return Response.ok(ofertaActualizada).build();
        } catch (PersistenceException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error de persistencia al actualizar la oferta: " + e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al actualizar la oferta: " + e.getMessage()).build();
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
