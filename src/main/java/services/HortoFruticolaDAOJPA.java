package services;

import data.HortoFruticola;
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
public class HortoFruticolaDAOJPA {
    @Inject
    EntityManager em;

    @Transactional
    public HortoFruticola create(HortoFruticola hortoFruticola) {
        if (em.find(HortoFruticola.class, hortoFruticola.getId()) != null) {
            return null;
        }
        em.persist(hortoFruticola);
        return hortoFruticola;
    }

    public HortoFruticola retrieve(Long id) {
        return Objects.requireNonNullElse(em.find(HortoFruticola.class, id), null);
    }

    @Transactional
    public HortoFruticola update(HortoFruticola hortoFruticola) {
        if (em.find(HortoFruticola.class, hortoFruticola.getId()) == null) {
            return null;
        }
        return em.merge(hortoFruticola);
    }

    @Transactional
    public HortoFruticola delete(Long id) {
        HortoFruticola found = em.find(HortoFruticola.class, id);
        if (found == null) {
            return null;
        }
        em.remove(found);
        return found;
    }

    public Collection<HortoFruticola> getAll() {
        TypedQuery<HortoFruticola> query = em.createQuery("SELECT h FROM HortoFruticola h", HortoFruticola.class);
        List<HortoFruticola> result = query.getResultList();
        return result != null ? result : new ArrayList<>();
    }
}

