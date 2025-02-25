package services;

import data.ComercioDetails;

import java.util.Collection;
import java.util.List;

public interface ComercioDao {


    String getTipo(String email);


    ComercioDetails getComercio(String email);



    List<ComercioDetails> getComercios();


    ComercioDetails loadComercioByUsername(String email, String password);


    Collection<ComercioDetails> listAllComercios();



}
