package resources;

import data.ComercioDetails;
import data.Usuario;
import data.carniceria.Carne;
import data.carniceria.HistoricoCarne;
import data.carniceria.StockCarne;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import resources.carne.StockCarneResource;
import services.ComercioDao;
import services.HistoricoProductoDAO;
import services.StockProductoDAO;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockCarneResourceIntegrationTest {

    @Mock
    ComercioDao daoComercio;
    @Mock
    StockProductoDAO<StockCarne> stockCarneDAO;
    @Mock
    HistoricoProductoDAO<HistoricoCarne> daoHistoricoProducto;
    StockCarneResource stockCarneResource;

//    private Carne carne;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        stockCarneResource = new StockCarneResource();
//        carne = new Carne();
//        carne.setId(1L);
//        carne.setNombre("Pechuga de Pollo");
//        carne.setUnidad("kg");
//        carne.setIdNegocio(10);
//        Mockito.reset();
//        MockitoAnnotations.openMocks(this);

    }


    @Test
    public void testObtenerTodos() {
        System.out.println("Empieza");
        List<StockCarne> lista = List.of(new StockCarne());
        System.out.println("lista es: " + lista);
        when(stockCarneDAO.getAll()).thenReturn(lista);
        stockCarneResource.setStockDAO(stockCarneDAO);
        stockCarneResource.setHistoricoDAO(daoHistoricoProducto);
        stockCarneResource.setDaoComercio(daoComercio);
        List<StockCarne> result = stockCarneResource.obtenerTodos();
        assertEquals(1, result.size());
    }

    @Test
    public void testObtenerPorId_Encontrado() {
        StockCarne stock = new StockCarne();
        when(stockCarneDAO.retrieve(1L)).thenReturn(stock);
        stockCarneResource.setStockDAO(stockCarneDAO);
        stockCarneResource.setHistoricoDAO(daoHistoricoProducto);
        stockCarneResource.setDaoComercio(daoComercio);
        Response response = stockCarneResource.obtenerPorId(1L);
        assertEquals(200, response.getStatus());
        assertEquals(stock, response.getEntity());
    }

    @Test
    public void testObtenerPorId_NoEncontrado() {
        when(stockCarneDAO.retrieve(1L)).thenReturn(null);
        stockCarneResource.setStockDAO(stockCarneDAO);
        stockCarneResource.setHistoricoDAO(daoHistoricoProducto);
        stockCarneResource.setDaoComercio(daoComercio);

        Response response = stockCarneResource.obtenerPorId(1L);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testObtenerPorIdProducto() {
        List<StockCarne> lista = List.of(new StockCarne());
        when(stockCarneDAO.retrieveByProducto(5L)).thenReturn(lista);
        stockCarneResource.setStockDAO(stockCarneDAO);
        stockCarneResource.setHistoricoDAO(daoHistoricoProducto);
        stockCarneResource.setDaoComercio(daoComercio);

        List<StockCarne> result = stockCarneResource.obtenerPorIdProducto(5L);
        assertEquals(1, result.size());
    }

    @Test
    public void testAgregarStock() {
        StockCarne stock = new StockCarne();
        when(stockCarneDAO.agregarStock(stock)).thenReturn(stock);
        stockCarneResource.setStockDAO(stockCarneDAO);
        stockCarneResource.setHistoricoDAO(daoHistoricoProducto);
        stockCarneResource.setDaoComercio(daoComercio);

        Response response = stockCarneResource.agregarStock(stock);
        assertEquals(201, response.getStatus());
        assertEquals(stock, response.getEntity());
    }

    @Test
    public void testActualizarStock_Existente() {
        StockCarne stock = new StockCarne();
        when(stockCarneDAO.retrieve(2L)).thenReturn(stock);
        stockCarneResource.setStockDAO(stockCarneDAO);
        stockCarneResource.setHistoricoDAO(daoHistoricoProducto);
        stockCarneResource.setDaoComercio(daoComercio);

        Response response = stockCarneResource.actualizarStock(2L, stock);
        assertEquals(200, response.getStatus());
        verify(stockCarneDAO).actualizarStock(stock);
    }

    @Test
    public void testActualizarStock_NoExistente() {
        when(stockCarneDAO.retrieve(2L)).thenReturn(null);
        stockCarneResource.setStockDAO(stockCarneDAO);
        stockCarneResource.setHistoricoDAO(daoHistoricoProducto);
        stockCarneResource.setDaoComercio(daoComercio);

        Response response = stockCarneResource.actualizarStock(2L, new StockCarne());
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testEliminarStock_Existente() {
        when(stockCarneDAO.eliminarStock(3L)).thenReturn(new StockCarne());
        stockCarneResource.setStockDAO(stockCarneDAO);
        stockCarneResource.setHistoricoDAO(daoHistoricoProducto);
        stockCarneResource.setDaoComercio(daoComercio);

        Response response = stockCarneResource.eliminarStock(3L);
        assertEquals(204, response.getStatus());
    }

    @Test
    public void testEliminarStock_NoExistente() {
        when(stockCarneDAO.eliminarStock(3L)).thenReturn(null);
        stockCarneResource.setStockDAO(stockCarneDAO);
        stockCarneResource.setHistoricoDAO(daoHistoricoProducto);
        stockCarneResource.setDaoComercio(daoComercio);

        Response response = stockCarneResource.eliminarStock(3L);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testVenderStock_OK() throws Exception {
        StockCarne stock = new StockCarne();
        when(stockCarneDAO.retrieve(4L)).thenReturn(stock);
        stockCarneResource.setStockDAO(stockCarneDAO);
        stockCarneResource.setHistoricoDAO(daoHistoricoProducto);
        stockCarneResource.setDaoComercio(daoComercio);

        Response response = stockCarneResource.venderStock(4L, new BigDecimal("1.0"));
        assertEquals(200, response.getStatus());
        verify(daoHistoricoProducto).addHistorico(stock, new BigDecimal("1.0"));
    }

    @Test
    public void testVenderStock_Exception() {
        doThrow(new RuntimeException()).when(stockCarneDAO).venderStock(anyLong(), any());
        stockCarneResource.setStockDAO(stockCarneDAO);
        stockCarneResource.setHistoricoDAO(daoHistoricoProducto);
        stockCarneResource.setDaoComercio(daoComercio);

        Response response = stockCarneResource.venderStock(4L, new BigDecimal("1.0"));
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testObtenerHistorico() {
        List<HistoricoCarne> historial = List.of(new HistoricoCarne());
        when(daoHistoricoProducto.obtenerHistorialPorProducto(1L, "HistoricoCarne")).thenReturn(historial);
        stockCarneResource.setStockDAO(stockCarneDAO);
        stockCarneResource.setHistoricoDAO(daoHistoricoProducto);
        stockCarneResource.setDaoComercio(daoComercio);

        Response response = stockCarneResource.obtenerHistorico(1L);
        assertEquals(200, response.getStatus());
        assertEquals(historial, response.getEntity());
    }

//    @Test
//    public void testObtenerPrediccion_OK() {
//        Usuario usuario = new Usuario();
//        ComercioDetails negocio = new ComercioDetails();
//        negocio.setIdNegocio(10L);
//        usuario.setNegocio(negocio);
//
//        when(daoComercio.getComercioPorCorreo("correo@test.com")).thenReturn(usuario);
//        when(stockCarneDAO.getPrediction(10L, daoHistoricoProducto)).thenReturn("\"Buena predicción\"");
//        stockCarneResource.setStockDAO(stockCarneDAO);
//        stockCarneResource.setHistoricoDAO(daoHistoricoProducto);
//        stockCarneResource.setDaoComercio(daoComercio);
//
//        Response response = stockCarneResource.obtenerPrediccion("correo@test.com");
//        assertEquals(200, response.getStatus());
//        assertTrue(response.getEntity().toString().contains("Buena predicción"));
//    }

    @Test
    public void testObtenerPrediccion_Error() {
        when(daoComercio.getComercioPorCorreo(anyString())).thenThrow(new RuntimeException());
        stockCarneResource.setStockDAO(stockCarneDAO);
        stockCarneResource.setHistoricoDAO(daoHistoricoProducto);
        stockCarneResource.setDaoComercio(daoComercio);

        Response response = stockCarneResource.obtenerPrediccion("correo@test.com");
        assertEquals(500, response.getStatus());
    }

}

