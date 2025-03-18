package resources;

import data.AprovechanteDetails;
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
        return Response.ok(usuario).build();
    }


    @GET
    @Path("/obtener")
    public Response obtenerPerfil(@CookieParam("usuario") String usuario) {
        System.out.println(usuario);
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("No hay sesión activa").build();
        }
        return Response.ok("Perfil del usuario en sesión: " + usuario).build();
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
        usuario.setTipo(tipoAprovechante);

        // Crear AprovechanteDetails
        AprovechanteDetails aprovechante = new AprovechanteDetails();
        aprovechante.setCondiciones(condiciones);
        aprovechante.setCondiciones2(condiciones2);


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
    public Response updateAprovechante(Usuario usuario, AprovechanteDetails aprovechante) {
        boolean actualizado = dao.actualizarAprovechante(usuario, aprovechante);
        if (!actualizado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/{correo}")
    public Response deleteAprovechante(@PathParam("correo") final String correo) {
        boolean eliminado = dao.eliminarAprovechante(correo);
        if (!eliminado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
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


}
