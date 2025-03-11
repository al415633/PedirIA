package services;

import data.AprovechanteDetails;
import data.ComercioDetails;
import data.Usuario;

import java.util.List;

public interface AprovechanteDao {

    List<Usuario> getAprovechantes();

    Usuario getAprovechantePorCorreo(String correo);

    boolean existeCorreo(String correo);

    Usuario crearAprovechante(Usuario usuario, AprovechanteDetails aprovechante);

    boolean actualizarAprovechante(Usuario usuario, AprovechanteDetails aprovechante);

    boolean eliminarAprovechante(String correo);
    Usuario verificarCredenciales(String correo, String password);


}
