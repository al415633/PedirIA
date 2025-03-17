package services;

import data.ComercioDetails;
import data.Usuario;

import java.util.List;

public interface ComercioDao {


    List<Usuario> getComercios();

    Usuario getComercioPorCorreo(String correo);

    boolean existeCorreo(String correo);

    Usuario crearNegocio(Usuario usuario, ComercioDetails negocio);

    boolean actualizarNegocio(Usuario usuario, ComercioDetails negocio);

    boolean eliminarNegocio(String correo);
    Usuario verificarCredenciales(String correo, String password);

}
