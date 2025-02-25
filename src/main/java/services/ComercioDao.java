package services;

import data.ComercioDetails;

import java.util.Collection;
import java.util.List;

public interface ComercioDao {


    String getTipo(String nombre);


    ComercioDetails getComercio(String nombre);




    List<ComercioDetails> getComercios();


    ComercioDetails loadComercioByUsername(String username, String password);


    Collection<ComercioDetails> listAllComercios();



}
