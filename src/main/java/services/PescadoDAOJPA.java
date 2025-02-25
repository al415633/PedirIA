package services;

import data.Pescado;
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
public class PescadoDAOJPA {
    @Inject
    EntityManager em;

    @Transactional
    public Pescado create(Pescado pescado) {
        if (em.find(Pescado.class, pescado.getId()) != null) {
            return null;
        }
        em.persist(pescado);
        return pescado;
    }

    public Pescado retrieve(Long id) {
        return Objects.requireNonNullElse(em.find(Pescado.class, id), null);
    }

    @Transactional
    public Pescado update(Pescado pescado) {
        if (em.find(Pescado.class, pescado.getId()) == null) {
            return null;
        }
        return em.merge(pescado);
    }

    @Transactional
    public Pescado delete(Long id) {
        Pescado found = em.find(Pescado.class, id);
        if (found == null) {
            return null;
        }
        em.remove(found);
        return found;
    }

    public Collection<Pescado> getAll() {
        TypedQuery<Pescado> query = em.createQuery("SELECT p FROM Pescado p", Pescado.class);
        List<Pescado> result = query.getResultList();
        return result != null ? result : new ArrayList<>();
    }
}
