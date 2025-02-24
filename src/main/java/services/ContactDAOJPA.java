package services;

import data.Contact;
import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class ContactDAOJPA implements ContactDAO {
    @Inject
    @PersistenceUnit("production")
    EntityManager em;

    @Override
    @Transactional
    public Contact create(Contact contact) {
        em.persist(contact);
        return contact;
    }

    @Override
    public Contact retrieve(String nif) {
        return em.find(Contact.class, nif);
    }

    @Override
    @Transactional
    public Contact update(Contact contact) {
        Contact found = em.find(Contact.class, contact.getNIF());
        found.update(contact);
        return found;
    }

    @Override
    @Transactional
    public Contact delete(String nif) {
        Contact found = em.find(Contact.class, nif);
        em.remove(found);
        return found;
    }

    @Override
    public Collection<Contact> getContacts() {
        TypedQuery<Contact> query = em.createNamedQuery("Contact.findAll", Contact.class);
        List<Contact> result = query.getResultList();
        if (result != null) return result;
        else return new ArrayList<>();
    }

}
