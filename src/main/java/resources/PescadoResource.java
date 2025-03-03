package resources;

import data.Carne;
import data.Pescado;
import jakarta.persistence.PersistenceException;
import services.PescadoDAO;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collection;

@Path("/pescados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PescadoResource {

    @Inject
    PescadoDAO pescadoDAO;

    @POST
    @Path("/create")
    public Response create(Pescado pescado) {
        Pescado nuevoPescado = pescadoDAO.create(pescado);
        if (nuevoPescado == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: El pescado ya existe.").build();
        }
        return Response.status(Response.Status.CREATED).entity(nuevoPescado).build();
    }

    @GET
    public Collection<Pescado> getAll() {
        return pescadoDAO.getAll();
    }

    @GET
    @Path("/retrieve/{id}")
    public Response retrieve(@PathParam("id") Long id) {
        Pescado pescado = pescadoDAO.retrieve(id);
        if (pescado == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Pescado no encontrado").build();
        }
        return Response.ok(pescado).build();
    }

    @PUT
    @Path("/update")
    public Response update(Pescado pescado) {
        int maxRetries = 3; // Número máximo de reintentos
        int attempt = 0; // Contador de intentos
        long delay = 1000; // Retraso en milisegundos entre intentos

        while (attempt < maxRetries) {
            try {
                Pescado result = pescadoDAO.update(pescado);
                if (result == null) {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
                // Se realiza correctamente, enviamos una respuesta vacía
                return Response.noContent().build();
            } catch (PersistenceException e) {
                attempt++;
                // Esperar antes de intentar de nuevo
                try {
                    Thread.sleep(delay); // Esperar el tiempo especificado
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); // Restaurar el estado de interrupción
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Ocurrió un error inesperado. Por favor, inténtelo de nuevo.")
                            .build();
                }
            } catch (Exception e) {
                // Manejo de otros errores
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Ocurrió un error inesperado. Por favor, inténtelo de nuevo.")
                        .build();
            }
        }

        // Si se sale del bucle sin éxito, devolver un error
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity("No se pudo actualizar la carne después de varios intentos. Inténtelo de nuevo más tarde.")
                .build();
    }

    @DELETE
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") Long id) {
        Pescado deletedPescado = pescadoDAO.delete(id);
        if (deletedPescado == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Error: Pescado no encontrado").build();
        }
        return Response.ok(deletedPescado).build();
    }
}
