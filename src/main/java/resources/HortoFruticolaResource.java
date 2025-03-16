package resources;

import data.HortoFruticola;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import services.ComercioDao;
import services.HortoFruticolaDAO;

import java.util.Collections;

@Path("/hortofruticolas")
public class HortoFruticolaResource {
    @Inject
    HortoFruticolaDAO daoHortofruticola;

    @Inject
    ComercioDao daoComercio;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        return Response.ok(daoHortofruticola.getAll()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getHortoFruticola(@PathParam("id") Long id) {
        HortoFruticola hortofruticola = daoHortofruticola.retrieve(id);
        if (hortofruticola == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(hortofruticola).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createHortoFruticola(HortoFruticola hortofruticola) {
        try {
            // El objeto 'carne' incluirá los datos de imagen (por ejemplo, imagenNombre, imagenTipo y imagenDatos)
            HortoFruticola hortofruticolaCreada = daoHortofruticola.create(hortofruticola);
            if (hortofruticolaCreada == null) {
                return Response.status(Response.Status.CONFLICT).build();
            }
            return Response.status(Response.Status.CREATED).entity(hortofruticolaCreada).build();
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
    public Response updateHortoFruticola(HortoFruticola hortoFruticola) {
        int maxRetries = 3; // Número máximo de reintentos
        int attempt = 0; // Contador de intentos
        long delay = 1000; // Retraso en milisegundos entre intentos

        while (attempt < maxRetries) {
            try {
                HortoFruticola result = daoHortofruticola.update(hortoFruticola);
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
    @Path("/{id}")
    public Response deleteHortoFruticola(@PathParam("id") Long id) {
        try {
            HortoFruticola result = daoHortofruticola.delete(id);
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
        boolean existe = daoHortofruticola.existeHortofruticola(nombre, unidad);
        return Response.ok(Collections.singletonMap("existe", existe)).build();
    }

    @GET
    @Path("/mis-hortofruticolas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerHortofruticolasDeUsuario(@CookieParam("usuario") String correo) {
        if (correo == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("No hay sesión activa").build();
        }
        Long idNegocio = daoComercio.getComercioPorCorreo(correo).getId_usuario();
        return Response.ok(daoHortofruticola.getAllByUsuario(idNegocio)).build();
    }
}