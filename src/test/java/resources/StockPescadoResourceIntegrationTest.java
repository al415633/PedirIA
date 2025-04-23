package resources;

import data.ComercioDetails;
import data.Usuario;
import data.pescaderia.HistoricoPescado;
import data.pescaderia.StockPescado;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import resources.pescado.StockPescadoResource;
import services.ComercioDao;
import services.HistoricoProductoDAO;
import services.StockProductoDAO;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockPescadoResourceIntegrationTest {

    @Mock
    ComercioDao daoComercio;
    @Mock
    StockProductoDAO<StockPescado> stockPescadoDAO;
    @Mock
    HistoricoProductoDAO<HistoricoPescado> daoHistoricoProducto;
    StockPescadoResource stockPescadoResource;

//    private Pescado pescado;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        stockPescadoResource = new StockPescadoResource();
//        pescado = new Pescado();
//        pescado.setId(1L);
//        pescado.setNombre("Pechuga de Pollo");
//        pescado.setUnidad("kg");
//        pescado.setIdNegocio(10);
//        Mockito.reset();
//        MockitoAnnotations.openMocks(this);

    }


    @Test
    public void testObtenerTodos() {
        System.out.println("Empieza");
        List<StockPescado> lista = List.of(new StockPescado());
        System.out.println("lista es: " + lista);
        when(stockPescadoDAO.getAll()).thenReturn(lista);
        stockPescadoResource.setStockDAO(stockPescadoDAO);
        stockPescadoResource.setHistoricoDAO(daoHistoricoProducto);
        stockPescadoResource.setDaoComercio(daoComercio);
        List<StockPescado> result = stockPescadoResource.obtenerTodos();
        assertEquals(1, result.size());
    }

    @Test
    public void testObtenerPorId_Encontrado() {
        StockPescado stock = new StockPescado();
        when(stockPescadoDAO.retrieve(1L)).thenReturn(stock);
        stockPescadoResource.setStockDAO(stockPescadoDAO);
        stockPescadoResource.setHistoricoDAO(daoHistoricoProducto);
        stockPescadoResource.setDaoComercio(daoComercio);
        Response response = stockPescadoResource.obtenerPorId(1L);
        assertEquals(200, response.getStatus());
        assertEquals(stock, response.getEntity());
    }

    @Test
    public void testObtenerPorId_NoEncontrado() {
        when(stockPescadoDAO.retrieve(1L)).thenReturn(null);
        stockPescadoResource.setStockDAO(stockPescadoDAO);
        stockPescadoResource.setHistoricoDAO(daoHistoricoProducto);
        stockPescadoResource.setDaoComercio(daoComercio);

        Response response = stockPescadoResource.obtenerPorId(1L);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testObtenerPorIdProducto() {
        List<StockPescado> lista = List.of(new StockPescado());
        when(stockPescadoDAO.retrieveByProducto(5L)).thenReturn(lista);
        stockPescadoResource.setStockDAO(stockPescadoDAO);
        stockPescadoResource.setHistoricoDAO(daoHistoricoProducto);
        stockPescadoResource.setDaoComercio(daoComercio);

        List<StockPescado> result = stockPescadoResource.obtenerPorIdProducto(5L);
        assertEquals(1, result.size());
    }

    @Test
    public void testAgregarStock() {
        StockPescado stock = new StockPescado();
        when(stockPescadoDAO.agregarStock(stock)).thenReturn(stock);
        stockPescadoResource.setStockDAO(stockPescadoDAO);
        stockPescadoResource.setHistoricoDAO(daoHistoricoProducto);
        stockPescadoResource.setDaoComercio(daoComercio);

        Response response = stockPescadoResource.agregarStock(stock);
        assertEquals(201, response.getStatus());
        assertEquals(stock, response.getEntity());
    }

    @Test
    public void testActualizarStock_Existente() {
        StockPescado stock = new StockPescado();
        when(stockPescadoDAO.retrieve(2L)).thenReturn(stock);
        stockPescadoResource.setStockDAO(stockPescadoDAO);
        stockPescadoResource.setHistoricoDAO(daoHistoricoProducto);
        stockPescadoResource.setDaoComercio(daoComercio);

        Response response = stockPescadoResource.actualizarStock(2L, stock);
        assertEquals(200, response.getStatus());
        verify(stockPescadoDAO).actualizarStock(stock);
    }

    @Test
    public void testActualizarStock_NoExistente() {
        when(stockPescadoDAO.retrieve(2L)).thenReturn(null);
        stockPescadoResource.setStockDAO(stockPescadoDAO);
        stockPescadoResource.setHistoricoDAO(daoHistoricoProducto);
        stockPescadoResource.setDaoComercio(daoComercio);

        Response response = stockPescadoResource.actualizarStock(2L, new StockPescado());
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testEliminarStock_Existente() {
        when(stockPescadoDAO.eliminarStock(3L)).thenReturn(new StockPescado());
        stockPescadoResource.setStockDAO(stockPescadoDAO);
        stockPescadoResource.setHistoricoDAO(daoHistoricoProducto);
        stockPescadoResource.setDaoComercio(daoComercio);

        Response response = stockPescadoResource.eliminarStock(3L);
        assertEquals(204, response.getStatus());
    }

    @Test
    public void testEliminarStock_NoExistente() {
        when(stockPescadoDAO.eliminarStock(3L)).thenReturn(null);
        stockPescadoResource.setStockDAO(stockPescadoDAO);
        stockPescadoResource.setHistoricoDAO(daoHistoricoProducto);
        stockPescadoResource.setDaoComercio(daoComercio);

        Response response = stockPescadoResource.eliminarStock(3L);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testVenderStock_OK() throws Exception {
        StockPescado stock = new StockPescado();
        when(stockPescadoDAO.retrieve(4L)).thenReturn(stock);
        stockPescadoResource.setStockDAO(stockPescadoDAO);
        stockPescadoResource.setHistoricoDAO(daoHistoricoProducto);
        stockPescadoResource.setDaoComercio(daoComercio);

        Response response = stockPescadoResource.venderStock(4L, new BigDecimal("1.0"));
        assertEquals(200, response.getStatus());
        verify(daoHistoricoProducto).addHistorico(stock, new BigDecimal("1.0"));
    }

    @Test
    public void testVenderStock_Exception() {
        doThrow(new RuntimeException()).when(stockPescadoDAO).venderStock(anyLong(), any());
        stockPescadoResource.setStockDAO(stockPescadoDAO);
        stockPescadoResource.setHistoricoDAO(daoHistoricoProducto);
        stockPescadoResource.setDaoComercio(daoComercio);

        Response response = stockPescadoResource.venderStock(4L, new BigDecimal("1.0"));
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testObtenerHistorico() {
        List<HistoricoPescado> historial = List.of(new HistoricoPescado());
        when(daoHistoricoProducto.obtenerHistorialPorProducto(1L, "HistoricoPescado")).thenReturn(historial);
        stockPescadoResource.setStockDAO(stockPescadoDAO);
        stockPescadoResource.setHistoricoDAO(daoHistoricoProducto);
        stockPescadoResource.setDaoComercio(daoComercio);

        Response response = stockPescadoResource.obtenerHistorico(1L);
        assertEquals(200, response.getStatus());
        assertEquals(historial, response.getEntity());
    }

    @Test
    public void testObtenerPrediccion_OK() {
        Usuario usuario = new Usuario();
        ComercioDetails negocio = new ComercioDetails();
        negocio.setIdNegocio(10L);
        usuario.setNegocio(negocio);

        when(daoComercio.getComercioPorCorreo("correo@test.com")).thenReturn(usuario);
        when(stockPescadoDAO.getPrediction(10L, daoHistoricoProducto)).thenReturn("\"Buena predicción\"");
        stockPescadoResource.setStockDAO(stockPescadoDAO);
        stockPescadoResource.setHistoricoDAO(daoHistoricoProducto);
        stockPescadoResource.setDaoComercio(daoComercio);

        Response response = stockPescadoResource.obtenerPrediccion("correo@test.com");
        assertEquals(200, response.getStatus());
        assertTrue(response.getEntity().toString().contains("Buena predicción"));
    }

    @Test
    public void testObtenerPrediccion_Error() {
        when(daoComercio.getComercioPorCorreo(anyString())).thenThrow(new RuntimeException());
        stockPescadoResource.setStockDAO(stockPescadoDAO);
        stockPescadoResource.setHistoricoDAO(daoHistoricoProducto);
        stockPescadoResource.setDaoComercio(daoComercio);

        Response response = stockPescadoResource.obtenerPrediccion("correo@test.com");
        assertEquals(500, response.getStatus());
    }

}

