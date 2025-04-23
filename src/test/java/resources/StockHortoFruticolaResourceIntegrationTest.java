package resources;

import com.google.gson.Gson;
import data.ComercioDetails;
import data.Usuario;

import data.hortofruticola.HistoricoHortofruticola;
import data.hortofruticola.StockHortoFruticola;
import jakarta.ws.rs.core.Response;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import resources.hortofruticola.StockHortoFruticolaResource;
import services.ComercioDao;
import services.HistoricoProductoDAO;
import services.StockProductoDAO;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockHortoFruticolaResourceIntegrationTest {

    @Mock
    ComercioDao daoComercio;
    @Mock
    StockProductoDAO<StockHortoFruticola> stockHortoFruticolaDAO;
    @Mock
    HistoricoProductoDAO<HistoricoHortofruticola> daoHistoricoProducto;
    StockHortoFruticolaResource stockHortoFruticolaResource;

//    private HortoFruticola hortofruticola;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        stockHortoFruticolaResource = new StockHortoFruticolaResource();
//        hortofruticola = new HortoFruticola();
//        hortofruticola.setId(1L);
//        hortofruticola.setNombre("Pechuga de Pollo");
//        hortofruticola.setUnidad("kg");
//        hortofruticola.setIdNegocio(10);
//        Mockito.reset();
//        MockitoAnnotations.openMocks(this);

    }


    @Test
    public void testObtenerTodos() {
        System.out.println("Empieza");
        List<StockHortoFruticola> lista = List.of(new StockHortoFruticola());
        System.out.println("lista es: " + lista);
        when(stockHortoFruticolaDAO.getAll()).thenReturn(lista);
        stockHortoFruticolaResource.setStockDAO(stockHortoFruticolaDAO);
        stockHortoFruticolaResource.setHistoricoDAO(daoHistoricoProducto);
        stockHortoFruticolaResource.setDaoComercio(daoComercio);
        List<StockHortoFruticola> result = stockHortoFruticolaResource.obtenerTodos();
        assertEquals(1, result.size());
    }

    @Test
    public void testObtenerPorId_Encontrado() {
        StockHortoFruticola stock = new StockHortoFruticola();
        when(stockHortoFruticolaDAO.retrieve(1L)).thenReturn(stock);
        stockHortoFruticolaResource.setStockDAO(stockHortoFruticolaDAO);
        stockHortoFruticolaResource.setHistoricoDAO(daoHistoricoProducto);
        stockHortoFruticolaResource.setDaoComercio(daoComercio);
        Response response = stockHortoFruticolaResource.obtenerPorId(1L);
        assertEquals(200, response.getStatus());
        assertEquals(stock, response.getEntity());
    }

    @Test
    public void testObtenerPorId_NoEncontrado() {
        when(stockHortoFruticolaDAO.retrieve(1L)).thenReturn(null);
        stockHortoFruticolaResource.setStockDAO(stockHortoFruticolaDAO);
        stockHortoFruticolaResource.setHistoricoDAO(daoHistoricoProducto);
        stockHortoFruticolaResource.setDaoComercio(daoComercio);

        Response response = stockHortoFruticolaResource.obtenerPorId(1L);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testObtenerPorIdProducto() {
        List<StockHortoFruticola> lista = List.of(new StockHortoFruticola());
        when(stockHortoFruticolaDAO.retrieveByProducto(5L)).thenReturn(lista);
        stockHortoFruticolaResource.setStockDAO(stockHortoFruticolaDAO);
        stockHortoFruticolaResource.setHistoricoDAO(daoHistoricoProducto);
        stockHortoFruticolaResource.setDaoComercio(daoComercio);

        List<StockHortoFruticola> result = stockHortoFruticolaResource.obtenerPorIdProducto(5L);
        assertEquals(1, result.size());
    }

    @Test
    public void testAgregarStock() {
        StockHortoFruticola stock = new StockHortoFruticola();
        when(stockHortoFruticolaDAO.agregarStock(stock)).thenReturn(stock);
        stockHortoFruticolaResource.setStockDAO(stockHortoFruticolaDAO);
        stockHortoFruticolaResource.setHistoricoDAO(daoHistoricoProducto);
        stockHortoFruticolaResource.setDaoComercio(daoComercio);

        Response response = stockHortoFruticolaResource.agregarStock(stock);
        assertEquals(201, response.getStatus());
        assertEquals(stock, response.getEntity());
    }

    @Test
    public void testActualizarStock_Existente() {
        StockHortoFruticola stock = new StockHortoFruticola();
        when(stockHortoFruticolaDAO.retrieve(2L)).thenReturn(stock);
        stockHortoFruticolaResource.setStockDAO(stockHortoFruticolaDAO);
        stockHortoFruticolaResource.setHistoricoDAO(daoHistoricoProducto);
        stockHortoFruticolaResource.setDaoComercio(daoComercio);

        Response response = stockHortoFruticolaResource.actualizarStock(2L, stock);
        assertEquals(200, response.getStatus());
        verify(stockHortoFruticolaDAO).actualizarStock(stock);
    }

    @Test
    public void testActualizarStock_NoExistente() {
        when(stockHortoFruticolaDAO.retrieve(2L)).thenReturn(null);
        stockHortoFruticolaResource.setStockDAO(stockHortoFruticolaDAO);
        stockHortoFruticolaResource.setHistoricoDAO(daoHistoricoProducto);
        stockHortoFruticolaResource.setDaoComercio(daoComercio);

        Response response = stockHortoFruticolaResource.actualizarStock(2L, new StockHortoFruticola());
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testEliminarStock_Existente() {
        when(stockHortoFruticolaDAO.eliminarStock(3L)).thenReturn(new StockHortoFruticola());
        stockHortoFruticolaResource.setStockDAO(stockHortoFruticolaDAO);
        stockHortoFruticolaResource.setHistoricoDAO(daoHistoricoProducto);
        stockHortoFruticolaResource.setDaoComercio(daoComercio);

        Response response = stockHortoFruticolaResource.eliminarStock(3L);
        assertEquals(204, response.getStatus());
    }

    @Test
    public void testEliminarStock_NoExistente() {
        when(stockHortoFruticolaDAO.eliminarStock(3L)).thenReturn(null);
        stockHortoFruticolaResource.setStockDAO(stockHortoFruticolaDAO);
        stockHortoFruticolaResource.setHistoricoDAO(daoHistoricoProducto);
        stockHortoFruticolaResource.setDaoComercio(daoComercio);

        Response response = stockHortoFruticolaResource.eliminarStock(3L);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testVenderStock_OK() throws Exception {
        StockHortoFruticola stock = new StockHortoFruticola();
        when(stockHortoFruticolaDAO.retrieve(4L)).thenReturn(stock);
        stockHortoFruticolaResource.setStockDAO(stockHortoFruticolaDAO);
        stockHortoFruticolaResource.setHistoricoDAO(daoHistoricoProducto);
        stockHortoFruticolaResource.setDaoComercio(daoComercio);

        Response response = stockHortoFruticolaResource.venderStock(4L, new BigDecimal("1.0"));
        assertEquals(200, response.getStatus());
        verify(daoHistoricoProducto).addHistorico(stock, new BigDecimal("1.0"));
    }

    @Test
    public void testVenderStock_Exception() {
        doThrow(new RuntimeException()).when(stockHortoFruticolaDAO).venderStock(anyLong(), any());
        stockHortoFruticolaResource.setStockDAO(stockHortoFruticolaDAO);
        stockHortoFruticolaResource.setHistoricoDAO(daoHistoricoProducto);
        stockHortoFruticolaResource.setDaoComercio(daoComercio);

        Response response = stockHortoFruticolaResource.venderStock(4L, new BigDecimal("1.0"));
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testObtenerHistorico() {
        List<HistoricoHortofruticola> historial = List.of(new HistoricoHortofruticola());
        when(daoHistoricoProducto.obtenerHistorialPorProducto(1L, "HistoricoHortofruticola")).thenReturn(historial);
        stockHortoFruticolaResource.setStockDAO(stockHortoFruticolaDAO);
        stockHortoFruticolaResource.setHistoricoDAO(daoHistoricoProducto);
        stockHortoFruticolaResource.setDaoComercio(daoComercio);

        Response response = stockHortoFruticolaResource.obtenerHistorico(1L);
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
        Gson gson = new Gson();
        Map<String,String> respuesta = new HashMap<String,String>();
        respuesta.put("message", "diccionario de respuesta desde python");
        when(stockHortoFruticolaDAO.getPrediction(10L, daoHistoricoProducto)).thenReturn(gson.fromJson(JSONObject.toJSONString(respuesta), JSONObject.class));
        stockHortoFruticolaResource.setStockDAO(stockHortoFruticolaDAO);
        stockHortoFruticolaResource.setHistoricoDAO(daoHistoricoProducto);
        stockHortoFruticolaResource.setDaoComercio(daoComercio);

        Response response = stockHortoFruticolaResource.obtenerPrediccion("correo@test.com");
        assertEquals(200, response.getStatus());
        assertTrue(response.getEntity().toString().contains("diccionario de respuesta desde python"));
    }

    @Test
    public void testObtenerPrediccion_Error() {
        when(daoComercio.getComercioPorCorreo(anyString())).thenThrow(new RuntimeException());
        stockHortoFruticolaResource.setStockDAO(stockHortoFruticolaDAO);
        stockHortoFruticolaResource.setHistoricoDAO(daoHistoricoProducto);
        stockHortoFruticolaResource.setDaoComercio(daoComercio);

        Response response = stockHortoFruticolaResource.obtenerPrediccion("correo@test.com");
        assertEquals(500, response.getStatus());
    }

}

