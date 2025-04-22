package resources;

import data.Producto;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import services.ComercioDao;
import services.ProductoDAO;

import java.util.Collections;

public abstract class ProductoResource<T extends Producto> {

    @Inject
    ProductoDAO<T> daoProducto;

    public void setDaoProducto(ProductoDAO<T> daoProducto) {
        this.daoProducto = daoProducto;
    }

    @Inject
    ComercioDao daoComercio;

    public void setDaoComercio(ComercioDao daoComercio) {
        this.daoComercio = daoComercio;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        return Response.ok(daoProducto.getAll()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProducto(@PathParam("id") Long id) {
        T producto = daoProducto.retrieve(id);
        if (producto == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Producto no encontrado").build();
        }
        return Response.ok(producto).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProducto(T producto, @CookieParam("usuario") String correo) {
        Integer idNegocio = Math.toIntExact(daoComercio.getComercioPorCorreo(correo).getId_usuario());
        producto.setIdNegocio(idNegocio);
        try {
            T productoCreado = daoProducto.create(producto);
            if (productoCreado == null) {
                return Response.status(Response.Status.CONFLICT).build();
            }
            return Response.status(Response.Status.CREATED).entity(productoCreado).build();
        } catch (PersistenceException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error de persistencia: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Ocurrió un error inesperado: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProducto(T producto) {
        int maxRetries = 3;
        int attempt = 0;
        long delay = 1000;

        while (attempt < maxRetries) {
            try {
                T result = daoProducto.update(producto);
                if (result == null) {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
                return Response.noContent().build();
            } catch (PersistenceException e) {
                attempt++;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Ocurrió un error inesperado. Por favor, inténtelo de nuevo.")
                            .build();
                }
            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Ocurrió un error inesperado. Por favor, inténtelo de nuevo.")
                        .build();
            }
        }

        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity("No se pudo actualizar el producto después de varios intentos. Inténtelo de nuevo más tarde.")
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProducto(@PathParam("id") Long id) {
        try {
            T result = daoProducto.delete(id);
            if (result == null) {
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

    @GET
    @Path("/validar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarProducto(@QueryParam("nombre") String nombre, @QueryParam("unidad") String unidad, @CookieParam("usuario") String correo) {
        Integer idNegocio = Math.toIntExact(daoComercio.getComercioPorCorreo(correo).getId_usuario());
        boolean existe = daoProducto.existeProducto(nombre, unidad, idNegocio);
        return Response.ok(Collections.singletonMap("existe", existe)).build();
    }

    @GET
    @Path("/mis-productos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerProductosDeUsuario(@CookieParam("usuario") String correo) {
        if (correo == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("No hay sesión activa").build();
        }
        Long idNegocio = daoComercio.getComercioPorCorreo(correo).getId_usuario();
        return Response.ok(daoProducto.getAllByUsuario(idNegocio)).build();
    }
}
