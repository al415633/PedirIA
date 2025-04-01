package services;

import data.AprovechanteDetails;
import data.ComercioDetails;
import data.Oferta;
import data.Usuario;

import java.util.List;

public interface OfertaDao {

    List<Oferta> getOfertas();

    Oferta getOfertaPorId(Long id_oferta);

    boolean existeOferta(Long id_oferta);

    Oferta crearOferta(Oferta oferta);

    boolean actualizarOferta(Oferta oferta);

    boolean eliminarOferta(Long id_oferta);

}
