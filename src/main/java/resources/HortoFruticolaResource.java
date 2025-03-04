package resources;

import data.HortoFruticola;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import services.HortoFruticolaDAOJPA;

import java.net.URISyntaxException;

@Path("/hortofruticolas")
public class HortoFruticolaResource {
    @Inject
    HortoFruticolaDAOJPA dao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        return Response.ok(dao.getAll()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/retrieve/{id}")
    public Response getHortoFruticola(@PathParam("id") final Long id) {
        HortoFruticola hortoFruticola = dao.retrieve(id);
        if (hortoFruticola == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(hortoFruticola).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response createHortoFruticola(HortoFruticola hortoFruticola) throws URISyntaxException {
        HortoFruticola hortoFruticolaCreado = dao.create(hortoFruticola);
        if (hortoFruticolaCreado == null) return Response.status(Response.Status.CONFLICT).build();
        return Response.status(Response.Status.CREATED).entity(hortoFruticolaCreado).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response updateHortoFruticola(final HortoFruticola hortoFruticola) {
        int maxRetries = 3; // Número máximo de reintentos
        int attempt = 0; // Contador de intentos
        long delay = 1000; // Retraso en milisegundos entre intentos

        while (attempt < maxRetries) {
            try {
                HortoFruticola result = dao.update(hortoFruticola);
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
                .entity("No se pudo actualizar el horto-frutícola después de varios intentos. Inténtelo de nuevo más tarde.")
                .build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/{id}")
    public Response deleteHortoFruticola(@PathParam("id") final Long id) {
        HortoFruticola result = dao.delete(id);
        if (result == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.noContent().build();
    }
}