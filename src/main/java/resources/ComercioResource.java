package resources;

import jakarta.annotation.security.RolesAllowed;
import data.Usuario;
import data.ComercioDetails;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import services.ComercioDao;
import java.net.URI;
import java.net.URISyntaxException;


@Path("/comercio")
public class ComercioResource {
    @Inject
    ComercioDao dao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComercios() {
        return Response.ok(dao.getComercios()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/retrieve/{correo}")
    public Response getComercio(@PathParam("correo") final String correo) {

        Usuario usuario = dao.getComercioPorCorreo(correo);
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
    public Response createComercio(
            @QueryParam("correo") String correo,
            @QueryParam("password") String password,
            @QueryParam("tipoComercio") String tipoComercio,
            @QueryParam("nombre") String nombre,
            @QueryParam("diaCompraDeStock") String diaCompraDeStock
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
        usuario.setTipo(tipoComercio);

        // Crear ComercioDetails
        ComercioDetails negocio = new ComercioDetails();
        negocio.setNombre(nombre);
        negocio.setDiaCompraDeStock(diaCompraDeStock);


        // Establecer relación entre Usuario y ComercioDetails
        negocio.setUsuario(usuario);
        usuario.setNegocio(negocio);

        // Guardar en la base de datos
        dao.crearNegocio(usuario, negocio);

        // Generar URI para el nuevo recurso
        URI uri = new URI("/comercio/retrieve/" + correo);
        return Response.created(uri).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response updateComercio(Usuario usuario, ComercioDetails negocio) {
        boolean actualizado = dao.actualizarNegocio(usuario, negocio);
        if (!actualizado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/{correo}")
    public Response deleteComercio(@PathParam("correo") final String correo) {
        boolean eliminado = dao.eliminarNegocio(correo);
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
