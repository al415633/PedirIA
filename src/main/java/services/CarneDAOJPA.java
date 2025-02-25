package services;

import data.Carne;
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
public class CarneDAOJPA {
    @Inject
    EntityManager em;

    @Transactional
    public Carne create(Carne carne) {
        em.persist(carne);
        return carne;
    }

    public Carne retrieve(Long id) {
        return Objects.requireNonNullElse(em.find(Carne.class, id), null);
    }

    @Transactional
    public Carne update(Carne carne) {
        if (em.find(Carne.class, carne.getId()) == null) {
            return null;
        }
        return em.merge(carne);
    }

    @Transactional
    public Carne delete(Long id) {
        Carne found = em.find(Carne.class, id);
        if (found == null) {
            return null;
        }
        em.remove(found);
        return found;
    }

    public Collection<Carne> getAll() {
        TypedQuery<Carne> query = em.createQuery("SELECT c FROM Carne c", Carne.class);
        List<Carne> result = query.getResultList();
        return result != null ? result : new ArrayList<>();
    }
}
