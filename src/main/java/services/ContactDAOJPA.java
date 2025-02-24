package services;

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
public class ContactDAOJPA implements ContactDAO {
    @Inject
    EntityManager em;

    @Override
    @Transactional
    public Contact create(Contact contact) {
        // Verificar si ya existe un contacto con el mismo NIF
        Contact existingContact = em.find(Contact.class, contact.getNIF());

        if (existingContact != null) {
            // Si ya existe un contacto con el mismo NIF, devuelve Contact.NOT_FOUND (o puedes devolver null o lanzar una excepción)
            return Contact.NOT_FOUND;
        }

        // Si no existe, persiste el nuevo contacto en la base de datos
        em.persist(contact);
        return contact;
    }


    @Override
    public Contact retrieve(String nif) {
        Contact contact = em.find(Contact.class, nif);  // Busca el contacto por NIF
        // Si no se encuentra, devuelve Contact.NOT_FOUND
        return Objects.requireNonNullElse(contact, Contact.NOT_FOUND);
    }


    @Override
    @Transactional
    public Contact update(Contact contact) {
        Contact found = em.find(Contact.class, contact.getNIF());
        if (found == null) {  // Si no se encuentra, devuelve Contact.NOT_FOUND
            return Contact.NOT_FOUND;
        }
        found.update(contact);  // Si se encuentra, actualiza el contacto
        return found;
    }


    @Override
    @Transactional
    public Contact delete(String nif) {
        Contact found = em.find(Contact.class, nif);
        if (found == null) {  // Si no se encuentra, devuelve Contact.NOT_FOUND
            return Contact.NOT_FOUND;
        }
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
