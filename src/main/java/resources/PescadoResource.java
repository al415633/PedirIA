package resources;

import data.Carne;
import data.Pescado;
import jakarta.persistence.PersistenceException;
import services.ComercioDao;
import services.PescadoDAO;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collections;

@Path("/pescados")
public class PescadoResource {

    @Inject
    PescadoDAO daoPescado;

    @Inject
    ComercioDao daoComercio;
    @Inject
    PescadoDAO pescadoDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        return Response.ok(daoPescado.getAll()).build();
    }

    @GET
    @Path("/{id}")
    public Response getPescado(@PathParam("id") Long id) {
        Pescado pescado = daoPescado.retrieve(id);
        if (pescado == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Pescado no encontrado").build();
        }
        return Response.ok(pescado).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPescado(Pescado pescado) {
        try {
            Pescado pescadoCreado = daoPescado.create(pescado);
            if (pescadoCreado == null) {
                return Response.status(Response.Status.CONFLICT).build();
            }
            return Response.status(Response.Status.CREATED).entity(pescadoCreado).build();
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
    public Response updatePescado(Pescado pescado) {
        int maxRetries = 3; // Número máximo de reintentos
        int attempt = 0; // Contador de intentos
        long delay = 1000; // Retraso en milisegundos entre intentos

        while (attempt < maxRetries) {
            try {
                Pescado result = daoPescado.update(pescado);
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

        // Si se sale del bucle sin éxito, devolver un error
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity("No se pudo actualizar el pescado después de varios intentos. Inténtelo de nuevo más tarde.")
                .build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            Pescado result = daoPescado.delete(id);
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
        boolean existe = pescadoDAO.existePescado(nombre, unidad);
        return Response.ok(Collections.singletonMap("existe", existe)).build();
    }

    @GET
    @Path("/mis-pescados")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerCarnesDeUsuario(@CookieParam("usuario") String correo) {
        if (correo == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("No hay sesión activa").build();
        }
        Long idNegocio = daoComercio.getComercioPorCorreo(correo).getId_usuario();
        return Response.ok(daoPescado.getAllByUsuario(idNegocio)).build();
    }
}
