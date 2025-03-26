package resources;


import java.math.BigDecimal;
import java.util.List;

import data.StockPescado;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import services.HistoricoStockPescadoDAO;
import services.StockPescadoDAO;

@Path("/pescados/stock")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StockPescadoResource {

    @Inject
    StockPescadoDAO stockPescadoDAO;

    @Inject
    HistoricoStockPescadoDAO historicoStockPescadoDAO;

    @GET
    public List<StockPescado> obtenerTodos() {
        return stockPescadoDAO.getAll();
    }

    @GET
    @Path("/prediccion")
    public Response obtenerPrediccion() {
        try {
            String prediction = stockPescadoDAO.getPrediction();
            return Response.ok("{\"message\": " + prediction + "}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\": \"No se pudo obtener la predicción\"}")
                        .build();
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/retrieve/{id}")
    public Response obtenerPorId(@PathParam("id") Long id) {
        StockPescado stockPescado = stockPescadoDAO.retrieve(id);
        if (stockPescado == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(stockPescado).build();
    }

    @GET
    @Path("/producto/{idPescado}")
    public List<StockPescado> obtenerPorIdCarne(@PathParam("idPescado") Long idPescado) {
        return stockPescadoDAO.retrieveByPescado(idPescado);
    }

    @POST
    public Response agregarStock(StockPescado stockPescado) {
        StockPescado nuevoStock = stockPescadoDAO.agregarStock(stockPescado);
        return Response.status(Response.Status.CREATED).entity(nuevoStock).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarStock(@PathParam("id") Long id, StockPescado stockPescado) {
        StockPescado stockExistente = stockPescadoDAO.retrieve(id);
        if (stockExistente != null) {
            stockPescado.setId(id);
            StockPescado stockActualizado = stockPescadoDAO.actualizarStock(stockPescado);
            return Response.ok(stockActualizado).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response eliminarStock(@PathParam("id") Long id) {
        StockPescado stockPescado = stockPescadoDAO.eliminarStock(id);
        if (stockPescado != null) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/vender/{idStock}/{cantidad}")
    public Response venderStock(@PathParam("idStock") Long idStock, @PathParam("cantidad") BigDecimal cantidadVendida) {
        stockPescadoDAO.venderStock(idStock, cantidadVendida);
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/historico/{idPescado}")
    public Response getAllHistoricoStockCarne(@PathParam("idPescado") Long idPescado) {
        return Response.ok(historicoStockPescadoDAO.obtenerHistorialPorProducto(idPescado)).build();
    }

}
