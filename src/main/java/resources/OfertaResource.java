package resources;

import data.ComercioDetails;
import data.Oferta;
import data.Usuario;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import services.ComercioDao;
import services.OfertaDao;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;

@Path("/oferta")
public class OfertaResource {

    @Inject
    OfertaDao daoOferta;

    @Inject
    ComercioDao daoComercio;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOfertas() {
        return Response.ok(daoOferta.getOfertas()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/retrieve/{id_oferta}")
    public Response getOferta(@PathParam("id_oferta") final Long id_oferta) {

        Oferta oferta = daoOferta.getOfertaPorId(id_oferta);
        if (oferta == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(oferta).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response createOferta(
            @QueryParam("lugar") String lugar,
            @QueryParam("idNegocio") Long idNegocio,
            @QueryParam("idAprovechante") Long idAprovechante,
            @QueryParam("fechaAlta") String fechaAlta,
            @QueryParam("fechaBaja") String fechaBaja,
            @QueryParam("fechaVencimiento") String fechaVencimiento,
            @QueryParam("cantidad") Integer cantidad,
            @QueryParam("idProducto") Long idProducto,
            @QueryParam("tipoProducto") String tipoProducto
    ) throws URISyntaxException {
        // Verificar si el negocio existe
        ComercioDetails negocio = daoComercio.getComercioPorId(idNegocio);
        if (negocio == null) {
            return Response.status(Response.Status.NOT_FOUND).build(); // Negocio no encontrado
        }

        // Crear Oferta
        Oferta oferta = new Oferta();
        oferta.setLugar(lugar);
        oferta.setId_negocio(idNegocio);
        oferta.setId_aprovechante(idAprovechante);
        oferta.setFecha_alta(LocalDate.parse(fechaAlta)); // Convertir la fecha de String a LocalDate
        oferta.setFecha_baja(fechaBaja != null ? LocalDate.parse(fechaBaja) : null); // Manejar fecha baja opcional
        oferta.setFecha_vencimiento(LocalDate.parse(fechaVencimiento)); // Convertir la fecha de String a LocalDate
        oferta.setCantidad(cantidad);
        oferta.setId_producto(idProducto);
        oferta.setTipo_producto(tipoProducto);

        // Guardar la oferta en la base de datos
        daoOferta.crearOferta(oferta);

        // Generar URI para el nuevo recurso
        URI uri = new URI("/oferta/retrieve/" + oferta.getId_oferta());
        return Response.created(uri).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response updateOferta(Oferta oferta) {
        // Buscar la oferta existente por su ID
        Oferta ofertaExistente = daoOferta.getOfertaPorId(oferta.getId_oferta());

        if (ofertaExistente == null) {
            return Response.status(Response.Status.NOT_FOUND).build(); // Si la oferta no existe, devolvemos 404
        }

        // Verificar si el negocio asociado a la oferta existe
        ComercioDetails negocio = daoComercio.getComercioPorId(oferta.getId_negocio());
        if (negocio == null) {
            return Response.status(Response.Status.NOT_FOUND).build(); // Si el negocio no existe, devolvemos 404
        }

        // Actualizar los campos de la oferta existente
        ofertaExistente.setLugar(oferta.getLugar());
        ofertaExistente.setId_aprovechante(oferta.getId_aprovechante());
        ofertaExistente.setId_negocio(oferta.getId_negocio());
        ofertaExistente.setFecha_alta(oferta.getFecha_alta());
        ofertaExistente.setFecha_baja(oferta.getFecha_baja());
        ofertaExistente.setFecha_vencimiento(oferta.getFecha_vencimiento());
        ofertaExistente.setCantidad(oferta.getCantidad());
        ofertaExistente.setId_producto(oferta.getId_producto());
        ofertaExistente.setTipo_producto(oferta.getTipo_producto());

        daoOferta.actualizarOferta(ofertaExistente);

        return Response.noContent().build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/{idOferta}")
    public Response deleteOferta(@PathParam("idOferta") final Long idOferta) {
        Oferta ofertaExistente = daoOferta.getOfertaPorId(idOferta);

        if (ofertaExistente == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        daoOferta.eliminarOferta(idOferta);

        return Response.noContent().build();
    }



}
