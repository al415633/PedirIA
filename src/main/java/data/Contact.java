package data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.*;

import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@NamedQueries({
        @NamedQuery(name="Contact.findAll", query = "SELECT c FROM Contact c"),
        @NamedQuery(name = "Contact.findByNif", query = "SELECT c FROM Contact c WHERE c.nif = :nif")
})
public class Contact {
    String name;
    String surname;
    @Id
    String nif;
    @OneToOne
    @JoinColumn(name = "postal_address_id")
    PostalAddress postalAddress;

    @Transient
    public static final Contact NOT_FOUND = new Contact("Not found", "", "", PostalAddress.NO_ADDRESS);

    public Contact() {
        super();
    }

    public Contact(String name, String surname, String nif, PostalAddress postalAddress) {
        this.name = name;
        this.surname = surname;
        this.nif = nif;
        this.postalAddress = postalAddress;
    }

    public String getNIF() {
        return nif;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(name, contact.name) && Objects.equals(surname, contact.surname) && nif.equals(contact.nif);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, nif);
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", nif='" + nif + '\'' +
                '}';
    }

    public void update(Contact contact) {
        name = contact.name;
        surname = contact.surname;
        nif = contact.nif;
        postalAddress = contact.postalAddress;
    }
}