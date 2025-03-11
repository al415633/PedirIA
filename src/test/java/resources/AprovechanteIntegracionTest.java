package resources;

import data.Usuario;
import data.AprovechanteDetails;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import services.AprovechanteDao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AprovechanteIntegracionTest {

    @Mock
    AprovechanteDao dao;

    @InjectMocks
    AprovechanteResource aprovechanteResource;

    private Usuario usuario;
    private AprovechanteDetails aprovechante;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setCorreo("test@ejemplo.com");
        usuario.setPassword(BcryptUtil.bcryptHash("passwordCorrecto"));
        usuario.setTipo("beneficiario");

        aprovechante = new AprovechanteDetails();
        aprovechante.setCondiciones("Aceptadas");
        aprovechante.setCondiciones2("Aceptadas");
        aprovechante.setUsuario(usuario);
    }

    @Test
    public void testGetAprovechantePorCorreo_Encontrado() {
        when(dao.getAprovechantePorCorreo("test@ejemplo.com")).thenReturn(usuario);

        Response response = aprovechanteResource.getAprovechante("test@ejemplo.com");
        assertEquals(200, response.getStatus());
        verify(dao, times(1)).getAprovechantePorCorreo("test@ejemplo.com");
    }

    @Test
    public void testGetAprovechantePorCorreo_NoEncontrado() {
        when(dao.getAprovechantePorCorreo("noexiste@ejemplo.com")).thenReturn(null);

        Response response = aprovechanteResource.getAprovechante("noexiste@ejemplo.com");
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testCreateAprovechante_CorreoExistente() throws Exception {
        when(dao.existeCorreo("test@ejemplo.com")).thenReturn(true);

        Response response = aprovechanteResource.createAprovechante(
                "test@ejemplo.com", "passwordCorrecto", "beneficiario",
                "Aceptadas", "Aceptadas"
        );

        assertEquals(409, response.getStatus());
    }

    @Test
    public void testLoginExitoso() {
        when(dao.verificarCredenciales("test@ejemplo.com", "passwordCorrecto")).thenReturn(usuario);

        Response response = aprovechanteResource.login("test@ejemplo.com", "passwordCorrecto");

        assertEquals(200, response.getStatus());
        verify(dao, times(1)).verificarCredenciales("test@ejemplo.com", "passwordCorrecto");
    }

    @Test
    public void testLoginFallido() {
        when(dao.verificarCredenciales("test@ejemplo.com", "passwordIncorrecto")).thenReturn(null);

        Response response = aprovechanteResource.login("test@ejemplo.com", "passwordIncorrecto");

        assertEquals(401, response.getStatus());
    }
}
