package resources;

import java.util.List;

import data.StockHortoFruticola;
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
import services.StockHortoFruticolaDAO;

@Path("/hortofruticolas/stock")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StockHortoFruticolaResource {

    @Inject
    StockHortoFruticolaDAO stockHortoFruticolaDAO;

    @GET
    public List<StockHortoFruticola> obtenerTodos() {
        return stockHortoFruticolaDAO.getAll();
    }

    @GET
    @Path("/prediccion")
    public Response obtenerPrediccion() {
        try {
            String prediction = stockHortoFruticolaDAO.getPrediction();
            return Response.ok("{\"message\": " + prediction + "}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\": \"No se pudo obtener la predicción\"}")
                        .build();
        }
    }
    
    @GET
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
}