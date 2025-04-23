package resources;

import data.HistoricoProducto;
import data.StockProducto;
import data.Usuario;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import services.ComercioDao;
import services.HistoricoProductoDAO;
import services.StockProductoDAO;

import java.math.BigDecimal;
import java.util.List;

public abstract class StockProductoResource<T extends StockProducto, H extends HistoricoProducto> {

    @Inject
    protected StockProductoDAO<T> stockDAO;

    @Inject
    protected HistoricoProductoDAO<H> historicoDAO;

    @Inject
    protected ComercioDao daoComercio;

    public void setStockDAO(StockProductoDAO<T> stockDAO) {
        this.stockDAO = stockDAO;
    }

    public void setHistoricoDAO(HistoricoProductoDAO<H> historicoDAO) {
        this.historicoDAO = historicoDAO;
    }

    public void setDaoComercio(ComercioDao daoComercio) {
        this.daoComercio = daoComercio;
    }

    // Obtener todos los stocks
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<T> obtenerTodos() {
        return stockDAO.getAll();
    }

    // Obtener stock por ID
    @GET
    @Path("/retrieve/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerPorId(@PathParam("id") Long id) {
        T stock = stockDAO.retrieve(id);
        if (stock != null) {
            return Response.ok(stock).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // Obtener stock por ID de producto
    @GET
    @Path("/producto/{idProducto}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<T> obtenerPorIdProducto(@PathParam("idProducto") Long idProducto) {
        return stockDAO.retrieveByProducto(idProducto);
    }

    // Agregar nuevo stock
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response agregarStock(T stock) {
        System.out.println("Agregando stock: " + stock);
        T nuevoStock = stockDAO.agregarStock(stock);
        return Response.status(Response.Status.CREATED).entity(nuevoStock).build();
    }

    // Actualizar stock
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response actualizarStock(@PathParam("id") Long id, T stock) {
        T stockExistente = stockDAO.retrieve(id);
        if (stockExistente != null) {
            stockDAO.actualizarStock(stock);
            return Response.ok(stock).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // Eliminar stock
    @DELETE
    @Path("/{id}")
    public Response eliminarStock(@PathParam("id") Long id) {
        T stock = stockDAO.eliminarStock(id);
        if (stock != null) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // Vender stock
    @POST
    @Path("/vender/{idStock}/{cantidad}")
    public Response venderStock(@PathParam("idStock") Long idStock, @PathParam("cantidad") BigDecimal cantidadVendida) {
        try {
            stockDAO.venderStock(idStock, cantidadVendida);
            StockProducto stockProducto = stockDAO.retrieve(idStock);
            historicoDAO.addHistorico(stockProducto, cantidadVendida);
            return Response.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Obtener historial de ventas por producto
    @GET
    @Path("/historico/{idProducto}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerHistorico(@PathParam("idProducto") Long idProducto) {
        List<H> historial = historicoDAO.obtenerHistorialPorProducto(idProducto, getHistoricoEntityName());
        return Response.ok(historial).build();
    }
    @GET
    @Path("/prediccion")
    public Response obtenerPrediccion(@CookieParam("usuario") String usuario) {
        try {
            Usuario datosUsuario = daoComercio.getComercioPorCorreo(usuario);
            Long id = datosUsuario.getNegocio().getIdNegocio();
            String prediction = stockDAO.getPrediction(id, historicoDAO);
            return Response.ok("{\"message\": " + prediction + "}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"No se pudo obtener la predicción\"}")
                    .build();
        }
    }

    // Método abstracto para obtener el nombre de la entidad de historial (subclase específica)
    protected abstract String getHistoricoEntityName();
}
