package services;

import data.Oferta;
import data.Usuario;
import data.ComercioDetails;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class OfertaDAOJPA implements OfertaDao {

    @PersistenceContext
    EntityManager em;

    @Override
    public List<Oferta> getOfertas() {
        return em.createQuery("SELECT o FROM Oferta o", Oferta.class).getResultList();
    }

    public Oferta getOfertaPorId(Long id_oferta) {
        try {
            return em.createQuery("SELECT o FROM Oferta o WHERE o.id_oferta = :id_oferta", Oferta.class)
                    .setParameter("id_oferta", id_oferta)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean existeOferta(Long id_oferta) {
        return getOfertaPorId(id_oferta) != null;
    }

    @Transactional
    public Oferta crearOferta(Oferta oferta) {
        em.persist(oferta);
        em.flush();



        return oferta;
    }

    @Transactional
    public boolean actualizarOferta(Oferta oferta) {
        Oferta ofertaExistente = getOfertaPorId(oferta.getId_oferta());
        if (ofertaExistente == null) {
            return false;
        }

        ofertaExistente.setLugar(oferta.getLugar());
        ofertaExistente.setId_aprovechante(oferta.getId_aprovechante());
        ofertaExistente.setId_negocio(oferta.getId_negocio());
        ofertaExistente.setFecha_alta(oferta.getFecha_alta());
        ofertaExistente.setFecha_baja(oferta.getFecha_baja());
        ofertaExistente.setFecha_vencimiento(oferta.getFecha_vencimiento());
        ofertaExistente.setCantidad(oferta.getCantidad());
        ofertaExistente.setId_producto(oferta.getId_producto());
        ofertaExistente.setTipo_producto(oferta.getTipo_producto());

        return true;
    }


    @Transactional
    public boolean eliminarOferta(Long id_oferta) {
        Oferta oferta = getOfertaPorId(id_oferta);
        if (oferta == null) return false;

        em.remove(oferta);
        return true;
    }

}
