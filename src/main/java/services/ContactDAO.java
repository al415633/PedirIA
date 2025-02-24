package services;

import data.Contact;

import java.util.Collection;

public interface ContactDAO {
    Contact create(Contact contact);
    Contact retrieve(String nif);
    Contact update(Contact contact);
    Contact delete(String nif);
    Collection<Contact> getContacts();
}
