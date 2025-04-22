package resources;

import data.Usuario;
import data.hortofruticola.HortoFruticola;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import resources.hortofruticola.HortoFruticolaResource;
import services.ComercioDao;
import services.ProductoDAO;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HortofruticolaResourceIntegrationTest {

//    @InjectMocks
//    ProductoDAO<HortoFruticola> daoProducto;

//     @InjectMocks


//    @InjectMocks
//    EntityManager entityManager;


    //    @Mock
//    Usuario user;
    @Mock
    ComercioDao daoComercio;
    @Mock
    ProductoDAO<HortoFruticola> daoProducto;
    @InjectMocks
    HortoFruticolaResource hortofruticolaResource;
//    @InjectMocks
//        ProductoResource<HortoFruticola> hortofruticolaResource;

    private HortoFruticola hortofruticola;

    @BeforeEach
    void setup() {
        hortofruticola = new HortoFruticola();
        hortofruticola.setId(1L);
        hortofruticola.setNombre("Pechuga de Pollo");
        hortofruticola.setUnidad("kg");
        hortofruticola.setIdNegocio(10);
        Mockito.reset();
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testGetAllHortoFruticola() {
        List<HortoFruticola> lista = Arrays.asList(hortofruticola);
        when(daoProducto.getAll()).thenReturn(lista);
        hortofruticolaResource.setDaoComercio(daoComercio);
        hortofruticolaResource.setDaoProducto(daoProducto);
        Response response = hortofruticolaResource.getAll();

        assertEquals(200, response.getStatus());
        verify(daoProducto, times(1)).getAll();
    }

    @Test
    public void testGetHortoFruticolaById_Encontrado() {
        when(daoProducto.retrieve(1L)).thenReturn(hortofruticola);
        hortofruticolaResource.setDaoComercio(daoComercio);
        hortofruticolaResource.setDaoProducto(daoProducto);
        Response response = hortofruticolaResource.getProducto(1L);

        assertEquals(200, response.getStatus());
        verify(daoProducto).retrieve(1L);
    }

    @Test
    public void testGetHortoFruticolaById_NoEncontrado() {
        when(daoProducto.retrieve(2L)).thenReturn(null);
        hortofruticolaResource.setDaoComercio(daoComercio);
        hortofruticolaResource.setDaoProducto(daoProducto);
        Response response = hortofruticolaResource.getProducto(2L);

        assertEquals(404, response.getStatus());
        assertEquals("Producto no encontrado", response.getEntity());
    }

    @Test
    public void testCreateHortoFruticola_Exitoso() {
        HortoFruticola nuevo = new HortoFruticola();
        nuevo.setNombre("Costilla");
        nuevo.setUnidad("kg");
        Usuario user = mock(Usuario.class);
        when(user.getId_usuario()).thenReturn(20L);

        when(daoComercio.getComercioPorCorreo("carnicero@ejemplo.com")).thenReturn(user);
//        when(daoProducto.create(any(HortoFruticola.class))).thenReturn(nuevo);
        when(daoProducto.create(any(HortoFruticola.class))).thenReturn(nuevo);
        hortofruticolaResource.setDaoComercio(daoComercio);
        hortofruticolaResource.setDaoProducto(daoProducto);

        Response response = hortofruticolaResource.createProducto(nuevo, "carnicero@ejemplo.com");

        assertEquals(201, response.getStatus());
//        verify(daoProducto).create(any(HortoFruticola.class));
    }

    @Test
    public void testCreateHortoFruticola_Conflicto() {
        Usuario user = mock(Usuario.class);
        when(user.getId_usuario()).thenReturn(20L);
        when(daoComercio.getComercioPorCorreo("carnicero@ejemplo.com")).thenReturn(user);
        when(daoProducto.create(any(HortoFruticola.class))).thenReturn(null);
        hortofruticolaResource.setDaoComercio(daoComercio);
        hortofruticolaResource.setDaoProducto(daoProducto);
        Response response = hortofruticolaResource.createProducto(hortofruticola, "carnicero@ejemplo.com");

        assertEquals(409, response.getStatus());
    }

    @Test
    public void testUpdateHortoFruticola_Exitoso() {
        when(daoProducto.update(hortofruticola)).thenReturn(hortofruticola);
        hortofruticolaResource.setDaoComercio(daoComercio);
        hortofruticolaResource.setDaoProducto(daoProducto);
        Response response = hortofruticolaResource.updateProducto(hortofruticola);

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testUpdateHortoFruticola_NoEncontrado() {
        when(daoProducto.update(hortofruticola)).thenReturn(null);
        hortofruticolaResource.setDaoComercio(daoComercio);
        hortofruticolaResource.setDaoProducto(daoProducto);
        Response response = hortofruticolaResource.updateProducto(hortofruticola);

        assertEquals(404, response.getStatus());
    }

    @Test
    public void testDeleteHortoFruticola_Exitoso() {
        when(daoProducto.delete(1L)).thenReturn(hortofruticola);
        hortofruticolaResource.setDaoComercio(daoComercio);
        hortofruticolaResource.setDaoProducto(daoProducto);
        Response response = hortofruticolaResource.deleteProducto(1L);

        assertEquals(204, response.getStatus());
        verify(daoProducto).delete(1L);
    }

    @Test
    public void testDeleteHortoFruticola_NoEncontrado() {
        when(daoProducto.delete(99L)).thenReturn(null);
//        when(entityManager.createNativeQuery(
//                "SELECT c." + "id_hortofruticola" + ", c.nombre, c.unidad, c.tipo_conserva, c.id_img, " +
//                        "i.nombre AS imagenNombre, i.tipo AS imagenTipo, i.datos AS imagenDatos, " +
//                        "c.id_negocio " +
//                        "FROM " + "HortoFruticola" + " c " +
//                        "JOIN " + "ImagenesHortoFruticolas" + " i ON c.id_img = i.id_img " +
//                        "WHERE c." + "id_hortofruticola" + " = ?",
//                "HortoFruticolaMapping"
//        )).thenReturn();
        hortofruticolaResource.setDaoComercio(daoComercio);
        hortofruticolaResource.setDaoProducto(daoProducto);
        Response response = hortofruticolaResource.deleteProducto(99L);

        assertEquals(404, response.getStatus());
    }

    @Test
    public void testValidarHortoFruticola_Existe() {
        Usuario user = mock(Usuario.class);
        when(user.getId_usuario()).thenReturn(99L);
        when(daoComercio.getComercioPorCorreo("hortofruticola@local")).thenReturn(user);
        when(daoProducto.existeProducto("Lomo", "kg", 99)).thenReturn(true);
        hortofruticolaResource.setDaoComercio(daoComercio);
        hortofruticolaResource.setDaoProducto(daoProducto);
        Response response = hortofruticolaResource.validarProducto("Lomo", "kg", "hortofruticola@local");

        assertEquals(200, response.getStatus());
        assertEquals(Collections.singletonMap("existe", true), response.getEntity());
    }

    @Test
    public void testObtenerMisProductos_SinSesion() {
        Response response = hortofruticolaResource.obtenerProductosDeUsuario(null);

        assertEquals(401, response.getStatus());
        assertEquals("No hay sesión activa", response.getEntity());
    }

    @Test
    public void testObtenerMisProductos_ConSesion() {
        Usuario user = mock(Usuario.class);
        when(user.getId_usuario()).thenReturn(77L);
        when(daoComercio.getComercioPorCorreo("hortofruticola@local")).thenReturn(user);
        when(daoProducto.getAllByUsuario(77L)).thenReturn(List.of(hortofruticola));
        hortofruticolaResource.setDaoComercio(daoComercio);
        hortofruticolaResource.setDaoProducto(daoProducto);
        Response response = hortofruticolaResource.obtenerProductosDeUsuario("hortofruticola@local");

        assertEquals(200, response.getStatus());
        verify(daoProducto).getAllByUsuario(77L);
    }
}

