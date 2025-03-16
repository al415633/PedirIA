package resources;

import data.StockHortoFruticola;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import services.HistoricoStockCarneDAO;
import services.HistoricoStockHortofruticolaDAO;
import services.StockHortoFruticolaDAO;

import java.math.BigDecimal;
import java.util.List;

@Path("/hortofruticolas/stock")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StockHortoFruticolaResource {

    @Inject
    StockHortoFruticolaDAO stockHortoFruticolaDAO;

    @Inject
    HistoricoStockHortofruticolaDAO stockHortofruticolaHistoricoDAO;

    @GET
    public List<StockHortoFruticola> obtenerTodos() {
        return stockHortoFruticolaDAO.getAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/retrieve/{id}")
    public Response obtenerPorId(@PathParam("id") Long id) {
        StockHortoFruticola stockHortoFruticola = stockHortoFruticolaDAO.retrieve(id);
        if (stockHortoFruticola != null) {
            return Response.ok(stockHortoFruticola).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/producto/{idHortoFruticola}")
    public List<StockHortoFruticola> obtenerPorIdHortoFruticola(@PathParam("idHortoFruticola") Long idHortoFruticola) {
        return stockHortoFruticolaDAO.retrieveByHortoFruticola(idHortoFruticola);
    }

    @POST
    public Response agregarStock(StockHortoFruticola stockHortoFruticola) {
        System.out.println("Agregando stock: " + stockHortoFruticola);
        StockHortoFruticola nuevoStock = stockHortoFruticolaDAO.agregarStock(stockHortoFruticola);
        return Response.status(Response.Status.CREATED).entity(nuevoStock).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarStock(@PathParam("id") Long id, StockHortoFruticola stockHortoFruticola) {
        StockHortoFruticola stockExistente = stockHortoFruticolaDAO.retrieve(id);
        if (stockExistente != null) {
            stockHortoFruticola.setId(id); // Asegura que el ID coincida con el ID enviado
            StockHortoFruticola stockActualizado = stockHortoFruticolaDAO.actualizarStock(stockHortoFruticola);
            return Response.ok(stockActualizado).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response eliminarStock(@PathParam("id") Long id) {
        StockHortoFruticola stockHortoFruticola = stockHortoFruticolaDAO.eliminarStock(id);
        if (stockHortoFruticola != null) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/vender/{idStock}/{cantidad}")
    public Response venderStock(@PathParam("idStock") Long idStock, @PathParam("cantidad") BigDecimal cantidadVendida) {
        stockHortoFruticolaDAO.venderStock(idStock, cantidadVendida);
        System.out.println("Hola");
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/historico/{idHortofruticola}")
    public Response getAllHistoricoStockCarne(@PathParam("idHortofruticola") Long idHortofruticola) {
        return Response.ok(stockHortofruticolaHistoricoDAO.obtenerHistorialPorProducto(idHortofruticola)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/predict")
    public Response obtenerPrediccion() {

//        return stockPescadoDAO.getPrediction();
        return Response.ok(stockHortoFruticolaDAO.getPrediction()).build();
    }
}