package resources;

import data.AprovechanteDetails;
import data.ComercioDetails;
import jakarta.annotation.security.RolesAllowed;
import data.Usuario;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import services.AprovechanteDao;
import java.net.URI;
import java.net.URISyntaxException;


@Path("/aprovechante")
public class AprovechanteResource {
    @Inject
    AprovechanteDao dao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAprovechantes() {
        return Response.ok(dao.getAprovechantes()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/retrieve/{correo}")
    public Response getAprovechante(@PathParam("correo") final String correo) {

        Usuario usuario = dao.getAprovechantePorCorreo(correo);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        System.out.println(usuario);
        System.out.println(usuario.getAprovechante());
        return Response.ok(usuario).build();
    }


    @GET
    @Path("/obtener")
    public Response obtenerPerfil(@CookieParam("usuario") String usuario) {
        System.out.println(usuario);
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("No hay sesión activa").build();
        }
        return Response.ok(usuario).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response createAprovechante(
            @QueryParam("correo") String correo,
            @QueryParam("password") String password,
            @QueryParam("tipoAprovechante") String tipoAprovechante,
            @QueryParam("condiciones") String condiciones,
            @QueryParam("condiciones2") String condiciones2
    ) throws URISyntaxException {
        // Verificar si ya existe el correo
        if (dao.existeCorreo(correo)) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        // Crear Usuario
        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);

        String encr_pass = BcryptUtil.bcryptHash(password);
        usuario.setPassword(encr_pass);
        usuario.setTipo("aprovechante");

        // Crear AprovechanteDetails
        AprovechanteDetails aprovechante = new AprovechanteDetails();
        aprovechante.setCondiciones(condiciones);
        aprovechante.setCondiciones2(condiciones2);
        aprovechante.setTipo_aprovechante(tipoAprovechante);


        // Establecer relación entre Usuario y AprovechanteDetails
        aprovechante.setUsuario(usuario);
        usuario.setAprovechante(aprovechante);

        // Guardar en la base de datos
        dao.crearAprovechante(usuario, aprovechante);

        // Generar URI para el nuevo recurso
        URI uri = new URI("/aprovechante/retrieve/" + correo);
        return Response.created(uri).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response updateAprovechante(@QueryParam("correo") String correo,
                                       @QueryParam("password") String password,
                                       @QueryParam("condiciones") String condiciones,
                                       @QueryParam("condiciones2") String condiciones2) {
        try {
            // Verificar que el correo y los datos del comercio no sean nulos
            if (correo == null) {
                System.out.println("correo = " + correo);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Datos incompletos para la actualización")
                        .build();
            }

            // Buscar el usuario por su correo
            Usuario usuarioExistente = dao.getAprovechantePorCorreo(correo);
            if (usuarioExistente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Usuario no encontrado")
                        .build();
            }

            // Actualizar los datos del usuario
            //TODO: NO SE SI DEBERIA DE ENCRIPTAR AQUI LA CONTRASEÑA
            String encr_pass = BcryptUtil.bcryptHash(password);
            usuarioExistente.setPassword(encr_pass); // Actualizar la contraseña

            // Actualizar los datos del comercio (negocio)
            AprovechanteDetails aprovechanteExistente = usuarioExistente.getAprovechante();
            if (aprovechanteExistente != null) {
                aprovechanteExistente.setCondiciones(condiciones);
                aprovechanteExistente.setCondiciones2(condiciones2);
            } else {
                // Si el negocio no existe, podemos crear uno nuevo y asociarlo
                aprovechanteExistente = new AprovechanteDetails();
                aprovechanteExistente.setUsuario(usuarioExistente);
                usuarioExistente.setAprovechante(aprovechanteExistente);
            }

            // Guardar los cambios en la base de datos
            boolean actualizado = dao.actualizarAprovechante(usuarioExistente, aprovechanteExistente);
            if (!actualizado) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Error al actualizar el comercio")
                        .build();
            }

            return Response.noContent().build(); // Retorna un código 204 cuando la actualización fue exitosa
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Ocurrió un error inesperado")
                    .build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/{correo}")
    public Response deleteAprovechante(@PathParam("correo") final String correo) {
        Usuario usuario = dao.getAprovechantePorCorreo(correo);

        // Verificar si el usuario existe
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Usuario no encontrado")
                    .build();
        }

        // Eliminar el usuario y su negocio asociado
        boolean eliminado = dao.eliminarAprovechante(correo);

        if (!eliminado) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al eliminar el comercio")
                    .build();
        }

        NewCookie cookie = new NewCookie("usuario", "", "/", null, "Usuario en sesión", 0, false);

        return Response.noContent()
                .cookie(cookie) // Deshabilitar (eliminar) la cookie
                .build();
    }




    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login")
    public Response login(@QueryParam("correo") String correo, @QueryParam("password") String password) {
        Usuario usuario = dao.verificarCredenciales(correo, password);

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Credenciales incorrectas").build();
        }

        System.out.println("usuario: " + password);
        System.out.println("encriptada: " + usuario.getPassword());

        // Verificar la contraseña encriptada con Bcrypt
        if (!BcryptUtil.matches(password, usuario.getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Contraseña incorrecta").build();
        }


        NewCookie cookie = new NewCookie("usuario", correo, "/", null, "Usuario en sesión", 3600, false);
        System.out.println(cookie);
        return Response.ok("Sesión iniciada para " + correo).cookie(cookie).build();


    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/logout")
    public Response logout(@CookieParam("usuario") String correo) {
        // Verificar si el usuario está autenticado (si la cookie existe)
        if (correo == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("No hay sesión activa")
                    .build();
        }

        // Eliminar la cookie del usuario configurando su valor vacío y tiempo de expiración a 0
        NewCookie cookie = new NewCookie("usuario", "", "/", null, "Usuario en sesión", 0, false);

        return Response.ok("Sesión cerrada con éxito")
                .cookie(cookie) // Deshabilitar (vaciar) la cookie
                .build();
    }




}
