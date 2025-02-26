package services;

import data.ComercioDetails;

import java.util.Collection;
import java.util.List;

public interface ComercioDao {


    String getTipo(String correo);


    ComercioDetails getComercio(String correo);



    List<ComercioDetails> getComercios();


    ComercioDetails loadComercioByUsername(String correo, String password);


    Collection<ComercioDetails> listAllComercios();



}
