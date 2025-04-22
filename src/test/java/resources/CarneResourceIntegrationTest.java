package resources;

import data.Usuario;
import data.carniceria.Carne;
import jakarta.persistence.EntityManager;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import resources.carne.CarneResource;
import services.ComercioDao;
import services.ProductoDAO;
import services.carne.CarneDAO;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarneResourceIntegrationTest {

//    @InjectMocks
//    ProductoDAO<Carne> daoProducto;

//     @InjectMocks


//    @InjectMocks
//    EntityManager entityManager;


    //    @Mock
//    Usuario user;
    @Mock
    ComercioDao daoComercio;
    @Mock
    ProductoDAO<Carne> daoProducto;
    @InjectMocks
    CarneResource carneResource;
//    @InjectMocks
//        ProductoResource<Carne> carneResource;

    private Carne carne;

    @BeforeEach
    void setup() {
        carne = new Carne();
        carne.setId(1L);
        carne.setNombre("Pechuga de Pollo");
        carne.setUnidad("kg");
        carne.setIdNegocio(10);
        Mockito.reset();
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testGetAllCarne() {
        List<Carne> lista = Arrays.asList(carne);
        when(daoProducto.getAll()).thenReturn(lista);
        carneResource.setDaoComercio(daoComercio);
        carneResource.setDaoProducto(daoProducto);
        Response response = carneResource.getAll();

        assertEquals(200, response.getStatus());
        verify(daoProducto, times(1)).getAll();
    }

    @Test
    public void testGetCarneById_Encontrado() {
        when(daoProducto.retrieve(1L)).thenReturn(carne);
        carneResource.setDaoComercio(daoComercio);
        carneResource.setDaoProducto(daoProducto);
        Response response = carneResource.getProducto(1L);

        assertEquals(200, response.getStatus());
        verify(daoProducto).retrieve(1L);
    }

    @Test
    public void testGetCarneById_NoEncontrado() {
        when(daoProducto.retrieve(2L)).thenReturn(null);
        carneResource.setDaoComercio(daoComercio);
        carneResource.setDaoProducto(daoProducto);
        Response response = carneResource.getProducto(2L);

        assertEquals(404, response.getStatus());
        assertEquals("Producto no encontrado", response.getEntity());
    }

    @Test
    public void testCreateCarne_Exitoso() {
        Carne nuevo = new Carne();
        nuevo.setNombre("Costilla");
        nuevo.setUnidad("kg");
        Usuario user = mock(Usuario.class);
        when(user.getId_usuario()).thenReturn(20L);

        when(daoComercio.getComercioPorCorreo("carnicero@ejemplo.com")).thenReturn(user);
//        when(daoProducto.create(any(Carne.class))).thenReturn(nuevo);
        when(daoProducto.create(any(Carne.class))).thenReturn(nuevo);
        carneResource.setDaoComercio(daoComercio);
        carneResource.setDaoProducto(daoProducto);

        Response response = carneResource.createProducto(nuevo, "carnicero@ejemplo.com");

        assertEquals(201, response.getStatus());
//        verify(daoProducto).create(any(Carne.class));
    }

    @Test
    public void testCreateCarne_Conflicto() {
        Usuario user = mock(Usuario.class);
        when(user.getId_usuario()).thenReturn(20L);
        when(daoComercio.getComercioPorCorreo("carnicero@ejemplo.com")).thenReturn(user);
        when(daoProducto.create(any(Carne.class))).thenReturn(null);
        carneResource.setDaoComercio(daoComercio);
        carneResource.setDaoProducto(daoProducto);
        Response response = carneResource.createProducto(carne, "carnicero@ejemplo.com");

        assertEquals(409, response.getStatus());
    }

    @Test
    public void testUpdateCarne_Exitoso() {
        when(daoProducto.update(carne)).thenReturn(carne);
        carneResource.setDaoComercio(daoComercio);
        carneResource.setDaoProducto(daoProducto);
        Response response = carneResource.updateProducto(carne);

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testUpdateCarne_NoEncontrado() {
        when(daoProducto.update(carne)).thenReturn(null);
        carneResource.setDaoComercio(daoComercio);
        carneResource.setDaoProducto(daoProducto);
        Response response = carneResource.updateProducto(carne);

        assertEquals(404, response.getStatus());
    }

    @Test
    public void testDeleteCarne_Exitoso() {
        when(daoProducto.delete(1L)).thenReturn(carne);
        carneResource.setDaoComercio(daoComercio);
        carneResource.setDaoProducto(daoProducto);
        Response response = carneResource.deleteProducto(1L);

        assertEquals(204, response.getStatus());
        verify(daoProducto).delete(1L);
    }

    @Test
    public void testDeleteCarne_NoEncontrado() {
        when(daoProducto.delete(99L)).thenReturn(null);
//        when(entityManager.createNativeQuery(
//                "SELECT c." + "id_carne" + ", c.nombre, c.unidad, c.tipo_conserva, c.id_img, " +
//                        "i.nombre AS imagenNombre, i.tipo AS imagenTipo, i.datos AS imagenDatos, " +
//                        "c.id_negocio " +
//                        "FROM " + "Carne" + " c " +
//                        "JOIN " + "ImagenesCarnes" + " i ON c.id_img = i.id_img " +
//                        "WHERE c." + "id_carne" + " = ?",
//                "CarneMapping"
//        )).thenReturn();
        carneResource.setDaoComercio(daoComercio);
        carneResource.setDaoProducto(daoProducto);
        Response response = carneResource.deleteProducto(99L);

        assertEquals(404, response.getStatus());
    }

    @Test
    public void testValidarCarne_Existe() {
        Usuario user = mock(Usuario.class);
        when(user.getId_usuario()).thenReturn(99L);
        when(daoComercio.getComercioPorCorreo("carne@local")).thenReturn(user);
        when(daoProducto.existeProducto("Lomo", "kg", 99)).thenReturn(true);
        carneResource.setDaoComercio(daoComercio);
        carneResource.setDaoProducto(daoProducto);
        Response response = carneResource.validarProducto("Lomo", "kg", "carne@local");

        assertEquals(200, response.getStatus());
        assertEquals(Collections.singletonMap("existe", true), response.getEntity());
    }

    @Test
    public void testObtenerMisProductos_SinSesion() {
        Response response = carneResource.obtenerProductosDeUsuario(null);

        assertEquals(401, response.getStatus());
        assertEquals("No hay sesión activa", response.getEntity());
    }

    @Test
    public void testObtenerMisProductos_ConSesion() {
        Usuario user = mock(Usuario.class);
        when(user.getId_usuario()).thenReturn(77L);
        when(daoComercio.getComercioPorCorreo("carne@local")).thenReturn(user);
        when(daoProducto.getAllByUsuario(77L)).thenReturn(List.of(carne));
        carneResource.setDaoComercio(daoComercio);
        carneResource.setDaoProducto(daoProducto);
        Response response = carneResource.obtenerProductosDeUsuario("carne@local");

        assertEquals(200, response.getStatus());
        verify(daoProducto).getAllByUsuario(77L);
    }
}

