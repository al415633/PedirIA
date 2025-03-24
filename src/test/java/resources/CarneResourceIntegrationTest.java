package resources;

import data.carniceria.Carne;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import resources.carne.CarneResource;
import services.ComercioDao;
import services.ProductoDAO;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarneResourceIntegrationTest {

    @Mock
    ProductoDAO<Carne> daoProducto;

    @Mock
    ComercioDao daoComercio;

    @InjectMocks
    CarneResource carneResource;

    private Carne carne;

    @BeforeEach
    void setup() {
        carne = new Carne();
        carne.setId(1L);
        carne.setNombre("Pechuga de Pollo");
        carne.setUnidad("kg");
        carne.setIdNegocio(10);
    }

    @Test
    public void testGetAllCarne() {
        List<Carne> lista = Arrays.asList(carne);
        when(daoProducto.getAll()).thenReturn(lista);

        Response response = carneResource.getAll();

        assertEquals(200, response.getStatus());
        verify(daoProducto, times(1)).getAll();
    }

    @Test
    public void testGetCarneById_Encontrado() {
        when(daoProducto.retrieve(1L)).thenReturn(carne);

        Response response = carneResource.getProducto(1L);

        assertEquals(200, response.getStatus());
        verify(daoProducto).retrieve(1L);
    }

    @Test
    public void testGetCarneById_NoEncontrado() {
        when(daoProducto.retrieve(2L)).thenReturn(null);

        Response response = carneResource.getProducto(2L);

        assertEquals(404, response.getStatus());
        assertEquals("Producto no encontrado", response.getEntity());
    }

    @Test
    public void testCreateCarne_Exitoso() {
        Carne nuevo = new Carne();
        nuevo.setNombre("Costilla");
        nuevo.setUnidad("kg");

        when(daoComercio.getComercioPorCorreo("carnicero@ejemplo.com").getId_usuario()).thenReturn(20L);
        when(daoProducto.create(any(Carne.class))).thenReturn(nuevo);

        Response response = carneResource.createProducto(nuevo, "carnicero@ejemplo.com");

        assertEquals(201, response.getStatus());
        verify(daoProducto).create(any(Carne.class));
    }

    @Test
    public void testCreateCarne_Conflicto() {
        when(daoComercio.getComercioPorCorreo("carnicero@ejemplo.com").getId_usuario()).thenReturn(20L);
        when(daoProducto.create(any(Carne.class))).thenReturn(null);

        Response response = carneResource.createProducto(carne, "carnicero@ejemplo.com");

        assertEquals(409, response.getStatus());
    }

    @Test
    public void testUpdateCarne_Exitoso() {
        when(daoProducto.update(carne)).thenReturn(carne);

        Response response = carneResource.updateProducto(carne);

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testUpdateCarne_NoEncontrado() {
        when(daoProducto.update(carne)).thenReturn(null);

        Response response = carneResource.updateProducto(carne);

        assertEquals(404, response.getStatus());
    }

    @Test
    public void testDeleteCarne_Exitoso() {
        when(daoProducto.delete(1L)).thenReturn(carne);

        Response response = carneResource.deleteProducto(1L);

        assertEquals(204, response.getStatus());
        verify(daoProducto).delete(1L);
    }

    @Test
    public void testDeleteCarne_NoEncontrado() {
        when(daoProducto.delete(99L)).thenReturn(null);

        Response response = carneResource.deleteProducto(99L);

        assertEquals(404, response.getStatus());
    }

    @Test
    public void testValidarCarne_Existe() {
        when(daoComercio.getComercioPorCorreo("carne@local").getId_usuario()).thenReturn(99L);
        when(daoProducto.existeProducto("Lomo", "kg", 99)).thenReturn(true);

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
        when(daoComercio.getComercioPorCorreo("carne@local").getId_usuario()).thenReturn(77L);
        when(daoProducto.getAllByUsuario(77L)).thenReturn(List.of(carne));

        Response response = carneResource.obtenerProductosDeUsuario("carne@local");

        assertEquals(200, response.getStatus());
        verify(daoProducto).getAllByUsuario(77L);
    }
}

