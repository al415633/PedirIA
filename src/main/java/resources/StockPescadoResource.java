package resources;


import data.StockPescado;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import services.StockPescadoDAO;

import java.util.List;

@Path("/pescados/stock")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StockPescadoResource {

    @Inject
    StockPescadoDAO stockPescadoDAO;

    @GET
    public List<StockPescado> obtenerTodos() {
        return stockPescadoDAO.getAll();
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
        return stockPescadoDAO.retrieveByCarne(idPescado);
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
            stockPescado.setId(id); // Asegura que el ID coincida con el ID enviado
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/predict")
    public Response obtenerPrediccion() {

//        return stockPescadoDAO.getPrediction();
        return Response.ok(stockPescadoDAO.getPrediction()).build();
    }
}
