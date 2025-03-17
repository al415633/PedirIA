package resources;

import data.carniceria.Carne;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import services.CarneDAO;
import services.ComercioDao;

import java.util.Collections;

@Path("/carnes")
public class CarneResource {

    @Inject
    CarneDAO daoCarne;

    @Inject
    ComercioDao daoComercio;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        return Response.ok(daoCarne.getAll()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getCarne(@PathParam("id") Long id) {
        Carne carne = daoCarne.retrieve(id);
        if (carne == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(carne).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCarne(Carne carne) {
        try {
            // El objeto 'carne' incluirá los datos de imagen (por ejemplo, imagenNombre, imagenTipo y imagenDatos)
            Carne carneCreada = daoCarne.create(carne);
            if (carneCreada == null) {
                return Response.status(Response.Status.CONFLICT).build();
            }
            return Response.status(Response.Status.CREATED).entity(carneCreada).build();
        } catch (PersistenceException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error de persistencia: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Ocurrió un error inesperado: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCarne(Carne carne) {
        int maxRetries = 3;
        int attempt = 0;
        long delay = 1000;

        while (attempt < maxRetries) {
            try {
                // Si en el objeto 'carne' se han incluido nuevos datos de imagen, el DAO actualizará ambas tablas.
                Carne result = daoCarne.update(carne);
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
                            .entity("Error en el sistema, inténtelo de nuevo.")
                            .build();
                }
            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Ocurrió un error inesperado: " + e.getMessage())
                        .build();
            }
        }
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity("No se pudo actualizar la carne después de varios intentos. Inténtelo de nuevo más tarde.")
                .build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response deleteCarne(@PathParam("id") Long id) {
        try {
            Carne result = daoCarne.delete(id);
            if (result == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Ocurrió un error al eliminar: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/validar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarCarne(@QueryParam("nombre") String nombre, @QueryParam("unidad") String unidad) {
        boolean existe = daoCarne.existeProducto(nombre, unidad);
        return Response.ok(Collections.singletonMap("existe", existe)).build();
    }

    @GET
    @Path("/mis-carnes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerCarnesDeUsuario(@CookieParam("usuario") String correo) {
        if (correo == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("No hay sesión activa").build();
        }
        Long idNegocio = daoComercio.getComercioPorCorreo(correo).getId_usuario();
        return Response.ok(daoCarne.getAllByUsuario(idNegocio)).build();
    }

}
