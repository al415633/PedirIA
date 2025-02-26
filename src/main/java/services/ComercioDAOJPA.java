package services;

import data.ComercioDetails;
import data.Contact;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@ApplicationScoped

public class ComercioDAOJPA implements ComercioDao {


    @Inject
    EntityManager em;

    @Override
    public String getTipo(String correo) {
        ComercioDetails comercioDetails = em.find(ComercioDetails.class, correo);  // Busca el contacto por correo
        // Si no se encuentra, devuelve Contact.NOT_FOUND
        return Objects.requireNonNullElse(comercioDetails.getTipo(), null);
    }

    @Override
    public ComercioDetails getComercio(String correo) {
        ComercioDetails comercioDetails = em.find(ComercioDetails.class, correo);  // Busca el contacto por correo
        // Si no se encuentra, devuelve Contact.NOT_FOUND
        return Objects.requireNonNullElse(comercioDetails, null);
    }

    @Override
    public List<ComercioDetails> getComercios() {
        TypedQuery<ComercioDetails> query = em.createNamedQuery("Comercio.findAll", ComercioDetails.class);
        List<ComercioDetails> result = query.getResultList();
        if (result != null) return result;
        else return new ArrayList<>();
    }

    @Override
    public ComercioDetails loadComercioByUsername(String correo, String password) {
        ComercioDetails comercioDetails = em.find(ComercioDetails.class, correo);  // Busca el comercio por correo

        if (comercioDetails != null && comercioDetails.getPassword().equals(password)) {
            return comercioDetails;  // Retorna si la contraseña es correcta
        }

        return null;  // Retorna null si no encuentra el comercio o si la contraseña es incorrecta
    }


    @Override
    public Collection<ComercioDetails> listAllComercios() {
        return List.of();
    }
}

