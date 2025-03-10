package resources;


import data.StockCarne;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import services.HistoricoStockCarneDAO;
import services.StockCarneDAO;

import java.math.BigDecimal;
import java.util.List;

@Path("/carnes/stock")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StockCarneResource {

    @Inject
    StockCarneDAO stockCarneDAO;

    @Inject
    HistoricoStockCarneDAO stockCarneHistoricoDAO;

    @GET
    public List<StockCarne> obtenerTodos() {
        return stockCarneDAO.getAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/retrieve/{id}")
    public Response obtenerPorId(@PathParam("id") Long id) {

        StockCarne stockCarne = stockCarneDAO.retrieve(id);
        if (stockCarne != null) {
            return Response.ok(stockCarne).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/producto/{idCarne}")
    public List<StockCarne> obtenerPorIdCarne(@PathParam("idCarne") Long idCarne) {
        return stockCarneDAO.retrieveByCarne(idCarne);
    }

    @POST
    public Response agregarStock(StockCarne stockCarne) {
        System.out.println("Agregando stock: " + stockCarne);
        StockCarne nuevoStock = stockCarneDAO.agregarStock(stockCarne);
        return Response.status(Response.Status.CREATED).entity(nuevoStock).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarStock(@PathParam("id") Long id, StockCarne stockCarne) {
        StockCarne stockExistente = stockCarneDAO.retrieve(id);
        if (stockExistente != null) {
            stockCarne.setId(id); // Asegura que el ID coincida con el ID enviado
            StockCarne stockActualizado = stockCarneDAO.actualizarStock(stockCarne);
            return Response.ok(stockActualizado).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response eliminarStock(@PathParam("id") Long id) {
        StockCarne stockCarne = stockCarneDAO.eliminarStock(id);
        if (stockCarne != null) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/vender/{idStock}/{cantidad}")
    public Response venderStock(@PathParam("idStock") Long idStock, @PathParam("cantidad") BigDecimal cantidadVendida) {
        stockCarneDAO.venderStock(idStock, cantidadVendida);
        System.out.println("Hola");
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/historico/{idCarne}")
    public Response getAllHistoricoStockCarne(@PathParam("idCarne") Long idCarne) {
        return Response.ok(stockCarneHistoricoDAO.obtenerHistorialPorProducto(idCarne)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/predict")
    public Response obtenerPrediccion() {

//        return stockPescadoDAO.getPrediction();
        return Response.ok(stockCarneDAO.getPrediction()).build();
    }

}
