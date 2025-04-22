package resources;

import data.Usuario;
import data.pescaderia.Pescado;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import resources.pescado.PescadoResource;
import services.ComercioDao;
import services.ProductoDAO;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PescadoResourceIntegrationTest {

//    @InjectMocks
//    ProductoDAO<Pescado> daoProducto;

//     @InjectMocks


//    @InjectMocks
//    EntityManager entityManager;


    //    @Mock
//    Usuario user;
    @Mock
    ComercioDao daoComercio;
    @Mock
    ProductoDAO<Pescado> daoProducto;
    @InjectMocks
    PescadoResource pescadoResource;
//    @InjectMocks
//        ProductoResource<Pescado> pescadoResource;

    private Pescado pescado;

    @BeforeEach
    void setup() {
        pescado = new Pescado();
        pescado.setId(1L);
        pescado.setNombre("Pechuga de Pollo");
        pescado.setUnidad("kg");
        pescado.setIdNegocio(10);
        Mockito.reset();
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testGetAllPescado() {
        List<Pescado> lista = Arrays.asList(pescado);
        when(daoProducto.getAll()).thenReturn(lista);
        pescadoResource.setDaoComercio(daoComercio);
        pescadoResource.setDaoProducto(daoProducto);
        Response response = pescadoResource.getAll();

        assertEquals(200, response.getStatus());
        verify(daoProducto, times(1)).getAll();
    }

    @Test
    public void testGetPescadoById_Encontrado() {
        when(daoProducto.retrieve(1L)).thenReturn(pescado);
        pescadoResource.setDaoComercio(daoComercio);
        pescadoResource.setDaoProducto(daoProducto);
        Response response = pescadoResource.getProducto(1L);

        assertEquals(200, response.getStatus());
        verify(daoProducto).retrieve(1L);
    }

    @Test
    public void testGetPescadoById_NoEncontrado() {
        when(daoProducto.retrieve(2L)).thenReturn(null);
        pescadoResource.setDaoComercio(daoComercio);
        pescadoResource.setDaoProducto(daoProducto);
        Response response = pescadoResource.getProducto(2L);

        assertEquals(404, response.getStatus());
        assertEquals("Producto no encontrado", response.getEntity());
    }

    @Test
    public void testCreatePescado_Exitoso() {
        Pescado nuevo = new Pescado();
        nuevo.setNombre("Costilla");
        nuevo.setUnidad("kg");
        Usuario user = mock(Usuario.class);
        when(user.getId_usuario()).thenReturn(20L);

        when(daoComercio.getComercioPorCorreo("carnicero@ejemplo.com")).thenReturn(user);
//        when(daoProducto.create(any(Pescado.class))).thenReturn(nuevo);
        when(daoProducto.create(any(Pescado.class))).thenReturn(nuevo);
        pescadoResource.setDaoComercio(daoComercio);
        pescadoResource.setDaoProducto(daoProducto);

        Response response = pescadoResource.createProducto(nuevo, "carnicero@ejemplo.com");

        assertEquals(201, response.getStatus());
//        verify(daoProducto).create(any(Pescado.class));
    }

    @Test
    public void testCreatePescado_Conflicto() {
        Usuario user = mock(Usuario.class);
        when(user.getId_usuario()).thenReturn(20L);
        when(daoComercio.getComercioPorCorreo("carnicero@ejemplo.com")).thenReturn(user);
        when(daoProducto.create(any(Pescado.class))).thenReturn(null);
        pescadoResource.setDaoComercio(daoComercio);
        pescadoResource.setDaoProducto(daoProducto);
        Response response = pescadoResource.createProducto(pescado, "carnicero@ejemplo.com");

        assertEquals(409, response.getStatus());
    }

    @Test
    public void testUpdatePescado_Exitoso() {
        when(daoProducto.update(pescado)).thenReturn(pescado);
        pescadoResource.setDaoComercio(daoComercio);
        pescadoResource.setDaoProducto(daoProducto);
        Response response = pescadoResource.updateProducto(pescado);

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testUpdatePescado_NoEncontrado() {
        when(daoProducto.update(pescado)).thenReturn(null);
        pescadoResource.setDaoComercio(daoComercio);
        pescadoResource.setDaoProducto(daoProducto);
        Response response = pescadoResource.updateProducto(pescado);

        assertEquals(404, response.getStatus());
    }

    @Test
    public void testDeletePescado_Exitoso() {
        when(daoProducto.delete(1L)).thenReturn(pescado);
        pescadoResource.setDaoComercio(daoComercio);
        pescadoResource.setDaoProducto(daoProducto);
        Response response = pescadoResource.deleteProducto(1L);

        assertEquals(204, response.getStatus());
        verify(daoProducto).delete(1L);
    }

    @Test
    public void testDeletePescado_NoEncontrado() {
        when(daoProducto.delete(99L)).thenReturn(null);
//        when(entityManager.createNativeQuery(
//                "SELECT c." + "id_pescado" + ", c.nombre, c.unidad, c.tipo_conserva, c.id_img, " +
//                        "i.nombre AS imagenNombre, i.tipo AS imagenTipo, i.datos AS imagenDatos, " +
//                        "c.id_negocio " +
//                        "FROM " + "Pescado" + " c " +
//                        "JOIN " + "ImagenesPescados" + " i ON c.id_img = i.id_img " +
//                        "WHERE c." + "id_pescado" + " = ?",
//                "PescadoMapping"
//        )).thenReturn();
        pescadoResource.setDaoComercio(daoComercio);
        pescadoResource.setDaoProducto(daoProducto);
        Response response = pescadoResource.deleteProducto(99L);

        assertEquals(404, response.getStatus());
    }

    @Test
    public void testValidarPescado_Existe() {
        Usuario user = mock(Usuario.class);
        when(user.getId_usuario()).thenReturn(99L);
        when(daoComercio.getComercioPorCorreo("pescado@local")).thenReturn(user);
        when(daoProducto.existeProducto("Lomo", "kg", 99)).thenReturn(true);
        pescadoResource.setDaoComercio(daoComercio);
        pescadoResource.setDaoProducto(daoProducto);
        Response response = pescadoResource.validarProducto("Lomo", "kg", "pescado@local");

        assertEquals(200, response.getStatus());
        assertEquals(Collections.singletonMap("existe", true), response.getEntity());
    }

    @Test
    public void testObtenerMisProductos_SinSesion() {
        Response response = pescadoResource.obtenerProductosDeUsuario(null);

        assertEquals(401, response.getStatus());
        assertEquals("No hay sesión activa", response.getEntity());
    }

    @Test
    public void testObtenerMisProductos_ConSesion() {
        Usuario user = mock(Usuario.class);
        when(user.getId_usuario()).thenReturn(77L);
        when(daoComercio.getComercioPorCorreo("pescado@local")).thenReturn(user);
        when(daoProducto.getAllByUsuario(77L)).thenReturn(List.of(pescado));
        pescadoResource.setDaoComercio(daoComercio);
        pescadoResource.setDaoProducto(daoProducto);
        Response response = pescadoResource.obtenerProductosDeUsuario("pescado@local");

        assertEquals(200, response.getStatus());
        verify(daoProducto).getAllByUsuario(77L);
    }
}

