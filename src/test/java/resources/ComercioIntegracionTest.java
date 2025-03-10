package resources;

import data.Usuario;
import data.ComercioDetails;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import services.ComercioDao;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class ComercioIntegracionTest {

    @Mock
    ComercioDao dao;  // Simulamos el DAO para no depender de la BD real

    @InjectMocks
    ComercioResource comercioResource;  // Clase a probar

    private Usuario usuario;
    private ComercioDetails comercio;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setCorreo("test@ejemplo.com");
        usuario.setPassword(BcryptUtil.bcryptHash("passwordCorrecto"));
        usuario.setTipo("tienda");

        comercio = new ComercioDetails();
        comercio.setNombre("Tienda Ejemplo");
        comercio.setDiaCompraDeStock("Lunes");
        comercio.setUsuario(usuario);
    }


    @Test
    public void testGetComercioPorCorreo_Encontrado() {
        when(dao.getComercioPorCorreo("test@ejemplo.com")).thenReturn(usuario);

        Response response = comercioResource.getComercio("test@ejemplo.com");
        assertEquals(200, response.getStatus());
        verify(dao, times(1)).getComercioPorCorreo("test@ejemplo.com");
    }

    @Test
    public void testGetComercioPorCorreo_NoEncontrado() {
        when(dao.getComercioPorCorreo("noexiste@ejemplo.com")).thenReturn(null);

        Response response = comercioResource.getComercio("noexiste@ejemplo.com");
        assertEquals(404, response.getStatus());
    }


    @Test
    public void testCreateComercio_CorreoExistente() throws Exception {
        when(dao.existeCorreo("test@ejemplo.com")).thenReturn(true);

        Response response = comercioResource.createComercio(
                "test@ejemplo.com", "passwordCorrecto", "tienda",
                "Tienda Ejemplo", "Lunes"
        );

        assertEquals(409, response.getStatus());
    }

    @Test
    public void testLoginExitoso() {
        when(dao.verificarCredenciales("test@ejemplo.com", "passwordCorrecto")).thenReturn(usuario);

        Response response = comercioResource.login("test@ejemplo.com", "passwordCorrecto");

        assertEquals(200, response.getStatus());
        verify(dao, times(1)).verificarCredenciales("test@ejemplo.com", "passwordCorrecto");
    }

    @Test
    public void testLoginFallido() {
        when(dao.verificarCredenciales("test@ejemplo.com", "passwordIncorrecto")).thenReturn(null);

        Response response = comercioResource.login("test@ejemplo.com", "passwordIncorrecto");

        assertEquals(401, response.getStatus());
    }
}
